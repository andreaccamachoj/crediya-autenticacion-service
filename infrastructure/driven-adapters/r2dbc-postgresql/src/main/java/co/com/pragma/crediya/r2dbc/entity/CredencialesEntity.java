package co.com.pragma.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table("credenciales")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CredencialesEntity {

    @Id
    @Column("id_credenciales")
    private Long idCredenciales;
    @Column("id_usuario")
    private Long idUsuario;
    @Column("correo_electronico")
    private String correoElectronico;
    @Column("clave")
    private String clave;
    @Column("intentos_fallidos")
    private Integer intentosFallidos;
    @Column("bloqueado")
    private Boolean bloqueado;
    @Column("fecha_bloqueo")
    private Timestamp fechaBloqueo;

}
