package io.aios.kernel.service.nodes;

import io.aios.kernel.WorkerClientService;
import io.aios.kernel.model.AgentState;
import io.aios.shared.proto.ScriptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorNode {

    private final WorkerClientService workerClientService;

    public void process(AgentState state) {
        log.info("ExecutorNode executing code...");
        state.addHistory("Executing script in Worker...");

        try {
            ScriptResponse response = workerClientService.sendTask(
                    "agent-task-" + System.currentTimeMillis(),
                    state.getCurrentSourceCode(),
                    "run",
                    Collections.emptyMap()
            ).block();

            if (response != null && response.getSuccess()) {
                log.info("Execution successful.");
                state.setSolved(true);
                state.setFinalResult(response.getResultJson());
                state.addHistory("Execution success: " + response.getResultJson());
            } else {
                String error = (response != null) ? response.getErrorMessage() : "No response from worker";
                log.error("Execution failed: {}", error);
                state.setLastError(error);
                state.setAttemptCount(state.getAttemptCount() + 1);
                state.addHistory("Execution failed: " + error);
            }
        } catch (Exception e) {
            log.error("Communication error during execution", e);
            state.setLastError(e.getMessage());
            state.setAttemptCount(state.getAttemptCount() + 1);
            state.addHistory("Communication error: " + e.getMessage());
        }
    }
}
