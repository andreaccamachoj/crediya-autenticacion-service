package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.crediya.r2dbc.entity.UsuarioEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigInteger;
import java.util.List;

@Repository
@Transactional
public class UsuarioReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Usuario,
        UsuarioEntity,
        BigInteger,
        UsuarioReactiveRepository
        >  implements UsuarioRepository {
    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Usuario.class/* change for domain model */));
    }

    private static final Logger log = LoggerFactory.getLogger(UsuarioReactiveRepositoryAdapter.class);


    @Override
    public Mono<Usuario> save(Usuario usuario) {
        return super.save(usuario)
                .doOnSubscribe(sub -> log.info("Guardando usuario: {}", usuario))
                .doOnNext(res -> log.info("Usuario guardado con éxito: {}", res))
                .doOnError(err -> log.error("Error al guardar usuario [{}]: {}", usuario, err.getMessage()));
    }

    @Override
    public Mono<Boolean> existsByCorreoElectronico(String correo) {
        return repository.existsByCorreoElectronico(correo)
                .doOnSubscribe(sub -> log.info("Verificando existencia de correo: {}", correo))
                .doOnNext(existe -> log.info("Resultado existsByCorreoElectronico [{}]: {}", correo, existe))
                .doOnError(err -> log.error("Error al verificar correo [{}]: {}", correo, err.getMessage()));
    }

    @Override
    public Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad) {
        return repository.findByDocumentoIdentidad(documentoIdentidad)
                .doOnSubscribe(sub -> log.info("Buscando usuario por documentoIdentidad [{}]", documentoIdentidad))
                .doOnNext(entity -> log.info("Usuario encontrado [{}]: {}", documentoIdentidad, entity))
                .doOnError(err -> log.error("Error al buscar usuario [{}]: {}", documentoIdentidad, err.getMessage()))
                .map(this::toEntity)
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.APPLICANT_NOT_FOUND));
    }

    @Override
    public Mono<Usuario> findById(Long idUsuario) {
        return repository.findById(BigInteger.valueOf(idUsuario))
                .doOnSubscribe(sub -> log.info("Buscando usuario por id [{}]", idUsuario))
                .doOnNext(entity -> log.info("Usuario encontrado id [{}]: {}", idUsuario, entity))
                .doOnError(err -> log.error("Error al buscar usuario id [{}]: {}", idUsuario, err.getMessage()))
                .map(this::toEntity)
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_NOT_FOUND));
    }

    @Override
    public Flux<Usuario> findAllByIds(List<Long> ids) {
        return repository.findAllByIdIn(ids)
                .doOnSubscribe(sub -> log.info("Buscando usuarios por lista de ids: {}", ids))
                .doOnNext(entity -> log.info("Usuario encontrado en lista [{}]: {} {}", ids, entity.getIdUsuario(), entity.getNombres()))
                .doOnError(err -> log.error("Error al buscar usuarios [{}]: {}", ids, err.getMessage()))
                .map(this::toEntity)
                .onErrorMap(ex -> new BusinessException(BusinessExceptionMessage.USER_SERVICE_ERROR));
    }

}
