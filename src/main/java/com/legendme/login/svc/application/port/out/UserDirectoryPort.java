package com.legendme.login.svc.application.port.out;

import com.legendme.login.svc.adapters.out.http.dto.UserResponse;
import com.legendme.login.svc.shared.dto.GoogleUserPayload;
import com.legendme.login.svc.shared.dto.UserData;

/**
 * Puerto que define las operaciones relacionadas con el directorio de usuarios.
 * Este puerto se utiliza para gestionar y actualizar la información de los usuarios en el sistema.
 */
public interface UserDirectoryPort {
    /**
     * Inserta o actualiza un usuario en el sistema utilizando los datos proporcionados por Google.
     *
     * @param googleUserPayload Los datos del usuario obtenidos de Google.
     * @return Un objeto {@link UserData} que contiene la información actualizada del usuario.
     */
    UserResponse upsertGoogleUser(GoogleUserPayload googleUserPayload);

    UserResponse findByEmail(String email);

    UserResponse createLocalUser(String firstName, String lastName, String username, String email, String password);
}
