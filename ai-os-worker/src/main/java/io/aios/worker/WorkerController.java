package io.aios.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aios.shared.proto.ScriptRequest;
import io.aios.shared.proto.ScriptResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WorkerController {

    private final GroovyExecutionService executionService;
    private final ObjectMapper objectMapper;

    @MessageMapping("execute.groovy")
    public ScriptResponse executeGroovy(ScriptRequest request) {
        log.info("Received execution request for scriptId: {}", request.getScriptId());
        try {
            Object result = executionService.execute(
                    request.getGroovyCode(),
                    request.getMethodName(),
                    request.getArgumentsMap()
            );

            ScriptResponse.Builder builder = ScriptResponse.newBuilder().setSuccess(true);

            if (result instanceof byte[]) {
                builder.setArtifactData(com.google.protobuf.ByteString.copyFrom((byte[]) result));
                builder.setArtifactMimeType("application/octet-stream");
                builder.setResultJson("binary-data");
            } else {
                String resultJson = result instanceof String ? (String) result : objectMapper.writeValueAsString(result);
                builder.setResultJson(resultJson);
            }

            return builder.build();
        } catch (Exception e) {
            log.error("Failed to execute script", e);
            return ScriptResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build();
        }
    }
}
