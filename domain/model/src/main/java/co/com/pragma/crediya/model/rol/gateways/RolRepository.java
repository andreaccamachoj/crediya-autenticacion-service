package co.com.pragma.crediya.model.rol.gateways;

import co.com.pragma.crediya.model.rol.Rol;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Boolean> existsById(Long id);
    Mono<Rol> findById(Long idRol);
}
