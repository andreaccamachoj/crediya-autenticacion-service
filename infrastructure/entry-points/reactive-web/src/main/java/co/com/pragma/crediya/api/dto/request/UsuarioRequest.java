package co.com.pragma.crediya.api.dto.request;

import java.sql.Timestamp;

public record UsuarioRequest(
        String nombres,
        String apellidos,
        String correoElectronico,
        String documentoIdentidad,
        Timestamp fechaNacimiento, // ISO-8601: "1995-11-03"
        String telefono,
        Long idRol,
        String direccion,
        Double salarioBase,
        String clave
) {}