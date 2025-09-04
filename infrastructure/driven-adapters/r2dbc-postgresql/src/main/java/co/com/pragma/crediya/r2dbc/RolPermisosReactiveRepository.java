package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.r2dbc.entity.RolPermisosEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface RolPermisosReactiveRepository
        extends ReactiveCrudRepository<RolPermisosEntity, BigInteger>, ReactiveQueryByExampleExecutor<RolPermisosEntity> {

    @Query("""
        SELECT EXISTS(
          SELECT 1
          FROM rol_permisos rp
          JOIN permisos p ON p.id_permisos = rp.id_permisos
          WHERE rp.id_rol = :idRol
            AND p.codigo = :codigoPermiso
            AND rp.indhabilitado = :indHabilitado
        )
        """)
    Mono<Boolean> existsByIdRolAndCodigoPermisoAndIndHabilitadoTrue(
            @Param("idRol") Long idRol,
            @Param("codigoPermiso") String codigoPermiso,
            @Param("indHabilitado") Boolean indHabilitado);
}
