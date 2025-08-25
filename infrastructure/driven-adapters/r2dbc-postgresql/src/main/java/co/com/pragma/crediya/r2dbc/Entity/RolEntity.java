package co.com.pragma.crediya.r2dbc.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("rol")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RolEntity {

    @Id
    @Column("id_rol")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;
    private String nombre;
    private String descripcion;
}
