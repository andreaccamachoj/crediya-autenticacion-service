package co.com.pragma.crediya.model.permisos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Permisos {

    private Long idPermisos;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean indHabilitado;

}
