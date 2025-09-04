package co.com.pragma.crediya.model.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessExceptionMessage {
    EMAIL_ALREADY_REGISTERED(
            "BUS0001", "Email already registered", "409",
            "El correo electrónico ya está registrado para otro solicitante."
    ),
    APPLICANT_NOT_FOUND(
            "BUS0002", "Applicant not found", "404",
            "No se encontró el usuario solicitado."
    ),
    SALARY_OUT_OF_RANGE(
            "BUS0003", "Salary violates allowed range [0, 15000000]", "422",
            "El salario_base está fuera del rango permitido."
    ),
    BUSINESS_RULE_VIOLATION(
            "BUS0099", "Business rule violation", "422",
            "La operación no cumple una regla de negocio."
    ),
    ROLE_NOT_FOUND("BUS0004","Role not found","404",
            "El rol especificado no existe."
    ),
    USER_NOT_FOUND("BUS0005","User not found","404",
            "El usuario con ese numero de identificacion no existe."
    ),
    INVALID_PASSWORD("BUS0006","Incorrect password","404",
            "Contraseña incorrecta."),
    INVALID_TOKEN("BUS0007","Invalid Token","404",
            "Token invalido."),
    UNAUTHORIZED("BUS0008","Unauthorized","404",
            "Usuario inautorizado."),
    SESSION_ERROR("BUS0009","Sesion Error","404",
            "Ocurrio un error al actualizar la sesion."),
    SESSION_EXPIRED("BUS0009","Sesion Expired","404",
            "Sesion expirada."),
    USER_SERVICE_ERROR("BUS0010","Service Error","404",
            "Erro de servicio."),
    ACCOUNT_LOCKED("BUS0011","Account Locked","404",
            "Usuario bloqueado.");

    private final String code;
    private final String description;
    private final String itcCode;
    private final String message;
}
