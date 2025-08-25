package co.com.pragma.crediya.api.dto;

import jakarta.validation.constraints.*;

import java.sql.Timestamp;

public record CrearUsuarioDto(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank @Email String correoElectronico,
        @NotBlank String documentoIdentidad,
        @NotNull @Past Timestamp fechaNacimiento,
        String telefono,
        @NotNull Long idRol,
        String direccion,
        @NotNull @DecimalMin("0") @DecimalMax("15000000") Double salarioBase) {
}
