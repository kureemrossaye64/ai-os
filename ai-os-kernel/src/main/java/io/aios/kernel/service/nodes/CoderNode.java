package io.aios.kernel.service.nodes;

import io.aios.kernel.config.PromptRegistry;
import io.aios.kernel.model.AgentState;
import io.aios.kernel.service.AgentAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoderNode {

    private final AgentAiService aiService;

    public void process(AgentState state) {
        String code;
        if (state.getAttemptCount() > 0) {
            log.info("CoderNode attempting fix. Attempt: {}", state.getAttemptCount());
            state.addHistory("Attempting to fix code error...");
            code = aiService.fixCode(state.getLastError(), state.getCurrentSourceCode());
        } else {
            log.info("CoderNode generating new code.");
            state.addHistory("Generating new Groovy script...");
            String requirements = PromptRegistry.CODER_PROMPT + "\n\nGoal: " + state.getUserGoal();
            code = aiService.generateCode(requirements);
        }

        // Simple sanitization
        code = sanitizeCode(code);
        state.setCurrentSourceCode(code);
        state.setLastError(null);
    }

    private String sanitizeCode(String code) {
        return code.replaceAll("```groovy", "")
                .replaceAll("```", "")
                .trim();
    }
}
