package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.config.UsuarioPath;
import co.com.pragma.crediya.api.dto.CrearUsuarioDto;
import co.com.pragma.crediya.model.usuario.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    private final UsuarioPath usuariosPath;
    private final Handler usuarioHandler;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/usuarios",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "listenSaveUsuario",
                    operation = @Operation(
                            operationId = "createUsuario",
                            summary = "Crear usuario",
                            description = "Crea un usuario si el correo no existe y el rol es válido.",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = CrearUsuarioDto.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "usuario_minimo",
                                                            summary = "Ejemplo mínimo",
                                                            value = """
                                {
                                   "nombres": "Andrés",
                                   "apellidos": "Ramírez",
                                   "correoElectronico": "andres.ramirez@example.com",
                                   "documentoIdentidad": "1012345678",
                                   "fechaNacimiento": "1987-09-08",
                                   "telefono": "+57 3159876543",
                                   "idRol": 3,
                                   "direccion": "Avenida 30 #12-40, Barranquilla",
                                   "salarioBase": 4200000.00,
                                   "clave": "qwerty"
                                }"""
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario creado",
                                            content = @Content(
                                                    schema = @Schema(implementation = Usuario.class),
                                                    examples = {
                                                            @ExampleObject(
                                                                    name = "usuario_creado",
                                                                    value = """
                                        {
                                          "id": 1,
                                          "nombres": "Ana",
                                          "apellidos": "García",
                                          "email": "ana@example.com",
                                          "salarioBase": 4500000,
                                          "idRol": 2,
                                          "documentoIdentidad": "1020304050"
                                        }"""
                                                            )
                                                    }
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "400",
                                            description = "Error de validación en los datos"
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "El correo electrónico ya está registrado"
                                    )
                            },
                            security = { @SecurityRequirement(name = "bearerAuth") }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuario/{documentoIdentidad}",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = org.springframework.web.bind.annotation.RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getUsuario",
                    operation = @Operation(
                            operationId = "getUsuarioPorDocumento",
                            summary = "Obtener usuario por documento de identidad",
                            description = "Consulta un usuario existente a partir de su documento de identidad.",
                            parameters = {
                                    @Parameter(
                                            name = "documentoIdentidad",
                                            in = ParameterIn.PATH,
                                            required = true,
                                            description = "Documento de identidad del usuario",
                                            schema = @Schema(type = "string", example = "1020304050")
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuario encontrado",
                                            content = @Content(schema = @Schema(implementation = Usuario.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "Usuario no encontrado"
                                    )
                            },
                            security = { @SecurityRequirement(name = "bearerAuth") }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/usuario/listUsuarios",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = org.springframework.web.bind.annotation.RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "findByIds",
                    operation = @Operation(
                            operationId = "findUsuariosByIds",
                            summary = "Obtener usuarios por lista de IDs",
                            description = "Recibe una lista de identificadores y devuelve los usuarios correspondientes.",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = java.util.List.class),
                                            examples = {
                                                    @ExampleObject(
                                                            name = "lista_ids",
                                                            value = "[1, 2, 3]"
                                                    )
                                            }
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Usuarios encontrados",
                                            content = @Content(
                                                    schema = @Schema(implementation = Usuario.class)
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "404",
                                            description = "No se encontraron usuarios para los IDs proporcionados"
                                    )
                            },
                            security = { @SecurityRequirement(name = "bearerAuth") }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST(usuariosPath.getUsuarios()), usuarioHandler::listenSaveUsuario)
                .andRoute(GET(usuariosPath.getByDocumento()), usuarioHandler::getUsuario)
                .andRoute(POST(usuariosPath.getListUsuariosByIds()), usuarioHandler::findByIds);
    }
}

