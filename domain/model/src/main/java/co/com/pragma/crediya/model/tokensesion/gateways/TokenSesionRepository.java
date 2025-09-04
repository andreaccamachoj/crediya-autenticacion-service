package co.com.pragma.crediya.model.tokensesion.gateways;

import co.com.pragma.crediya.model.tokensesion.TokenSesion;
import reactor.core.publisher.Mono;

public interface TokenSesionRepository {
    Mono<TokenSesion> saveTokenSesion(TokenSesion tokenSesion);
    Mono<TokenSesion> findActiveByToken(String token);
    Mono<Boolean> deactivateByToken(String token);
    Mono<Boolean> deactivateAllActiveByUser(Long idUsuario);

}
