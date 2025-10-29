package com.legendme.login.svc.application.port.out;

import com.legendme.login.svc.adapters.in.http.dto.RegisterRequest;
import com.legendme.login.svc.adapters.out.http.dto.UserResponse;
import com.legendme.login.svc.shared.dto.GoogleUserPayload;
import com.legendme.login.svc.shared.dto.UserData;
import java.util.UUID;

/**
 * Puerto que define las operaciones relacionadas con el directorio de usuarios.
 * Este puerto se utiliza para gestionar y actualizar la información de los usuarios en el sistema.
 */
public interface UserDirectoryPort {
    record RegisterResponse(UUID id, String username, String email) {}

    /**
     * Inserta o actualiza un usuario en el sistema utilizando los datos proporcionados por Google.
     *
     * @param googleUserPayload Los datos del usuario obtenidos de Google.
     * @return Un objeto {@link UserData} que contiene la información actualizada del usuario.
     */
    UserResponse upsertGoogleUser(GoogleUserPayload googleUserPayload);

    Boolean existByEmail(String email);

    Boolean existByUsername(String username);

    RegisterResponse createLocalUser(RegisterRequest registerRequest);
}
