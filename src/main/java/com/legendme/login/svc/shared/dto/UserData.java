package com.legendme.login.svc.shared.dto;

import java.util.List;

/**
 * Representa los datos de un usuario en el sistema.
 *
 * @param userId El identificador único del usuario.
 * @param email El correo electrónico del usuario.
 * @param name El nombre completo del usuario.
 * @param roles La lista de roles asociados al usuario.
 */
public record UserData(Long userId, String email, String name, List<String> roles) {

}

