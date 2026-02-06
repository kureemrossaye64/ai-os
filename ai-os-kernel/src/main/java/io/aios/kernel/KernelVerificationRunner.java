package io.aios.kernel;

import io.aios.shared.proto.ScriptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class KernelVerificationRunner implements CommandLineRunner {

    private final WorkerClientService workerClientService;

    @Override
    public void run(String... args) {
        log.info("Starting Kernel verification...");

        String groovyCode =
            "// AI Generated Code Simulation\n" +
            "browser.navigate(\"https://example.com\")\n" +
            "String title = browser.extractText(\"h1\")\n" +
            "fs.write(\"result.txt\", \"Title found: \" + title)\n" +
            "return \"Scraped: \" + title";

        log.info("Sending task to Worker...");
        try {
            ScriptResponse response = workerClientService.sendTask("test-phase2", groovyCode, "run", Collections.emptyMap())
                    .block();
            if (response != null && response.getSuccess()) {
                log.info("Result received: {}", response.getResultJson());
                if (!response.getArtifactData().isEmpty()) {
                    log.info("Artifact received: {} bytes, MIME type: {}",
                        response.getArtifactData().size(), response.getArtifactMimeType());
                }
            } else {
                log.error("Execution failed: {}", response != null ? response.getErrorMessage() : "No response");
            }
        } catch (Exception e) {
            log.error("Communication error: ", e);
        }
    }
}
