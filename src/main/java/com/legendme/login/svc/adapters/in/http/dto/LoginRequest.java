package com.legendme.login.svc.adapters.in.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

/**
 * DTO de entrada para el endpoint de autenticación por email y password.
 *
 * Campos:
 * - email: correo del usuario. Reglas: @Email y @NotBlank.
 * - password: contraseña del usuario. Regla: @NotBlank.
 * - Se ejecuta mediante Bean Validation (Jakarta Validation) cuando el controlador utiliza @Validated.
 * - En POST /auth/login este payload se utiliza para autenticar al usuario y emitir un JWT.
 */
public record LoginRequest (
        String name,
        String lastName,
        String username,
        Date birthDate,
        @Email @NotBlank String email,
        String provider,
        Boolean active,
        @NotBlank String password
){}
