package co.com.pragma.crediya.model.rol;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Rol {

    private String nombre;
    private String descripcion;

}
