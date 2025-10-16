package com.legendme.login.svc.domain.usecase;

import com.legendme.login.svc.domain.model.AuthTokens;

/**
 * Define un caso de uso para autenticar a un usuario utilizando un token de Google.
 */
public interface AuthenticateWithGoogle {

    /**
     * Representa el resultado de la autenticación con Google.
     *
     * @param userId El identificador único del usuario autenticado.
     * @param email El correo electrónico del usuario autenticado.
     * @param name El nombre del usuario autenticado.
     * @param tokens Los tokens de autenticación asociados al usuario.
     */
    record Result(java.util.UUID userId, String email, String name, AuthTokens tokens) {}

    /**
     * Autentica a un usuario utilizando un token de identificación de Google.
     *
     * @param idToken El token de identificación proporcionado por Google.
     * @return Un objeto {@link Result} que contiene los datos del usuario autenticado y los tokens de autenticación.
     */
    Result authenticate(String idToken);
}
