package co.com.pragma.crediya.usecase.enums;

public enum PermisoCodigo {

    CREAR_USUARIO("CREAR_USUARIO"),
    CREAR_SOLICITUD("CREAR_SOLICITUD"),
    GESTIONAR_SOLICITUD("GESTIONAR_SOLICITUD");

    private final String codigo;

    PermisoCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static PermisoCodigo fromCodigo(String value) {
        for (PermisoCodigo p : values()) {
            if (p.codigo.equalsIgnoreCase(value)) {
                return p;
            }
        }
        return null;
    }
}

