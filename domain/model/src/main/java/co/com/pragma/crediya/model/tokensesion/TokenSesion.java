package co.com.pragma.crediya.model.tokensesion;

import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TokenSesion {

    private Long idTokenSesion;
    private Long idUsuario;
    private String token;
    private Timestamp fechaCreacion;
    private Timestamp fechaExpiracion;
    private boolean indActivo;

}
