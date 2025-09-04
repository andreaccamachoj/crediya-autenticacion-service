package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.rol.Rol;
import co.com.pragma.crediya.model.rol.gateways.RolRepository;
import co.com.pragma.crediya.r2dbc.entity.RolEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class RolReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Rol,
        RolEntity,
        BigInteger,
        RolReactiveRepository
        > implements RolRepository {
    public RolReactiveRepositoryAdapter(RolReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Rol.class));
    }

    private static final Logger log = LoggerFactory.getLogger(RolReactiveRepositoryAdapter.class);


    @Override
    public Mono<Boolean> existsById(Long id) {
        return repository.existsById(BigInteger.valueOf(id))
                .doOnSubscribe(sub -> log.info("Verificando existencia del rol con id [{}]", id))
                .doOnNext(existe -> log.info("Resultado existsById [{}]: {}", id, existe))
                .doOnError(err -> log.error("Error al verificar existencia del rol [{}]: {}", id, err.getMessage()));
    }

    @Override
    public Mono<Rol> findById(Long idRol) {
        return repository.findById(BigInteger.valueOf(idRol))
                .doOnSubscribe(sub -> log.info("Buscando rol por id [{}]", idRol))
                .doOnNext(entity -> log.info("Rol encontrado id [{}]: {}", idRol, entity))
                .doOnError(err -> log.error("Error al buscar rol id [{}]: {}", idRol, err.getMessage()))
                .map(this::toEntity)
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_NOT_FOUND));
    }
}
