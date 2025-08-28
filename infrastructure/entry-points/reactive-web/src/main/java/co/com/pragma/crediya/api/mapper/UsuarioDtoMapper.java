package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.dto.UsuarioDto;
import co.com.pragma.crediya.api.dto.UsuarioResponse;
import co.com.pragma.crediya.model.usuario.Usuario;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsuarioDtoMapper {

    UsuarioDto toDtoResponse(Usuario usuarios);
    UsuarioResponse toResponse(Usuario usuarios);
}
