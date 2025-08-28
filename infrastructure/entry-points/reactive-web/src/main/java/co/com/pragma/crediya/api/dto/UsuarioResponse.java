package co.com.pragma.crediya.api.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioResponse {
    private Long idUsuario;
    private String documentoIdentidad;
    private String correoElectronico;
}