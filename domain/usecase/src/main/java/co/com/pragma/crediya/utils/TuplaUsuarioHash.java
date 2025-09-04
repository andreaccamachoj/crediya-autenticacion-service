package co.com.pragma.crediya.utils;

import co.com.pragma.crediya.model.usuario.Usuario;

public record TuplaUsuarioHash(Usuario usuario, String hash) {
    public static TuplaUsuarioHash of(Usuario u, String h) {
        return new TuplaUsuarioHash(u, h);
    }
}

