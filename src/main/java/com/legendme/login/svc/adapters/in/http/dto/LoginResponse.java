package com.legendme.login.svc.adapters.in.http.dto;

import java.util.UUID;

/**
 * DTO que representa la respuesta enviada al cliente tras un inicio de sesión exitoso.
 *
 * Este objeto se devuelve como resultado del proceso de autenticación, conteniendo la
 * información esencial del token JWT emitido por el sistema y los datos básicos del usuario autenticado.
 *
 * Funcionalidad principal:
 * 1. Transporta el token de acceso (JWT) generado por el servicio de autenticación.
 * 2. Informa el tipo de token (por ejemplo, "Bearer") para uso en encabezados HTTP posteriores.
 * 3. Indica el tiempo de expiración del token en segundos.
 * 4. Incluye el identificador único del usuario y su correo electrónico para referencia del cliente.
 *
 * Campos:
 * - accessToken: token JWT emitido tras la autenticación.
 * - tokenType: tipo de token (comúnmente "Bearer").
 * - expiresIn: duración del token en segundos antes de expirar.
 * - userId: identificador único del usuario autenticado.
 * - email: dirección de correo electrónico del usuario.
 *
 * Ejemplo de uso:
 * Se retorna como respuesta del endpoint `/login` o `/auth/google/callback` dentro del servicio de autenticación.
 */

public record LoginResponse(
        String accessToken,
        String tokenType,
        UUID userId,
        String email,
        String name
) {}