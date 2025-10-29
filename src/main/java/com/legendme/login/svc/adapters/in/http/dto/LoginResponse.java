package com.legendme.login.svc.adapters.in.http.dto;

import java.util.UUID;

/**
 * Respuesta del servicio de inicio de sesión.
 * Contiene el token de acceso y los datos básicos del usuario autenticado.
 *
 * <p>Se utiliza como DTO en la capa HTTP para devolver el resultado del inicio de sesión.</p>
 *
 * @param accessToken Token JWT emitido tras la autenticación.
 * @param tokenType Tipo de token, p. ej., "Bearer".
 * @param userId Identificador único del usuario.
 * @param email Correo electrónico del usuario.
 * @param name Nombre para mostrar del usuario.
 */
public record LoginResponse(
        String accessToken,
        String tokenType,
        UUID userId,
        String email,
        String name
) {}