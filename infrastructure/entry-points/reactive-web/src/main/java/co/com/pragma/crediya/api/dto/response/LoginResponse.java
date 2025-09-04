package co.com.pragma.crediya.api.dto.response;

import java.sql.Timestamp;

public record LoginResponse(String token, Long idUsuario, Timestamp expiracion, String rolNombre) {}

