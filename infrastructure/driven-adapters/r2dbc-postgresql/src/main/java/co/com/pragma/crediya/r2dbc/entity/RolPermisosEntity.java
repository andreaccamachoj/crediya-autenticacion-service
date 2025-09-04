package co.com.pragma.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("rol_permisos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RolPermisosEntity {
    @Id
    @Column("id_rol_permisos")
    private Long idRolPermisos;
    @Column("id_rol")
    private Long idRol;
    @Column("id_permisos")
    private Long idPermisos;
    @Column("indhabilitado")
    private Boolean indHabilitado;
}
