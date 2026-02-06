package io.aios.kernel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;

@Configuration
public class WorkerConnectionConfig {

    @Bean
    public RSocketStrategies rSocketStrategies() {
        return RSocketStrategies.builder()
                .encoder(new org.springframework.http.codec.protobuf.ProtobufEncoder())
                .decoder(new org.springframework.http.codec.protobuf.ProtobufDecoder())
                .build();
    }

    @Bean
    public RSocketRequester rSocketRequester(RSocketRequester.Builder builder, RSocketStrategies strategies) {
        return builder
                .rsocketStrategies(strategies)
                .tcp("localhost", 7000);
    }
}
