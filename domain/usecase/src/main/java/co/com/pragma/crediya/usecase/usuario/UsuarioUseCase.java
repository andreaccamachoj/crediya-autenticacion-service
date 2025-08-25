package co.com.pragma.crediya.usecase.usuario;

import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.ValidationException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.exception.message.ValidationExceptionMessage;
import co.com.pragma.crediya.model.rol.gateways.RolRepository;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.model.usuario.gateways.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class UsuarioUseCase {

    private static final Pattern EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioRepository usuariosRepository;
    private final RolRepository rolRepository;

    public Mono<Usuario> saveUsuarios(Usuario usuario) {
// refactorizar esta logica en un metodo aparte reutiliza
        if (isBlank(usuario.getNombres()))           return Mono.error(new ValidationException(ValidationExceptionMessage.NAMES_REQUIRED));
        if (isBlank(usuario.getApellidos()))         return Mono.error(new ValidationException(ValidationExceptionMessage.LASTNAMES_REQUIRED));
        if (isBlank(usuario.getCorreoElectronico()))             return Mono.error(new ValidationException(ValidationExceptionMessage.EMAIL_REQUIRED));
        if (!EMAIL.matcher(usuario.getCorreoElectronico()).matches())
            return Mono.error(new ValidationException(ValidationExceptionMessage.EMAIL_INVALID));
        if (usuario.getSalarioBase() == null)        return Mono.error(new ValidationException(ValidationExceptionMessage.SALARY_REQUIRED));
        if (usuario.getSalarioBase() < 0 || usuario.getSalarioBase() > 15_000_000)
            return Mono.error(new BusinessException(BusinessExceptionMessage.SALARY_OUT_OF_RANGE));

        return validateRol(usuario.getIdRol())
                .then(usuariosRepository.existsByCorreoElectronico(usuario.getCorreoElectronico()))
                .flatMap(exists -> exists
                        ? Mono.error(new BusinessException(BusinessExceptionMessage.EMAIL_ALREADY_REGISTERED))
                        : usuariosRepository.save(usuario));
    }

    private Mono<Void> validateRol(Long idRol) {
        return Mono.justOrEmpty(idRol)
                .switchIfEmpty(Mono.error(new ValidationException(ValidationExceptionMessage.ROL_REQUIRED)))
                .filterWhen(rolRepository::existsById)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessExceptionMessage.ROLE_NOT_FOUND)))
                .then();
    }

    public Mono<Usuario> existsByDocumentoIdentidad(String documentoIdentidad){
        return usuariosRepository.findByDocumentoIdentidad(documentoIdentidad);
    }


    private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
}
