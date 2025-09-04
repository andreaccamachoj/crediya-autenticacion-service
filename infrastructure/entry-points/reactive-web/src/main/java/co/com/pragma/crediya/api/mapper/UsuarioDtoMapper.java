package co.com.pragma.crediya.api.mapper;

import co.com.pragma.crediya.api.dto.UsuarioDto;
import co.com.pragma.crediya.api.dto.UsuarioResponse;
import co.com.pragma.crediya.api.dto.request.UsuarioRequest;
import co.com.pragma.crediya.api.dto.response.UsuarioDemoraficoResponse;
import co.com.pragma.crediya.model.usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioDtoMapper {

    UsuarioDto toDtoResponse(Usuario usuarios);

    @Mapping(target = "idUsuario", ignore = true)
    Usuario toDomain(UsuarioRequest req);

    UsuarioResponse toResponse(Usuario user);

    UsuarioDemoraficoResponse toUsuarioDemoraficoResponse(Usuario user);

}
