package io.aios.kernel.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import io.aios.kernel.service.AiArchitect;
import io.aios.kernel.service.AgentAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.context.annotation.Profile;

@Configuration
public class LangChain4jConfig {

    @Bean
    @Profile("!test")
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    @Profile("!test")
    public AiArchitect aiArchitect(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(AiArchitect.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    @Bean
    @Profile("!test")
    public AgentAiService agentAiService(ChatLanguageModel chatLanguageModel) {
        return AiServices.builder(AgentAiService.class)
                .chatLanguageModel(chatLanguageModel)
                .build();
    }

    @Bean
    @Profile("!test")
    public EmbeddingStore<TextSegment> embeddingStore(
            @Value("${spring.datasource.username}") String user,
            @Value("${spring.datasource.password}") String password) {

        // Assuming default localhost:5432/ai_os as per application.properties
        return PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("ai_os")
                .user(user)
                .password(password)
                .table("tool_embeddings")
                .dimension(384)
                .build();
    }
}
