package co.com.pragma.crediya.usecase.login;

import co.com.pragma.crediya.model.credenciales.Credenciales;
import co.com.pragma.crediya.model.credenciales.gateways.CredencialesRepository;
import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.gateway.PasswordEncoderGateway;
import co.com.pragma.crediya.model.login.LoginResult;
import co.com.pragma.crediya.model.rol.Rol;
import co.com.pragma.crediya.model.rol.gateways.RolRepository;
import co.com.pragma.crediya.model.tokensesion.gateways.TokenSesionGateway;
import co.com.pragma.crediya.model.tokensesion.gateways.TokenSesionRepository;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final TokenSesionGateway tokenSesionGateway;
    private final CredencialesRepository credencialesRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final UsuarioRepository usuarioRepository;
    private final TokenSesionRepository tokenSesionRepository;
    private final RolRepository rolRepository;

    public Mono<LoginResult> login(String correo, String claveRaw) {
        return credencialesRepository.findByCorreoElectronico(correo)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.USER_NOT_FOUND)))
                .flatMap(cred -> validatePassword(claveRaw, cred.getClave())
                        .flatMap(valid -> Boolean.TRUE.equals(valid)
                                ? generateLoginResult(cred)
                                : Mono.error(new BusinessException(BusinessExceptionMessage.INVALID_PASSWORD))
                        )
                );
    }

    private Mono<Boolean> validatePassword(String raw, String encoded) {
        return passwordEncoderGateway.matches(raw, encoded);
    }

    private Mono<LoginResult> generateLoginResult(Credenciales cred) {
        return usuarioRepository.findById(cred.getIdUsuario())
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.USER_NOT_FOUND)))
                .flatMap(usuario -> Mono.zip(
                        deactivatePreviousSessions(usuario.getIdUsuario()),
                        findUserRole(usuario.getIdRol())
                ).flatMap(tuple -> buildLoginResult(usuario, tuple.getT2())));
    }

    private Mono<Boolean> deactivatePreviousSessions(Long userId) {
        return tokenSesionRepository.deactivateAllActiveByUser(userId)
                .onErrorResume(e -> Mono.just(false));
    }

    private Mono<Rol> findUserRole(Long roleId) {
        return rolRepository.findById(roleId)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.ROLE_NOT_FOUND)));
    }

    private Mono<LoginResult> buildLoginResult(Usuario usuario, Rol rol) {
        return tokenSesionGateway.generarToken(usuario, rol.getNombre())
                .map(token -> new LoginResult(
                        token.getToken(),
                        token.getIdUsuario(),
                        token.getFechaExpiracion(),
                        rol.getNombre()
                ));
    }
}