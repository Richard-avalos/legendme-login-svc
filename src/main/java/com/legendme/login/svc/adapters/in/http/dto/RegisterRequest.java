package com.legendme.login.svc.adapters.in.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO que representa la solicitud de registro de un nuevo usuario en el sistema.
 *
 * Este objeto se recibe desde el cliente al momento de crear una cuenta local
 * (no asociada a un proveedor externo como Google). Contiene la información básica
 * necesaria para crear el usuario en el servicio de autenticación.
 *
 * Funcionalidad principal:
 * 1. Transporta los datos de registro enviados por el cliente.
 * 2. Aplica validaciones mediante anotaciones Jakarta Validation (@NotBlank, @Email, @Size).
 * 3. Es utilizado por el controlador HTTP para validar y procesar el alta de un nuevo usuario.
 *
 * Campos:
 * - firstName: nombre del usuario. Obligatorio.
 * - lastName: apellido del usuario. Obligatorio.
 * - email: correo electrónico válido y único del usuario. Obligatorio.
 * - password: contraseña en texto claro (será validada y hasheada en el servicio). Debe tener entre 8 y 100 caracteres.
 *
 * Ejemplo de uso:
 * Se recibe en el endpoint `/register` del servicio de autenticación.
 */
public record RegisterRequest (
        @NotBlank String firstName,
        @NotBlank String lastName,
        @Email @NotBlank String email,
        @NotBlank @Size(min = 8, max = 100) String password
) {}
