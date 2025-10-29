package com.legendme.login.svc.adapters.in.http.dto;

import java.util.UUID;

/**
 * Respuesta del servicio de registro de usuario.
 * Contiene los datos básicos del usuario registrado.
 * @param userId Identificador único del usuario.
 * @param username Nombre de usuario registrado.
 * @param email Correo electrónico del usuario.
 */
public record RegisterResponse (
        UUID userId,
        String username,
        String email
){}