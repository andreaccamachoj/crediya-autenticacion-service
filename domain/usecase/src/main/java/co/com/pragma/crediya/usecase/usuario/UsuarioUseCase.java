package co.com.pragma.crediya.usecase.usuario;

import co.com.pragma.crediya.model.credenciales.Credenciales;
import co.com.pragma.crediya.model.credenciales.gateways.CredencialesRepository;
import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.exception.message.ValidationExceptionMessage;
import co.com.pragma.crediya.model.gateway.PasswordEncoderGateway;
import co.com.pragma.crediya.model.login.LoginResponse;
import co.com.pragma.crediya.model.rol.gateways.RolRepository;
import co.com.pragma.crediya.model.rolpermisos.gateways.RolPermisosRepository;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.crediya.usecase.enums.PermisoCodigo;
import co.com.pragma.crediya.utils.TuplaUsuarioHash;
import co.com.pragma.crediya.utils.ValidationHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static co.com.pragma.crediya.utils.utils.isBlank;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioRepository usuariosRepository;
    private final RolRepository rolRepository;
    private final CredencialesRepository credencialesRepository;
    private final PasswordEncoderGateway passwordEncoderGateway;
    private final RolPermisosRepository rolPermisosRepository;

    public Mono<Usuario> saveUsuarios(LoginResponse authPrincipal, Usuario usuario, String claveRaw) {
        return rolPermisosRepository.validarPermiso(authPrincipal.getIdRol(),
                        PermisoCodigo.CREAR_USUARIO.getCodigo(), Boolean.TRUE)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.UNAUTHORIZED)))
                .flatMap(__ -> saveUsuarios(usuario, claveRaw)); // 👈 cambia .then por .flatMap
    }

    public Mono<Usuario> saveUsuarios(Usuario usuario, String claveRaw) {
        return Mono.just(usuario)
                .flatMap(u -> validarCamposRequeridos(u, claveRaw))
                .flatMap(this::validarPrevios)
                .zipWith(passwordEncoderGateway.encode(claveRaw), TuplaUsuarioHash::of)
                .flatMap(t ->
                        usuariosRepository.save(t.usuario())
                                .flatMap(saved -> {
                                    Credenciales cred = new Credenciales();
                                    cred.setIdUsuario(saved.getIdUsuario());
                                    cred.setCorreoElectronico(saved.getCorreoElectronico());
                                    cred.setClave(t.hash());
                                    return credencialesRepository.save(cred).thenReturn(saved);
                                })
                );
    }

    private Mono<Usuario> validarCamposRequeridos(Usuario u, String claveRaw) {
        return ValidationHelper.validateAll(List.of(
                () -> ValidationHelper.validateCondition(!isBlank(u.getNombres()),
                        ValidationExceptionMessage.NAMES_REQUIRED),
                () -> ValidationHelper.validateCondition(!isBlank(u.getApellidos()),
                        ValidationExceptionMessage.LASTNAMES_REQUIRED),
                () -> ValidationHelper.validateCondition(!isBlank(u.getCorreoElectronico()),
                        ValidationExceptionMessage.EMAIL_REQUIRED),
                () -> ValidationHelper.validateCondition(
                        EMAIL_PATTERN.matcher(u.getCorreoElectronico()).matches(),
                        ValidationExceptionMessage.EMAIL_INVALID),
                () -> ValidationHelper.validateCondition(u.getSalarioBase() != null,
                        ValidationExceptionMessage.SALARY_REQUIRED),
                () -> ValidationHelper.validateBusinessCondition(
                        u.getSalarioBase() >= 0 && u.getSalarioBase() <= 15_000_000,
                        BusinessExceptionMessage.SALARY_OUT_OF_RANGE),
                () -> ValidationHelper.validateCondition(
                        u.getIdRol() != null,
                        ValidationExceptionMessage.ROL_REQUIRED),
                () -> ValidationHelper.validateCondition(!isBlank(claveRaw),
                        ValidationExceptionMessage.PASSWORD_REQUIRED)
        )).thenReturn(u);
    }

    private Mono<Boolean> validateRol(Long idRol) {
        return Mono.justOrEmpty(idRol)
                .flatMap(rolRepository::existsById)
                .flatMap(exists -> Boolean.TRUE.equals(exists)
                        ? Mono.just(true)
                        : Mono.error(new BusinessException(BusinessExceptionMessage.ROLE_NOT_FOUND)));
    }

    public Mono<Usuario> existsByDocumentoIdentidad(String documentoIdentidad) {
        return usuariosRepository.findByDocumentoIdentidad(documentoIdentidad)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.APPLICANT_NOT_FOUND)));
    }

    private Mono<Usuario> validarPrevios(Usuario usuario) {
        return Mono.zip(validateRol(usuario.getIdRol()),
                        usuariosRepository.existsByCorreoElectronico(usuario.getCorreoElectronico()))
                .flatMap(t -> Boolean.TRUE.equals(t.getT2())
                        ? Mono.error(new BusinessException(BusinessExceptionMessage.EMAIL_ALREADY_REGISTERED))
                        : Mono.just(usuario));
    }


    public Mono<List<Usuario>> findAllByIds(List<Long> ids) {
        return Mono.justOrEmpty(ids)
                .defaultIfEmpty(List.of())
                .map(list -> list.stream()
                        .filter(Objects::nonNull)
                        .distinct()
                        .toList())
                .flatMap(cleanIds -> {
                    if (cleanIds.isEmpty()) {
                        return Mono.just(List.of());
                    }
                    return usuariosRepository.findAllByIds(cleanIds).collectList();
                });
    }
}