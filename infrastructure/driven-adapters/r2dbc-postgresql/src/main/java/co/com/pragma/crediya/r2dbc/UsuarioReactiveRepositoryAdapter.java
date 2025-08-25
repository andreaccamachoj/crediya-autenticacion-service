package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.crediya.r2dbc.Entity.UsuarioEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

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


    @Override
    public Mono<Usuario> save(Usuario usuario) {
        return super.save(usuario);
    }

    @Override
    public Mono<Boolean> existsByCorreoElectronico(String correo) {
        return repository.existsByCorreoElectronico(correo);
    }

    @Override
    public Mono<Usuario> findByDocumentoIdentidad(String documentoIdentidad) {
        return repository.findByDocumentoIdentidad(documentoIdentidad)
                .map(this::toEntity)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.APPLICANT_NOT_FOUND)));
    }

}
