package co.com.pragma.crediya.api;

import co.com.pragma.crediya.api.config.path.LoginPath;
import co.com.pragma.crediya.api.dto.request.LoginRequest;
import co.com.pragma.crediya.model.usuario.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class AuthRouterRest {

    private final AuthHandler authHandler;
    private final LoginPath loginPath;

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/login",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.POST,
                    beanClass = AuthHandler.class,
                    beanMethod = "login",
                    operation = @Operation(
                            operationId = "login",
                            summary = "Autenticación de usuario",
                            description = "Recibe credenciales (username y password) y devuelve un JWT.",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = LoginRequest.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Login exitoso"
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Credenciales inválidas",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    )
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/validate",
                    produces = MediaType.APPLICATION_JSON_VALUE,
                    method = RequestMethod.GET,
                    beanClass = AuthHandler.class,
                    beanMethod = "validate",
                    operation = @Operation(
                            operationId = "validateToken",
                            summary = "Validar token",
                            description = "Valida un token JWT y devuelve los datos del usuario.",
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Token válido",
                                            content = @Content(schema = @Schema(implementation = Usuario.class))
                                    ),
                                    @ApiResponse(
                                            responseCode = "401",
                                            description = "Token inválido o ausente",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
                                    )
                            },
                            security = { @SecurityRequirement(name = "bearerAuth") }
                    )
            )
    })
    public RouterFunction<ServerResponse> routes() {
        return route(POST(loginPath.getLogin()), authHandler::login)
                .andRoute(GET(loginPath.getValidateToken()), authHandler::validate);
    }
}

