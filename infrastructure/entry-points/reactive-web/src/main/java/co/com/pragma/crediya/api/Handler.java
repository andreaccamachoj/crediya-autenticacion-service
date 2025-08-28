package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.mapper.UsuarioDtoMapper;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {

    private final UsuarioUseCase usuarioUseCase;
    private final UsuarioDtoMapper usuarioDtoMapper;

    public Mono<ServerResponse> listenSaveUsuario(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Usuario.class)
                .flatMap(usuarioUseCase::saveUsuarios)
                .flatMap(savedUsuario -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(savedUsuario));
    }

    public Mono<ServerResponse> getUsuario(ServerRequest req) {
        String documento = req.pathVariable("documentoIdentidad");
        return usuarioUseCase.existsByDocumentoIdentidad(documento)
                .map(usuarioDtoMapper::toResponse)
                .flatMap(resp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp));
    }

}
