package co.com.pragma.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("permisos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PermisosEntity {

    @Id
    @Column("id_permisos")
    private Long idPermisos;

    @Column("codigo")
    private String codigo;

    @Column("nombre")
    private String nombre;

    @Column("descripcion")
    private String descripcion;

    @Column("indhabilitado")
    private Boolean indHabilitado;
}
