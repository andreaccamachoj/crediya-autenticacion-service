package co.com.pragma.crediya.api.config.path;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "routes.paths")
public class LoginPath {
    private String login;
    private String validateToken = "/api/v1/validate";
}
