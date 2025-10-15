package com.legendme.login.svc.adapters.out.http.dto;

import java.util.Date;

/**
 * Está clase representa el DTO para la solicitud de creación o actualización de un usuario.
 * Se utiliza en endpoints RESTful para recibir datos necesarios para registrar un nuevo usuario
 * o actualizar la información de un usuario existente.
 * Incluye información personal, credenciales y proveedor de autenticación.
 *
 * @param name
 * @param lastname
 * @param birthDate
 * @param username
 * @param email
 * @param password
 * @param provider
 */

public record UserRequest(String name, String lastname, Date birthDate, String username, String email, String password, String provider) {

}
