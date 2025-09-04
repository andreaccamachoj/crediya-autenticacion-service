package co.com.pragma.crediya.autenticacion.security;

import co.com.pragma.crediya.model.gateway.PasswordEncoderGateway;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class BCryptPasswordEncoderAdapter implements PasswordEncoderGateway {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    @Override public Mono<Boolean> matches(String raw, String hash) {
        return Mono.fromCallable(() -> encoder.matches(raw, hash));
    }

    @Override public Mono<String> encode(String raw) {
        return Mono.fromCallable(() -> encoder.encode(raw))
                .subscribeOn(Schedulers.boundedElastic());
    }
}

