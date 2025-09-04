package co.com.pragma.crediya.model.login;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoginResponse {

    private Long idUsuario;
    private String correoElectronico;
    private Long idRol;
    private String nombreRol;

}
