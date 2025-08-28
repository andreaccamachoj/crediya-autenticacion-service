package co.com.pragma.crediya.model.exception.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ValidationExceptionMessage {
    NAMES_REQUIRED(
      "VAL0001", "Field 'nombres' is required", "400",
              "El campo 'nombres' no puede estar vacío."
    ),
    LASTNAMES_REQUIRED(
      "VAL0002", "Field 'apellidos' is required", "400",
              "El campo 'apellidos' no puede estar vacío."
    ),
    EMAIL_REQUIRED(
      "VAL0003", "Field 'correo_electronico' is required", "400",
              "El campo 'correo_electronico' no puede estar vacío."
    ),
    EMAIL_INVALID(
      "VAL0004", "Invalid email format", "400",
              "Debe proporcionar un correo electrónico válido."
    ),
    SALARY_REQUIRED(
      "VAL0005", "Field 'salario_base' is required", "400",
              "El campo 'salario_base' no puede ser nulo."
    ),
    SALARY_NOT_NUMERIC(
      "VAL0006", "Salary must be numeric", "400",
              "El campo 'salario_base' debe ser numérico."
    ),
    SALARY_OUT_OF_RANGE(
      "VAL0007", "Salary out of allowed range [0, 15000000]", "422",
              "El 'salario_base' debe estar entre 0 y 15000000."
    ),
    BIRTHDATE_REQUIRED(
      "VAL0008", "Field 'fecha_nacimiento' is required", "400",
              "La 'fecha_nacimiento' no puede ser nula."
    ),
    BIRTHDATE_IN_FUTURE(
      "VAL0009", "Birthdate must be in the past", "400",
              "La 'fecha_nacimiento' debe ser una fecha pasada."
    ),
    ROL_REQUIRED(
      "VAL0010", "Rol is required", "400",
              "La 'fecha_nacimiento' debe ser una fecha pasada."
    );

    private final String code;
    private final String description;
    private final String itcCode;
    private final String message;
}
