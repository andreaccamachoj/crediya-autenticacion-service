package co.com.pragma.crediya.usecase.usuario;

import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.ValidationException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.exception.message.ValidationExceptionMessage;
import co.com.pragma.crediya.model.rol.gateways.RolRepository;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;

class UsuarioUseCaseTest {

    private UsuarioRepository usuarioRepository;

    private RolRepository rolRepository;

    private UsuarioUseCase usuarioUseCase;

    @BeforeEach
    void setUp() {
        usuarioRepository = Mockito.mock(UsuarioRepository.class);
        rolRepository = Mockito.mock(RolRepository.class);
        usuarioUseCase = new UsuarioUseCase(usuarioRepository, rolRepository);
    }

    private Usuario buildValidUsuario() {
        return Usuario.builder()
                .nombres("Andrea")
                .apellidos("Camacho")
                .correoElectronico("test@correo.com")
                .salarioBase(5_000_000D)
                .idRol(1L)
                .documentoIdentidad("123456789")
                .build();
    }

    @Test
    void saveUsuarioSuccess() {
        Usuario usuario = buildValidUsuario();

        when(rolRepository.existsById(usuario.getIdRol())).thenReturn(Mono.just(true));
        when(usuarioRepository.existsByCorreoElectronico(usuario.getCorreoElectronico())).thenReturn(Mono.just(false));
        when(usuarioRepository.save(usuario)).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectNext(usuario)
                .verifyComplete();

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void saveUsuarioFailsWhenNamesMissing() {
        Usuario usuario = buildValidUsuario();
        usuario.setNombres(null);

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectErrorSatisfies(error ->
                {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.NAMES_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void failsWhenLastnamesMissing() {
        Usuario usuario = buildValidUsuario();
        usuario.setApellidos("");

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.LASTNAMES_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void failsWhenEmailMissing() {
        Usuario usuario = buildValidUsuario();
        usuario.setCorreoElectronico("  ");

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.EMAIL_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void failsWhenSalaryIsNull() {
        Usuario usuario = buildValidUsuario();
        usuario.setSalarioBase(null);

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.SALARY_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioFailsWhenEmailAlreadyExists() {
        Usuario usuario = buildValidUsuario();

        when(rolRepository.existsById(usuario.getIdRol())).thenReturn(Mono.just(true));
        when(usuarioRepository.existsByCorreoElectronico(usuario.getCorreoElectronico())).thenReturn(Mono.just(true));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectErrorSatisfies(error ->
                {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(BusinessExceptionMessage.EMAIL_ALREADY_REGISTERED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void failsWhenSalaryOutOfRange() {
        Usuario usuario = buildValidUsuario();
        usuario.setSalarioBase(20_000_000D);

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(BusinessExceptionMessage.SALARY_OUT_OF_RANGE.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioFailsWhenRoleIsNull() {
        Usuario usuario = buildValidUsuario();
        usuario.setIdRol(null);

        when(usuarioRepository.existsByCorreoElectronico(anyString()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectErrorMatches(error -> error.getMessage().contains(ValidationExceptionMessage.ROL_REQUIRED.getMessage())
                )
                .verify();
    }

    @Test
    void saveUsuarioFailsWhenRoleNotFound() {
        Usuario usuario = buildValidUsuario();
        usuario.setIdRol(4L);

        when(rolRepository.existsById(usuario.getIdRol())).thenReturn(Mono.just(false));
        when(usuarioRepository.existsByCorreoElectronico(anyString()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(
                            BusinessExceptionMessage.ROLE_NOT_FOUND.getMessage(),
                            error.getMessage()
                    );
                })
                .verify();
    }

    @Test
    void saveUsuarioSuccessWhenRoleExists() {
        Usuario usuario = buildValidUsuario();
        usuario.setIdRol(1L);

        when(rolRepository.existsById(usuario.getIdRol())).thenReturn(Mono.just(true));
        when(usuarioRepository.existsByCorreoElectronico(usuario.getCorreoElectronico())).thenReturn(Mono.just(false));
        when(usuarioRepository.save(usuario)).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario))
                .expectNext(usuario)
                .verifyComplete();

        verify(usuarioRepository).save(usuario);
    }


    @Test
    void findByDocumentoIdentidadSuccess() {
        Usuario usuario = buildValidUsuario();

        when(usuarioRepository.findByDocumentoIdentidad(usuario.getDocumentoIdentidad()))
                .thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.existsByDocumentoIdentidad(usuario.getDocumentoIdentidad()))
                .expectNext(usuario)
                .verifyComplete();

        verify(usuarioRepository).findByDocumentoIdentidad(usuario.getDocumentoIdentidad());
    }
}
