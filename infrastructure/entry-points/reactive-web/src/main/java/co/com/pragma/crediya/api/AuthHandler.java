package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.dto.request.LoginRequest;
import co.com.pragma.crediya.api.dto.response.LoginResponse;
import co.com.pragma.crediya.api.dto.response.UsuarioLogeadoResponse;
import co.com.pragma.crediya.model.tokensesion.gateways.TokenSesionGateway;
import co.com.pragma.crediya.usecase.login.LoginUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthHandler {
    private final LoginUseCase loginUseCase;
    private final TokenSesionGateway tokenSesionGateway;

    public Mono<ServerResponse> login(ServerRequest req) {
        return req.bodyToMono(LoginRequest.class)
                .flatMap(r -> loginUseCase.login(r.correoElectronico(), r.clave()))
                .flatMap(res -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new LoginResponse(
                                res.token(),
                                res.idUsuario(),
                                res.expiracion(),
                                res.rolNombre()
                        )));
    }

    public Mono<ServerResponse> validate(ServerRequest req) {
        String auth = req.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = auth.substring(7);
        return tokenSesionGateway.validarToken(token)
                .flatMap(u -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(new UsuarioLogeadoResponse(
                                u.getIdUsuario(),
                                u.getCorreoElectronico(),
                                u.getIdRol(),
                                u.getNombreRol()
                        )))
                .onErrorResume(e -> ServerResponse.status(HttpStatus.UNAUTHORIZED).build());
    }

}
