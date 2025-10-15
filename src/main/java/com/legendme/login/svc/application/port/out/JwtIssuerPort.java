package com.legendme.login.svc.application.port.out;

import com.legendme.login.svc.domain.model.AuthTokens;

import java.util.UUID;

/**
 * Puerto que define la emisión de tokens JWT.
 * Este puerto se utiliza para generar tokens de acceso y de actualización, así como para emparejarlos.
 */
public interface JwtIssuerPort {
    /**
     * Genera un token de acceso (JWT) para el usuario especificado.
     *
     * @param userId El identificador único del usuario.
     * @param email El correo electrónico del usuario.
     * @param name El nombre del usuario.
     * @return El token de acceso generado como cadena.
     */
    String issueAccessToken(UUID userId, String email, String name);

    /**
     * Genera un token de actualización (JWT) para el usuario especificado.
     *
     * @param userId El identificador único del usuario.
     * @return El token de actualización generado como cadena.
     */
    String issueRefreshToken(UUID userId);

    /**
     * Genera un conjunto de tokens de autenticación (acceso y actualización) para el usuario especificado.
     *
     * @param userId El identificador único del usuario.
     * @param email El correo electrónico del usuario.
     * @param name El nombre del usuario.
     * @return Un objeto {@link AuthTokens} que contiene el token de acceso y el token de actualización.
     */
    default AuthTokens pair(UUID userId, String email, String name) {
        return new AuthTokens(issueAccessToken(userId, email, name), issueRefreshToken(userId));
    }
}
