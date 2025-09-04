package co.com.pragma.crediya.api.autenticacion;

import co.com.pragma.crediya.api.config.path.LoginPath;
import co.com.pragma.crediya.model.tokensesion.gateways.TokenSesionGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter implements WebFilter {

    private final TokenSesionGateway tokenSesionGateway;
    private final LoginPath loginPath;

    @Override
    public Mono<Void> filter(ServerWebExchange ex, WebFilterChain chain) {
        String path = ex.getRequest().getPath().value();
        if (path.startsWith(loginPath.getLogin())
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/swagger-resources")
                || path.startsWith("/webjars")) {
            return chain.filter(ex);
        }

        String auth = ex.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return ex.getResponse().setComplete();
        }

        String token = auth.substring(7);
        return tokenSesionGateway.validarToken(token)
                .doOnNext(u -> ex.getAttributes().put("authUser", u))
                .then(chain.filter(ex))
                .onErrorResume(e -> {
                    ex.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return ex.getResponse().setComplete();
                });
    }
}
