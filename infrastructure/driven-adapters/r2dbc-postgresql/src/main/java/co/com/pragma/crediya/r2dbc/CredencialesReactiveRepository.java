package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.r2dbc.entity.CredencialesEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface CredencialesReactiveRepository extends ReactiveCrudRepository<CredencialesEntity, BigInteger>, ReactiveQueryByExampleExecutor<CredencialesEntity> {

    Mono<CredencialesEntity> findByCorreoElectronico(String correo);

    @Query("""
           SELECT bloqueado
           FROM crediya.credenciales
           WHERE correo_electronico = :correo
           """)
    Mono<Boolean> isBloqueado(@Param("correo") String correo);

    @Query("""
           UPDATE crediya.credenciales
              SET intentos_fallidos = intentos_fallidos + 1,
                  bloqueado = CASE WHEN intentos_fallidos + 1 >= :max THEN TRUE ELSE bloqueado END,
                  fecha_bloqueo = CASE
                      WHEN intentos_fallidos + 1 >= :max AND bloqueado = FALSE THEN NOW()
                      ELSE fecha_bloqueo
                  END
            WHERE correo_electronico = :correo
           RETURNING bloqueado
           """)
    Mono<Boolean> registrarIntentoFallido(@Param("correo") String correo, @Param("max") int max);

    @Query("""
           UPDATE crediya.credenciales
              SET intentos_fallidos = 0
            WHERE correo_electronico = :correo
           RETURNING intentos_fallidos
           """)
    Mono<Integer> resetIntentosReturning(@Param("correo") String correo);

    @Query("""
           UPDATE crediya.credenciales
              SET bloqueado = TRUE, fecha_bloqueo = NOW()
            WHERE correo_electronico = :correo
           RETURNING bloqueado
           """)
    Mono<Boolean> bloquearPorCorreo(@Param("correo") String correo);
}
