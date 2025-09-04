package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.r2dbc.entity.UsuarioEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioEntity, BigInteger>, ReactiveQueryByExampleExecutor<UsuarioEntity> {

    @Query("SELECT EXISTS (SELECT 1 FROM usuario WHERE LOWER(correo_electronico)=LOWER($1))")
    Mono<Boolean> existsByCorreoElectronico(String correo);
    Mono<UsuarioEntity> findByDocumentoIdentidad(String correo);
    @Query("SELECT * FROM crediya.usuario WHERE id_usuario IN (:ids)")
    Flux<UsuarioEntity> findAllByIdIn(List<Long> ids);

}
