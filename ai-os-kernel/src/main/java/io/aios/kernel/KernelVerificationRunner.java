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
            "class Test {\n" +
            "    String sayHola(Map args) {\n" +
            "        return 'Hola ' + args.name + ' from the dynamic Worker JVM!'\n" +
            "    }\n" +
            "}";

        log.info("Sending task to Worker...");
        try {
            ScriptResponse response = workerClientService.sendTask("test-2", groovyCode, "sayHola", Collections.singletonMap("name", "Kernel"))
                    .block();
            if (response != null && response.getSuccess()) {
                log.info("Result received: {}", response.getResultJson());
            } else {
                log.error("Execution failed: {}", response != null ? response.getErrorMessage() : "No response");
            }
        } catch (Exception e) {
            log.error("Communication error: ", e);
        }
    }
}
