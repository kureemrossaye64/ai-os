package io.aios.kernel.service;

import io.aios.kernel.model.AiTool;
import io.aios.kernel.repository.ToolRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class MemoryIntegrationTest {

    @Autowired
    private ToolIngestionService toolIngestionService;

    @Autowired
    private ToolRetrievalService toolRetrievalService;

    @Autowired
    private ToolRepository toolRepository;

    @Test
    public void testFullEnrichmentAndRetrievalLoop() {
        // Step 1: Ingest a tool
        String weatherToolCode = 
            "class WeatherFetcher {\n" +
            "    String getSchema() { return '{\"city\": \"string\"}' }\n" +
            "    String fetch(Map args) {\n" +
            "        return \"The weather in \" + args.city + \" is 22 degrees.\"\n" +
            "    }\n" +
            "}";
        
        toolIngestionService.ingest("WeatherFetcher", weatherToolCode);

        // Step 2: Verification 1: Check Postgres (H2 in test) ai_tool table
        List<AiTool> tools = toolRepository.findAll();
        assertThat(tools).hasSize(1);
        AiTool savedTool = tools.get(0);
        assertThat(savedTool.getClassName()).isEqualTo("WeatherFetcher");
        assertThat(savedTool.getDescription()).isEqualTo("This tool fetches weather data for a given city.");
        assertThat(savedTool.getJsonSchema()).contains("city");
        
        System.out.println("Generated Description: " + savedTool.getDescription());
        System.out.println("Generated JSON Schema: " + savedTool.getJsonSchema());

        // Step 3: Action: Call ToolRetrievalService for semantic search
        List<AiTool> foundTools = toolRetrievalService.findToolsForGoal("I need to check the temperature outside");

        // Step 4: Verification 3: Assert that the "WeatherFetcher" tool is returned
        assertThat(foundTools).isNotEmpty();
        assertThat(foundTools.get(0).getClassName()).isEqualTo("WeatherFetcher");
    }
}
