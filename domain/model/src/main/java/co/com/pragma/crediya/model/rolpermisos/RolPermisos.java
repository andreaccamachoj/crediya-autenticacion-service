package co.com.pragma.crediya.model.rolpermisos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RolPermisos {

    private Long idRolPermisos;
    private Long idPermiso;
    private Long idRol;
    private Boolean indHabilitado;

}
