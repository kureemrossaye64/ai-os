package io.aios.kernel.controller;

import io.aios.kernel.model.AgentState;
import io.aios.kernel.service.AgentOrchestrator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final AgentOrchestrator orchestrator;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    @PostMapping("/task")
    public SseEmitter submitTask(@RequestBody TaskRequest request) {
        SseEmitter emitter = new SseEmitter(600000L); // 10 minutes timeout

        executorService.submit(() -> {
            try {
                orchestrator.runGoal(request.getGoal(), state -> {
                    try {
                        emitter.send(state);
                    } catch (IOException e) {
                        log.error("Failed to send SSE update", e);
                    }
                });
                emitter.complete();
            } catch (Exception e) {
                log.error("Error during task execution", e);
                emitter.completeWithError(e);
            }
        });

        return emitter;
    }

    @Data
    public static class TaskRequest {
        private String goal;
    }
}
