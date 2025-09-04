package co.com.pragma.crediya.model.credenciales.gateways;

import co.com.pragma.crediya.model.credenciales.Credenciales;
import reactor.core.publisher.Mono;

public interface CredencialesRepository {
    Mono<Credenciales> findByCorreoElectronico(String correo);
    public Mono<Credenciales> save(Credenciales credenciales);
    Mono<Boolean> registrarIntentoFallido(String correo, Integer intentosMaximos);
    Mono<Boolean> resetIntentos(String correo);
    Mono<Boolean> isBloqueado(String correo);
    Mono<Boolean> bloquearPorCorreo(String correo);
}
