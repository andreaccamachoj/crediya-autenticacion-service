package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.model.permisos.Permisos;
import co.com.pragma.crediya.model.permisos.gateways.PermisosRepository;
import co.com.pragma.crediya.r2dbc.entity.PermisosEntity;
import co.com.pragma.crediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Repository
public class PermisosReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Permisos,
        PermisosEntity,
        BigInteger,
        PermisosReactiveRepository
        > implements PermisosRepository {
    public PermisosReactiveRepositoryAdapter(PermisosReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Permisos.class));
    }
}
