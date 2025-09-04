package co.com.pragma.crediya.model.gateway;

import reactor.core.publisher.Mono;

public interface PasswordEncoderGateway {
    Mono<Boolean> matches(String raw, String hash);
    Mono<String> encode(String raw);
}
