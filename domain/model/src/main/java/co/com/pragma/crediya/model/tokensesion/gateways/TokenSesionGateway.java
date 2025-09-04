package co.com.pragma.crediya.model.tokensesion.gateways;

import co.com.pragma.crediya.model.login.LoginResponse;
import co.com.pragma.crediya.model.tokensesion.TokenSesion;
import co.com.pragma.crediya.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface TokenSesionGateway {

    Mono<TokenSesion> generarToken(Usuario u, String rolNombre);;
    Mono<LoginResponse> validarToken(String token);
}
