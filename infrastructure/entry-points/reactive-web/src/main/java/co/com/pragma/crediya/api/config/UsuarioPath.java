package co.com.pragma.crediya.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class UsuarioPath {
    private String usuarios;
    private String usuariosById;
    private String byDocumento = "/api/v1/usuario/{documentoIdentidad}";
}
