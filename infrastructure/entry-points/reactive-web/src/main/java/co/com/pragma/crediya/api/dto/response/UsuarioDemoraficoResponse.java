package co.com.pragma.crediya.api.dto.response;

public record UsuarioDemoraficoResponse(
        Long idUsuario,
        String nombres,
        String apellidos,
        String correoElectronico,
        String documentoIdentidad,
        String fechaNacimiento,
        String telefono,
        Long idRol,
        String direccion,
        Double salarioBase
) {}

