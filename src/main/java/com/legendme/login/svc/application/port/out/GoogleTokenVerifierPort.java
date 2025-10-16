package com.legendme.login.svc.application.port.out;

/**
 * Puerto que define la verificación de tokens de Google.
 * Este puerto se utiliza para validar un token de identificación proporcionado por Google
 * y obtener información del usuario autenticado.
 */
public interface GoogleTokenVerifierPort {
    /**
     * Representa un usuario de Google verificado tras la validación del token.
     *
     * @param sub El identificador único del usuario proporcionado por Google.
     * @param email El correo electrónico del usuario.
     * @param emailVerified Indica si el correo electrónico del usuario ha sido verificado.
     * @param name El nombre completo del usuario.
     * @param picture La URL de la imagen de perfil del usuario.
     */
    record VerifiedGoogleUser(String sub, String email, boolean emailVerified, String name, String picture){}

    /**
     * Verifica un token de identificación de Google y devuelve los datos del usuario autenticado.
     *
     * @param idToken El token de identificación proporcionado por Google.
     * @return Un objeto {@link VerifiedGoogleUser} que contiene los datos del usuario autenticado.
     */
    VerifiedGoogleUser verify(String idToken);
}
