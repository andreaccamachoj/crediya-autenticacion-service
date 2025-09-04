package co.com.pragma.crediya.usecase.usuario;

import co.com.pragma.crediya.model.credenciales.Credenciales;
import co.com.pragma.crediya.model.credenciales.gateways.CredencialesRepository;
import co.com.pragma.crediya.model.exception.BusinessException;
import co.com.pragma.crediya.model.exception.ValidationException;
import co.com.pragma.crediya.model.exception.message.BusinessExceptionMessage;
import co.com.pragma.crediya.model.exception.message.ValidationExceptionMessage;
import co.com.pragma.crediya.model.gateway.PasswordEncoderGateway;
import co.com.pragma.crediya.model.login.LoginResponse;
import co.com.pragma.crediya.model.rol.gateways.RolRepository;
import co.com.pragma.crediya.model.rolpermisos.gateways.RolPermisosRepository;
import co.com.pragma.crediya.model.usuario.Usuario;
import co.com.pragma.crediya.model.usuario.gateways.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @Mock
    private CredencialesRepository credencialesRepository;
    @Mock
    private PasswordEncoderGateway passwordEncoderGateway;
    @Mock
    private RolPermisosRepository rolPermisosRepository;

    private UsuarioUseCase usuarioUseCase;

    @BeforeEach
    void setUp() {
        usuarioUseCase = new UsuarioUseCase(
                usuarioRepository, rolRepository, credencialesRepository,
                passwordEncoderGateway, rolPermisosRepository
        );
    }

    private Usuario buildUsuario() {
        return Usuario.builder()
                .idUsuario(1L)
                .nombres("Andrea")
                .apellidos("Camacho")
                .correoElectronico("test@correo.com")
                .documentoIdentidad("123456789")
                .salarioBase(5_000_000D)
                .idRol(2L)
                .build();
    }

    @Test
    void saveUsuarioConPermisoExitoso() {
        Usuario usuario = buildUsuario();
        LoginResponse auth = new LoginResponse(1L, "test@correo.com", 1L, "ADMINISTRADOR");

        when(rolPermisosRepository.validarPermiso(1L, "CREAR_USUARIO", true))
                .thenReturn(Mono.just(true));
        when(rolRepository.existsById(2L)).thenReturn(Mono.just(true));
        when(usuarioRepository.existsByCorreoElectronico(usuario.getCorreoElectronico())).thenReturn(Mono.just(false));
        when(passwordEncoderGateway.encode("clave123")).thenReturn(Mono.just("hash"));
        when(usuarioRepository.save(usuario)).thenReturn(Mono.just(usuario));
        when(credencialesRepository.save(any())).thenReturn(Mono.just(new Credenciales()));

        StepVerifier.create(usuarioUseCase.saveUsuarios(auth, usuario, "clave123"))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void saveUsuarioSinPermisoDebeFallar() {
        Usuario usuario = buildUsuario();
        LoginResponse auth = new LoginResponse(2L, "test@correo.com", 2L, "CLIENTE");

        when(rolPermisosRepository.validarPermiso(any(), any(), any()))
                .thenReturn(Mono.just(false));

        StepVerifier.create(usuarioUseCase.saveUsuarios(auth, usuario, "clave123"))
                .expectErrorSatisfies(e -> {
                    assertTrue(e instanceof BusinessException);
                    assertEquals(BusinessExceptionMessage.UNAUTHORIZED.getMessage(), e.getMessage());
                })
                .verify();
    }

    // ---------- saveUsuarios validaciones ----------

    @Test
    void saveUsuarioExitoso() {
        Usuario usuario = buildUsuario();

        when(rolRepository.existsById(2L)).thenReturn(Mono.just(true));
        when(usuarioRepository.existsByCorreoElectronico(usuario.getCorreoElectronico())).thenReturn(Mono.just(false));
        when(passwordEncoderGateway.encode("clave123")).thenReturn(Mono.just("hash"));
        when(usuarioRepository.save(usuario)).thenReturn(Mono.just(usuario));
        when(credencialesRepository.save(any())).thenReturn(Mono.just(new Credenciales()));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "clave123"))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void saveUsuarioDebeFallarSiEmailYaRegistrado() {
        Usuario usuario = buildUsuario();

        when(rolRepository.existsById(anyLong())).thenReturn(Mono.just(true));
        when(usuarioRepository.existsByCorreoElectronico(any())).thenReturn(Mono.just(true));

        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "qwerty"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(BusinessExceptionMessage.EMAIL_ALREADY_REGISTERED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioDebeFallarSiRolNoExiste() {
        Usuario usuario = buildUsuario();

        when(rolRepository.existsById(anyLong())).thenReturn(Mono.just(false));
        when(usuarioRepository.existsByCorreoElectronico(any())).thenReturn(Mono.just(false));

        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "clave123"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(BusinessExceptionMessage.ROLE_NOT_FOUND.getMessage(), error.getMessage());
                })
                .verify();
    }


    @Test
    void saveUsuarioDebeFallarSiClaveEsVacia() {
        Usuario usuario = buildUsuario();

        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, ""))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.PASSWORD_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    // Puedes repetir tests análogos para cada campo requerido...

    // ---------- existsByDocumentoIdentidad ----------

    @Test
    void existsByDocumentoIdentidadExitoso() {
        Usuario usuario = buildUsuario();
        when(usuarioRepository.findByDocumentoIdentidad("123456789")).thenReturn(Mono.just(usuario));

        StepVerifier.create(usuarioUseCase.existsByDocumentoIdentidad("123456789"))
                .expectNext(usuario)
                .verifyComplete();
    }

    @Test
    void existsByDocumentoIdentidadNoEncontrado() {
        when(usuarioRepository.findByDocumentoIdentidad("123456789")).thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.existsByDocumentoIdentidad("123456789"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(BusinessExceptionMessage.APPLICANT_NOT_FOUND.getMessage(), error.getMessage());
                })
                .verify();
    }

    // ---------- findAllByIds ----------

    @Test
    void findAllByIdsConListaVacia() {
        StepVerifier.create(usuarioUseCase.findAllByIds(List.of()))
                .expectNext(List.of())
                .verifyComplete();
    }

    @Test
    void findAllByIdsConUsuarios() {
        Usuario u1 = buildUsuario();
        when(usuarioRepository.findAllByIds(anyList())).thenReturn(Flux.just(u1));

        StepVerifier.create(usuarioUseCase.findAllByIds(List.of(1L, 1L, 2L)))
                .expectNextMatches(list -> list.size() == 1 || list.contains(u1))
                .verifyComplete();
    }

    @Test
    void saveUsuarioConPermisoEmptyDebeFallar() {
        Usuario usuario = buildUsuario();
        LoginResponse auth = new LoginResponse(1L, "test@correo.com", 1L, "ADMIN");

        when(rolPermisosRepository.validarPermiso(anyLong(), anyString(), anyBoolean()))
                .thenReturn(Mono.empty());

        StepVerifier.create(usuarioUseCase.saveUsuarios(auth, usuario, "clave123"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(BusinessExceptionMessage.UNAUTHORIZED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioDebeFallarSiNombresVacios() {
        Usuario usuario = buildUsuario().toBuilder().nombres("").build();
        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "clave123"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.NAMES_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioDebeFallarSiApellidosVacios() {
        Usuario usuario = buildUsuario().toBuilder().apellidos("").build();

        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "clave123"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.LASTNAMES_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioDebeFallarSiEmailVacio() {
        Usuario usuario = buildUsuario().toBuilder().correoElectronico("").build();

        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "clave123"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.EMAIL_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioDebeFallarSiEmailInvalido() {
        Usuario usuario = buildUsuario().toBuilder().correoElectronico("no-valido").build();

        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "clave123"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.EMAIL_INVALID.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioDebeFallarSiSalarioNull() {
        Usuario usuario = buildUsuario().toBuilder().salarioBase(null).build();

        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "clave123"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(ValidationException.class, error);
                    assertEquals(ValidationExceptionMessage.SALARY_REQUIRED.getMessage(), error.getMessage());
                })
                .verify();
    }

    @Test
    void saveUsuarioDebeFallarSiSalarioFueraDeRango() {
        Usuario usuario = buildUsuario().toBuilder().salarioBase(20_000_000D).build();

        when(passwordEncoderGateway.encode(any())).thenReturn(Mono.just("hash-dummy"));

        StepVerifier.create(usuarioUseCase.saveUsuarios(usuario, "clave123"))
                .expectErrorSatisfies(error -> {
                    assertInstanceOf(BusinessException.class, error);
                    assertEquals(BusinessExceptionMessage.SALARY_OUT_OF_RANGE.getMessage(), error.getMessage());
                })
                .verify();
    }

}
