package io.aios.kernel;

import io.aios.kernel.model.AgentState;
import io.aios.kernel.service.AgentOrchestrator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test")
public class KernelVerificationRunner implements CommandLineRunner {

    private final AgentOrchestrator orchestrator;

    @Override
    public void run(String... args) {
        log.info("Starting Kernel verification (Phase 4)...");

        String goal = "Scrape the trending repository name from github.com/trending and save it to a file named 'trending.txt'.";

        try {
            AgentState result = orchestrator.runGoal(goal, state -> {
                log.info("PROGRESS: {}", state.getExecutionHistory().get(state.getExecutionHistory().size() - 1));
            });

            if (result.isSolved()) {
                log.info("Goal successfully solved!");
                log.info("Result: {}", result.getFinalResult());
            } else {
                log.error("Agent failed to solve the goal.");
            }
        } catch (Exception e) {
            log.error("Error during verification", e);
        }
    }
}
