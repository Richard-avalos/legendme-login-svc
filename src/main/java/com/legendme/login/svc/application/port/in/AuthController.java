package com.legendme.login.svc.application.port.in;

import jakarta.validation.constraints.NotBlank;

/**
 * Controlador de autenticación que define las operaciones de entrada relacionadas con el proceso de login.
 */
public interface AuthController {
    /**
     * Representa la solicitud para iniciar sesión con Google.
     *
     * @param idToken El token de identificación proporcionado por Google. No puede estar vacío.
     */
    record GoogleSignInRequest(@NotBlank String idToken){}

    /**
     * Representa la respuesta de autenticación, incluyendo los tokens y datos del usuario autenticado.
     *
     * @param accessToken El token de acceso generado.
     * @param refreshToken El token de actualización generado.
     * @param userId El identificador único del usuario.
     * @param email El correo electrónico del usuario.
     * @param name El nombre del usuario.
     */
    record AuthResponse(String accessToken, String refreshToken, Long userId, String email, String name) {}
}
