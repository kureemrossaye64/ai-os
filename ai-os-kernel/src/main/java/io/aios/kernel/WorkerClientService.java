package io.aios.kernel;

import io.aios.shared.proto.ScriptRequest;
import io.aios.shared.proto.ScriptResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkerClientService {

    private final RSocketRequester rSocketRequester;

    public Mono<ScriptResponse> sendTask(String scriptId, String groovyCode, String methodName, Map<String, String> arguments) {
        ScriptRequest request = ScriptRequest.newBuilder()
                .setScriptId(scriptId)
                .setGroovyCode(groovyCode)
                .setMethodName(methodName)
                .putAllArguments(arguments)
                .build();

        return rSocketRequester
                .route("execute.groovy")
                .data(request)
                .retrieveMono(ScriptResponse.class);
    }
}
