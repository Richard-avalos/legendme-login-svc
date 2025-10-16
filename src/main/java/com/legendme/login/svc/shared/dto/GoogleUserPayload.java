package com.legendme.login.svc.shared.dto;

/**
 * Representa los datos de un usuario obtenidos de Google.
 *
 * @param googleSub El identificador único del usuario proporcionado por Google.
 * @param email El correo electrónico del usuario.
 * @param name El nombre completo del usuario.
 * @param picture La URL de la imagen de perfil del usuario.
 * @param emailVerified Indica si el correo electrónico del usuario ha sido verificado.
 */
public record GoogleUserPayload(String googleSub, String email, String name, String picture, boolean emailVerified) {
}
