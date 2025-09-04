package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.credenciales.Credenciales;
import co.com.pragma.crediya.model.credenciales.gateways.CredencialesRepository;
import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.r2dbc.entity.CredencialesEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class CredencialesReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Credenciales,
        CredencialesEntity,
        BigInteger,
        CredencialesReactiveRepository
        > implements CredencialesRepository {
    public CredencialesReactiveRepositoryAdapter(CredencialesReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Credenciales.class));
    }

    private static final Logger log = LoggerFactory.getLogger(CredencialesReactiveRepositoryAdapter.class);

    @Override
    public Mono<Credenciales> findByCorreoElectronico(String correoElectronico) {
        return repository.findByCorreoElectronico(correoElectronico)
                .map(this::toEntity)
                .doOnSubscribe(sub -> log.info("Buscando credenciales por correo [{}]", correoElectronico))
                .doOnNext(res -> log.info("Credenciales encontradas para [{}]: {}", correoElectronico, res))
                .doOnError(err -> log.error("Error al buscar credenciales para [{}]: {}", correoElectronico, err.getMessage()))
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_NOT_FOUND));
    }

    @Override
    public Mono<Boolean> registrarIntentoFallido(String correo, Integer intentosMaximos) {
        return repository.registrarIntentoFallido(correo, intentosMaximos)
                .doOnSubscribe(sub -> log.info("Registrando intento fallido para [{}] con máximo [{}]", correo, intentosMaximos))
                .doOnNext(res -> log.info("Resultado registrarIntentoFallido [{}]: {}", correo, res))
                .doOnError(err -> log.error("Error al registrar intento fallido [{}]: {}", correo, err.getMessage()))
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.USER_NOT_FOUND)))
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_SERVICE_ERROR));
    }

    @Override
    public Mono<Boolean> resetIntentos(String correo) {
        return repository.resetIntentosReturning(correo)
                .doOnSubscribe(sub -> log.info("Reseteando intentos para [{}]", correo))
                .doOnNext(val -> log.info("Resultado resetIntentos [{}]: {}", correo, val))
                .doOnError(err -> log.error("Error al resetear intentos [{}]: {}", correo, err.getMessage()))
                .map(val -> val != null && val == 0)
                .defaultIfEmpty(false)
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_SERVICE_ERROR));
    }

    @Override
    public Mono<Boolean> isBloqueado(String correo) {
        return repository.isBloqueado(correo)
                .doOnSubscribe(sub -> log.info("Verificando si está bloqueado [{}]", correo))
                .doOnNext(res -> log.info("Resultado isBloqueado [{}]: {}", correo, res))
                .doOnError(err -> log.error("Error al verificar bloqueo [{}]: {}", correo, err.getMessage()))
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.USER_NOT_FOUND)))
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_SERVICE_ERROR));
    }

    @Override
    public Mono<Boolean> bloquearPorCorreo(String correo) {
        return repository.bloquearPorCorreo(correo)
                .doOnSubscribe(sub -> log.info("Bloqueando usuario con correo [{}]", correo))
                .doOnNext(res -> log.info("Resultado bloquearPorCorreo [{}]: {}", correo, res))
                .doOnError(err -> log.error("Error al bloquear usuario [{}]: {}", correo, err.getMessage()))
                .map(Boolean::booleanValue)
                .defaultIfEmpty(false)
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_SERVICE_ERROR));
    }
}
