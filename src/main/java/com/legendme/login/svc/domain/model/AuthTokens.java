package com.legendme.login.svc.domain.model;

/**
 * Representa un conjunto de tokens de autenticación, que incluye:
 * - Un token de acceso (`accessToken`) utilizado para autorizar solicitudes.
 * - Un token de actualización (`refreshToken`) utilizado para obtener un nuevo token de acceso.
 *
 * Este registro es inmutable y genera automáticamente un constructor, métodos de acceso,
 * `equals()`, `hashCode()` y `toString()`.
 *
 * Ejemplo de uso:
 *
 * AuthTokens tokens = new AuthTokens("access123", "refresh456");
 * String access = tokens.accessToken(); // Devuelve "access123"
 * String refresh = tokens.refreshToken(); // Devuelve "refresh456"
 *
 *
 * @param accessToken El token de acceso.
 * @param refreshToken El token de actualización.
 */
public record AuthTokens (String accessToken, String refreshToken){ }
