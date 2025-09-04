package co.com.pragma.crediya.model.credenciales;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Credenciales {

    private Long idCredenciales;
    private Long idUsuario;
    private String clave;
    private String correoElectronico;

}
