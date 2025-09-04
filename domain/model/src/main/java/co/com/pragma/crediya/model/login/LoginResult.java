package co.com.pragma.crediya.model.login;

import java.sql.Timestamp;

public record LoginResult(String token, Long idUsuario, Timestamp expiracion, String rolNombre) {}

