package co.com.pragma.crediya.model.rolpermisos.gateways;

import reactor.core.publisher.Mono;

public interface RolPermisosRepository {

    Mono<Boolean> validarPermiso(Long idRol, String codigoPermiso, Boolean indHabilitado);
}
