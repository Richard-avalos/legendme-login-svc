package com.legendme.login.svc.adapters.in.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Solicitud para registrar un nuevo usuario.
 * Contiene los datos necesarios para crear una cuenta en el sistema.
 * Reglas de validación:
 * - `email` debe tener formato válido y no puede estar vacío.
 * - `password` no puede estar vacío.
 * @param name nombre del usuario.
 * @param lastName apellido del usuario.
 * @param username nombre de usuario.
 * @param birthDate fecha de nacimiento del usuario.
 * @param email correo electrónico del usuario.
 * @param password contraseña del usuario.
 */
public record RegisterRequest (
        String name,
        String lastName,
        String username,
        Date birthDate,
        @Email @NotBlank String email,
        @NotBlank String password
){}