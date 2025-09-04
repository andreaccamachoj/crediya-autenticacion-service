package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.dto.request.UsuarioRequest;
import co.com.pragma.crediya.api.mapper.UsuarioDtoMapper;
import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.login.LoginResponse;
import co.com.pragma.crediya.usecase.usuario.UsuarioUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    private Mono<LoginResponse> principal(ServerRequest req) {
        return Mono.justOrEmpty(req.attribute("authUser")
                        .map(LoginResponse.class::cast)
                        .orElse(null))
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.UNAUTHORIZED)));
    }

    public Mono<ServerResponse> listenSaveUsuario(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UsuarioRequest.class)
                .zipWith(principal(serverRequest))
                .flatMap(t -> {
                    UsuarioRequest req = t.getT1();
                    LoginResponse auth       = t.getT2();
                    return usuarioUseCase.saveUsuarios(auth, usuarioDtoMapper.toDomain(req), req.clave());
                })
                .map(usuarioDtoMapper::toResponse)
                .flatMap(resp -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp));
    }


    public Mono<ServerResponse> getUsuario(ServerRequest req) {
        String documento = req.pathVariable("documentoIdentidad");
        return usuarioUseCase.existsByDocumentoIdentidad(documento)
                .map(usuarioDtoMapper::toResponse)
                .flatMap(resp -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(resp));
    }


    public Mono<ServerResponse> findByIds(ServerRequest req) {
        return req.bodyToMono(new org.springframework.core.ParameterizedTypeReference<java.util.List<Long>>() {})
                .defaultIfEmpty(java.util.List.of())
                .flatMap(usuarioUseCase::findAllByIds)
                .map(list -> list.stream()
                        .map(usuarioDtoMapper::toUsuarioDemoraficoResponse)
                        .toList())
                .flatMap(res -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(res));
    }


}
