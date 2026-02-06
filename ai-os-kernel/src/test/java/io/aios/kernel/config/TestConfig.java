package io.aios.kernel.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import io.aios.kernel.service.AiArchitect;
import io.aios.kernel.service.AgentAiService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Configuration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public EmbeddingStore<TextSegment> embeddingStore() {
        return new InMemoryEmbeddingStore<>();
    }

    @Bean
    @Primary
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @Bean
    @Primary
    public AgentAiService agentAiService() {
        AgentAiService mock = mock(AgentAiService.class);
        when(mock.generateCode(anyString())).thenReturn("return 'success'");
        return mock;
    }

    @Bean
    @Primary
    public AiArchitect aiArchitect() {
        AiArchitect mock = mock(AiArchitect.class);
        String json = "{\n" +
                "  \"description\": \"This tool fetches weather data for a given city.\",\n" +
                "  \"inputs\": {\n" +
                "    \"city\": \"string\"\n" +
                "  }\n" +
                "}";
        when(mock.explainCode(anyString())).thenReturn(json);
        return mock;
    }
}
