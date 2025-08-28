package co.com.pragma.crediya.model.usuario;
import lombok.*;
import java.sql.Timestamp;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Usuario {

    private Long idUsuario;
    private String nombres;
    private String apellidos;
    private String correoElectronico;
    private String documentoIdentidad;
    private Timestamp fechaNacimiento;
    private String telefono;
    private Long idRol;
    private String direccion;
    private Double salarioBase;

}

