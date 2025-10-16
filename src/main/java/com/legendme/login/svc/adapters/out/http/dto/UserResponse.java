package com.legendme.login.svc.adapters.out.http.dto;

import java.util.Date;
import java.util.UUID;

/**
 * Representa la respuesta del servicio de usuarios (User Directory Service)
 * que contiene la información pública y actualizada de un usuario dentro del sistema.
 *
 * Este record se utiliza principalmente como DTO (Data Transfer Object)
 * en las respuestas HTTP del servicio, o como resultado de operaciones
 * como creación, actualización o consulta de usuarios.
 *
 * Campos:
 * - id: identificador único del usuario en formato UUID.
 * - name: nombre del usuario.
 * - lastname: apellido del usuario.
 * - birthDate: fecha de nacimiento del usuario (puede ser nula si no se registró).
 * - username: nombre de usuario único en el sistema.
 * - email: correo electrónico del usuario.
 * - provider: origen de autenticación (por ejemplo: "LOCAL" o "GOOGLE").
 * - active: indica si el usuario está activo en el sistema.
 * - createdAt: fecha de creación del registro de usuario.
 * - updatedAt: fecha de la última actualización del registro.
 *
 */
public record UserResponse(UUID id, String name, String lastname, Date birthDate, String username, String email, String provider, boolean active, Date createdAt, Date updatedAt) {
}
