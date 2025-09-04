package co.com.pragma.crediya.usecase.permisos;

import co.com.pragma.crediya.model.rolpermisos.gateways.RolPermisosRepository;
import co.com.pragma.crediya.model.tokensesion.gateways.TokenSesionGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PermisosUseCase {
    private final TokenSesionGateway tokenSesionGateway;
    private final RolPermisosRepository rolPermisosRepository;
}
