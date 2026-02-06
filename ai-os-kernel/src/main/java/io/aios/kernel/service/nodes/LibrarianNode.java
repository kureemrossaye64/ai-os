package io.aios.kernel.service.nodes;

import io.aios.kernel.model.AgentState;
import io.aios.kernel.service.ToolIngestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LibrarianNode {

    private final ToolIngestionService toolIngestionService;

    public void process(AgentState state) {
        if (state.isSolved() && state.getCurrentSourceCode() != null && state.getCurrentClassName() == null) {
            log.info("LibrarianNode saving new tool.");
            state.addHistory("Saving tool to registry for future use...");

            // Derive a class name if not present
            String className = "Tool_" + System.currentTimeMillis();
            toolIngestionService.ingest(className, state.getCurrentSourceCode());
            state.setCurrentClassName(className);
        }
    }
}
