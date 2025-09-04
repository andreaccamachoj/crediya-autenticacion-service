package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.r2dbc.entity.TokenSesionEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

public interface TokenSesionReactiveRepository extends ReactiveCrudRepository<TokenSesionEntity, BigInteger>, ReactiveQueryByExampleExecutor<TokenSesionEntity> {

    Mono<TokenSesionEntity> findByTokenAndIndActivoIsTrue(String token);

    @Query("UPDATE token_sesion SET indactivo = FALSE WHERE token = :token")
    Mono<Integer> deactivateByToken(String token);

    @Query("UPDATE token_sesion SET indactivo = FALSE WHERE id_usuario = :idUsuario AND indactivo = TRUE")
    Mono<Integer> deactivateAllActiveByUser(Long idUsuario);

}
