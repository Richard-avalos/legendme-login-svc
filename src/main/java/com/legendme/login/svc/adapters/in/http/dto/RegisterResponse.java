package com.legendme.login.svc.adapters.in.http.dto;

import java.util.UUID;

/**
 * DTO que representa la respuesta devuelta al cliente tras completar el registro de un nuevo usuario.
 *
 * Este objeto es enviado como resultado exitoso del proceso de creación de cuenta local
 * (o potencialmente de registro mediante proveedores externos en futuras implementaciones).
 * Contiene la información básica del usuario recién registrado junto con su estado actual en el sistema.
 *
 * Funcionalidad principal:
 * 1. Transporta los datos confirmados del usuario creado.
 * 2. Permite al cliente identificar al nuevo usuario mediante su userId.
 * 3. Informa el estado del registro (por ejemplo, "ACTIVE", "PENDING_VERIFICATION", etc.).
 *
 * Campos:
 * - userId: identificador único asignado al usuario dentro del sistema.
 * - firstName: nombre del usuario registrado.
 * - lastName: apellido del usuario registrado.
 * - email: correo electrónico asociado al nuevo usuario.
 * - status: estado actual del usuario (por ejemplo, "ACTIVE" tras un registro exitoso).
 *
 * Ejemplo de uso:
 * Se retorna como respuesta del endpoint `/register` al completar el proceso de creación de usuario.
 */
public record RegisterResponse (
        UUID userId,
        String username,
        String email
){}
