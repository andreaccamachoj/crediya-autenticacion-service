package co.com.pragma.crediya.r2dbc;

import co.com.pragma.crediya.r2dbc.Entity.RolEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.math.BigInteger;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, BigInteger>, ReactiveQueryByExampleExecutor<RolEntity> {
}
