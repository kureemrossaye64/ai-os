package io.aios.kernel.service.nodes;

import io.aios.kernel.model.AiTool;
import io.aios.kernel.model.AgentState;
import io.aios.kernel.service.ToolRetrievalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PlannerNode {

    private final ToolRetrievalService toolRetrievalService;

    public void process(AgentState state) {
        log.info("PlannerNode processing goal: {}", state.getUserGoal());
        state.addHistory("Analyzing goal for existing tools...");

        List<AiTool> existingTools = toolRetrievalService.findToolsForGoal(state.getUserGoal());

        if (!existingTools.isEmpty()) {
            AiTool bestMatch = existingTools.get(0);
            log.info("Found existing tool: {}", bestMatch.getClassName());
            state.setCurrentClassName(bestMatch.getClassName());
            state.setCurrentSourceCode(bestMatch.getSourceCode());
            state.addHistory("Reusing existing tool: " + bestMatch.getClassName());
        } else {
            log.info("No suitable tool found, moving to generation.");
            state.addHistory("No existing tool found. Preparing for generation...");
        }
    }
}
