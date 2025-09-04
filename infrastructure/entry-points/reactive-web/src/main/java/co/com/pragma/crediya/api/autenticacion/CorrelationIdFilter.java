package co.com.pragma.crediya.api.autenticacion;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationIdFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange ex, WebFilterChain chain) {
        String cid = Optional.ofNullable(ex.getRequest().getHeaders().getFirst("X-Correlation-Id"))
                .orElse(UUID.randomUUID().toString());
        ex.getResponse().getHeaders().add("X-Correlation-Id", cid);
        return chain.filter(ex)
                .contextWrite(ctx -> ctx.put("correlationId", cid));
    }
}

