package co.com.pragma.crediya.r2dbc.Entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table(name ="usuario")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UsuarioEntity {

    @Id
    @Column("id_usuario")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;
    private String nombres;
    private String apellidos;
    @Column("correo_electronico")
    private String correoElectronico;
    @Column("documento_identidad")
    private String documentoIdentidad;
    @Column("fecha_nacimiento")
    private Timestamp fechaNacimiento;
    private String telefono;
    @Column("id_rol")
    private Long idRol;
    private String direccion;
    @Column("salario_base")
    private Double salarioBase;
}
