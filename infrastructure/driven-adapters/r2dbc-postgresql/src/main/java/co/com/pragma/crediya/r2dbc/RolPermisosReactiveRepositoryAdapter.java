package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.rolpermisos.RolPermisos;
import co.com.pragma.crediya.model.rolpermisos.gateways.RolPermisosRepository;
import co.com.pragma.crediya.r2dbc.entity.RolPermisosEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class RolPermisosReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        RolPermisos,
        RolPermisosEntity,
        BigInteger,
        RolPermisosReactiveRepository
        > implements RolPermisosRepository {
    public RolPermisosReactiveRepositoryAdapter(RolPermisosReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, RolPermisos.class));
    }
    private static final Logger log = LoggerFactory.getLogger(RolPermisosReactiveRepositoryAdapter.class);


    @Override
    public Mono<Boolean> validarPermiso(Long idRol, String codigoPermiso, Boolean indHabilitado) {
        return repository.existsByIdRolAndCodigoPermisoAndIndHabilitadoTrue(idRol, codigoPermiso, indHabilitado)
                .doOnSubscribe(sub -> log.info("Validando permiso [{}] para rol [{}] con indHabilitado={}", codigoPermiso, idRol, indHabilitado))
                .doOnNext(result -> log.info("Resultado validarPermiso rol [{}], permiso [{}]: {}", idRol, codigoPermiso, result))
                .doOnError(err -> log.error("Error al validar permiso [{}] para rol [{}]: {}", codigoPermiso, idRol, err.getMessage()))
                .defaultIfEmpty(false);
    }
}
