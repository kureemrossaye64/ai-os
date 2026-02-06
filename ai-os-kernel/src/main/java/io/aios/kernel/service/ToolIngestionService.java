package io.aios.kernel.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.aios.kernel.model.AiTool;
import io.aios.kernel.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToolIngestionService {

    private final AiArchitect aiArchitect;
    private final ToolRepository toolRepository;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ObjectMapper objectMapper;

    @Transactional
    public void ingest(String className, String sourceCode) {
        log.info("Ingesting tool: {}", className);

        // Step A: Reflection
        String reflectionJson = aiArchitect.explainCode(sourceCode);
        log.info("Tool reflection: {}", reflectionJson);

        String description = reflectionJson;
        String jsonSchema = "{}";

        try {
            // LangChain4j might return markdown wrapped JSON (```json ... ```)
            String cleanJson = reflectionJson.replaceAll("```json", "").replaceAll("```", "").trim();
            JsonNode node = objectMapper.readTree(cleanJson);
            if (node.has("description")) {
                description = node.get("description").asText();
            }
            if (node.has("inputs")) {
                jsonSchema = objectMapper.writeValueAsString(node.get("inputs"));
            }
        } catch (Exception e) {
            log.warn("Failed to parse reflection JSON, using raw string: {}", e.getMessage());
        }

        // Step B: Persistence
        AiTool tool = AiTool.builder()
                .id(UUID.randomUUID())
                .className(className)
                .sourceCode(sourceCode)
                .description(description)
                .jsonSchema(jsonSchema)
                .usageCount(0)
                .isVerified(false)
                .build();
        
        toolRepository.save(tool);

        // Step C: Embedding
        String textToEmbed = String.format("Tool Name: %s\nDescription: %s", className, description);
        TextSegment segment = TextSegment.from(textToEmbed, Metadata.from("toolId", tool.getId().toString()));
        
        embeddingStore.add(embeddingModel.embed(segment).content(), segment);
        log.info("Tool ingested successfully: {}", tool.getId());
    }
}
