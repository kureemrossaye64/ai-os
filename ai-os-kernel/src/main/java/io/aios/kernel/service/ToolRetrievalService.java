package io.aios.kernel.service;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import io.aios.kernel.model.AiTool;
import io.aios.kernel.repository.ToolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ToolRetrievalService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    private final ToolRepository toolRepository;

    public List<AiTool> findToolsForGoal(String userGoal) {
        log.info("Searching for tools for goal: {}", userGoal);

        // Convert userGoal into an embedding
        var embedding = embeddingModel.embed(userGoal).content();

        // Query EmbeddingStore for top 3 matches
        List<EmbeddingMatch<TextSegment>> matches = embeddingStore.findRelevant(embedding, 3, 0.7);

        // Extract toolId from metadata and fetch full entities
        return matches.stream()
                .map(match -> {
                    String toolIdStr = match.embedded().metadata().getString("toolId");
                    return toolRepository.findById(UUID.fromString(toolIdStr)).orElse(null);
                })
                .filter(tool -> tool != null)
                .collect(Collectors.toList());
    }
}
