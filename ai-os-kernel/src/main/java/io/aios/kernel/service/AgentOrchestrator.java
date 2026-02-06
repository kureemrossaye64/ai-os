package io.aios.kernel.service;

import io.aios.kernel.model.AgentState;
import io.aios.kernel.service.nodes.CoderNode;
import io.aios.kernel.service.nodes.ExecutorNode;
import io.aios.kernel.service.nodes.LibrarianNode;
import io.aios.kernel.service.nodes.PlannerNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgentOrchestrator {

    private final PlannerNode plannerNode;
    private final CoderNode coderNode;
    private final ExecutorNode executorNode;
    private final LibrarianNode librarianNode;

    public AgentState runGoal(String goal, Consumer<AgentState> progressCallback) {
        AgentState state = AgentState.builder()
                .userGoal(goal)
                .build();

        log.info("Starting agentic loop for goal: {}", goal);
        state.addHistory("Starting goal execution");
        progressCallback.accept(state);

        // 1. Plan
        plannerNode.process(state);
        progressCallback.accept(state);

        while (!state.isSolved() && state.getAttemptCount() < 3) {
            // 2. Code (if not already found in plan or if fixing)
            if (state.getCurrentSourceCode() == null || state.getAttemptCount() > 0) {
                coderNode.process(state);
                progressCallback.accept(state);
            }

            // 3. Execute
            executorNode.process(state);
            progressCallback.accept(state);
        }

        // 4. Save (if successful and new)
        if (state.isSolved()) {
            librarianNode.process(state);
            progressCallback.accept(state);
            log.info("Goal solved successfully.");
        } else {
            log.error("Failed to solve goal after {} attempts.", state.getAttemptCount());
            state.addHistory("Failed to solve goal within attempt limit.");
        }

        return state;
    }
}
