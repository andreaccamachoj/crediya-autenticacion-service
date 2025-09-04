package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.tokensesion.TokenSesion;
import co.com.pragma.crediya.model.tokensesion.gateways.TokenSesionRepository;
import co.com.pragma.crediya.r2dbc.entity.TokenSesionEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class TokenSesionReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        TokenSesion,
        TokenSesionEntity,
        BigInteger,
        TokenSesionReactiveRepository
        > implements TokenSesionRepository {

    private static final Logger log = LoggerFactory.getLogger(TokenSesionReactiveRepositoryAdapter.class);

    public TokenSesionReactiveRepositoryAdapter(TokenSesionReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, TokenSesion.class));
    }

    @Override
    public Mono<TokenSesion> saveTokenSesion(TokenSesion tokenSesion) {
        return repository.save(toData(tokenSesion))
                .doOnSubscribe(s -> log.info("Guardando token de sesión: usuario={}, exp={}",
                        tokenSesion.getIdUsuario(), tokenSesion.getFechaExpiracion()))
                .doOnNext(data -> log.debug("DB <- save token_sesion: id={}, usuario={}, activo={}",
                        data.getIdTokenSesion(), data.getIdUsuario(), data.getIndActivo()))
                .map(this::toEntity)
                .doOnNext(dom -> log.info("Token guardado: id={}, usuario={}, activo={}",
                        dom.getIdTokenSesion(), dom.getIdUsuario(), dom.isIndActivo()))
                .doOnError(ex -> log.error("Error guardando token para usuario={}, token={}",
                        tokenSesion.getIdUsuario(), mask(tokenSesion.getToken()), ex))
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.SESSION_ERROR));
    }

    @Override
    public Mono<TokenSesion> findActiveByToken(String token) {
        return repository.findByTokenAndIndActivoIsTrue(token)
                .doOnSubscribe(s -> log.debug("Buscando token activo: {}", mask(token)))
                .doOnNext(data -> log.debug("DB -> TokenSesionEntity: id={}, usuario={}, activo={}",
                        data.getIdTokenSesion(), data.getIdUsuario(), data.getIndActivo()))
                .map(this::toEntity)
                .doOnNext(dom -> log.debug("Mapeado a dominio -> id={}, usuario={}, activo={}",
                        dom.getIdTokenSesion(), dom.getIdUsuario(), dom.isIndActivo()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("No se encontró token activo para {}", mask(token));
                    return Mono.empty();
                }))
                .doOnError(ex -> log.error("Error consultando token activo {}", mask(token), ex))
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.SESSION_ERROR));
    }

    @Override
    public Mono<Boolean> deactivateByToken(String token) {
        return repository.deactivateByToken(token)
                .doOnSubscribe(s -> log.info("Desactivando token {}", mask(token)))
                .doOnNext(rows -> log.debug("Filas afectadas al desactivar {}: {}", mask(token), rows))
                .map(rows -> rows != null && rows > 0)
                .doOnNext(result -> log.info("Resultado desactivación {}: {}", mask(token), result))
                .doOnError(ex -> log.error("Error desactivando token {}", mask(token), ex))
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.SESSION_ERROR));
    }

    @Override
    public Mono<Boolean> deactivateAllActiveByUser(Long idUsuario) {
        return repository.deactivateAllActiveByUser(idUsuario)
                .doOnSubscribe(s -> log.info("Desactivando tokens activos del usuario {}", idUsuario))
                .doOnNext(rows -> log.debug("Filas afectadas (desactivar todos) usuario {}: {}", idUsuario, rows))
                .map(rows -> rows != null && rows > 0)
                .defaultIfEmpty(false)
                .doOnNext(res -> log.info("Resultado desactivación masiva usuario {}: {}", idUsuario, res))
                .doOnError(ex -> log.error("Error desactivando tokens del usuario {}", idUsuario, ex))
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_SERVICE_ERROR));
    }

    private String mask(String token) {
        if (token == null || token.length() < 8) return "***";
        return token.substring(0, 6) + "..." + token.substring(token.length() - 6);
    }
}
