package co.com.pragma.crediya.model.usuario.gateways;

import co.com.pragma.crediya.model.usuario.Usuario;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface UsuarioRepository {

    Mono<Usuario> save(Usuario usuario);
    Mono<Boolean> existsByCorreoElectronico(String correo);
    Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad);
    Mono<Usuario> findById(Long idUsuario);
    Flux<Usuario> findAllByIds(List<Long> ids);
}
