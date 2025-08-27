package co.com.pragma.crediya.usecase.usuario;

import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.exception.message.ValidationExceptionMessage;
import co.com.pragma.crediya.model.rol.gateways.RolRepository;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.model.usuario.gateways.UsuarioRepository;
import co.com.pragma.crediya.utils.ValidationHelper;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioRepository usuariosRepository;
    private final RolRepository rolRepository;

    public Mono<Usuario> saveUsuarios(Usuario usuario) {
        return Mono.just(usuario)
                .flatMap(this::validarCamposRequeridos)
                .flatMap(this::procesarValidacionesAsincronasYGuardar);
    }

    private Mono<Usuario> validarCamposRequeridos(Usuario u) {
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
                        ValidationExceptionMessage.ROL_REQUIRED)
        )).thenReturn(u);
    }


    private Mono<Usuario> procesarValidacionesAsincronasYGuardar(Usuario usuario) {
        return Mono.zip(validateRol(usuario.getIdRol()),
                usuariosRepository.existsByCorreoElectronico(usuario.getCorreoElectronico()))
                .flatMap(tuple2 -> {
                    if (Boolean.TRUE.equals(tuple2.getT2())) {
                        return Mono.error(new BusinessException(BusinessExceptionMessage.EMAIL_ALREADY_REGISTERED));
                    }
                    return usuariosRepository.save(usuario);
                });
    }

    private Mono<Boolean> validateRol(Long idRol) {
        return Mono.justOrEmpty(idRol)
                .flatMap(rolRepository::existsById)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.just(true);
                    }
                    return Mono.error(new BusinessException(BusinessExceptionMessage.ROLE_NOT_FOUND));
                });
    }


    public Mono<Usuario> existsByDocumentoIdentidad(String documentoIdentidad) {
        return usuariosRepository.findByDocumentoIdentidad(documentoIdentidad);
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}