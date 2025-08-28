package co.com.pragma.crediya.model.usuario.gateways;

import co.com.pragma.crediya.model.usuario.Usuario;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {

    Mono<Usuario> save(Usuario usuario);
    Mono<Boolean> existsByCorreoElectronico(String correo);
    Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad);
}
