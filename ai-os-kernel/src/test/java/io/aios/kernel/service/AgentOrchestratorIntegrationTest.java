package io.aios.kernel.service;

import io.aios.kernel.WorkerClientService;
import io.aios.kernel.model.AgentState;
import io.aios.kernel.repository.ToolRepository;
import io.aios.shared.proto.ScriptResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class AgentOrchestratorIntegrationTest {

    @Autowired
    private AgentOrchestrator orchestrator;

    @MockBean
    private AgentAiService agentAiService;

    @MockBean
    private WorkerClientService workerClientService;

    @Autowired
    private ToolRepository toolRepository;

    @Test
    public void testSelfHealingLoop() {
        // Step 1: Initial planning - No tool found
        String goal = "Scrape trending repo";
        
        // Mock Coder: First attempt returns buggy code
        when(agentAiService.generateCode(anyString())).thenReturn("buggy code");
        
        // Mock Executor: First attempt fails
        when(workerClientService.sendTask(anyString(), eq("buggy code"), anyString(), anyMap()))
                .thenReturn(Mono.just(ScriptResponse.newBuilder()
                        .setSuccess(false)
                        .setErrorMessage("Syntax Error")
                        .build()));

        // Mock Fixer: Second attempt returns fixed code
        when(agentAiService.fixCode(eq("Syntax Error"), eq("buggy code"))).thenReturn("fixed code");

        // Mock Executor: Second attempt succeeds
        when(workerClientService.sendTask(anyString(), eq("fixed code"), anyString(), anyMap()))
                .thenReturn(Mono.just(ScriptResponse.newBuilder()
                        .setSuccess(true)
                        .setResultJson("Scraped: LangChain4j")
                        .build()));

        // Run the orchestrator
        AgentState result = orchestrator.runGoal(goal, state -> {});

        // Verification
        assertThat(result.isSolved()).isTrue();
        assertThat(result.getAttemptCount()).isEqualTo(1); // One failed attempt, one retry
        assertThat(result.getFinalResult()).isEqualTo("Scraped: LangChain4j");
        assertThat(result.getCurrentSourceCode()).isEqualTo("fixed code");
        
        // Check if librarian saved it
        assertThat(toolRepository.findAll()).isNotEmpty();
    }
}
