package co.com.pragma.crediya.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.sql.Timestamp;

@Table("token_sesion")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TokenSesionEntity {

    @Id
    @Column("id_token_sesion")
    private Long idTokenSesion;

    @Column("id_usuario")
    private Long idUsuario;

    @Column("token")
    private String token;

    @Column("fecha_creacion")
    private Timestamp fechaCreacion;

    @Column("fecha_expiracion")
    private Timestamp fechaExpiracion;

    @Column("indactivo")
    private Boolean indActivo;
}
