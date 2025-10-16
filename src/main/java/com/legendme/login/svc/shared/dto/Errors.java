package com.legendme.login.svc.shared.dto;

/**
 * Representa un objeto de error que contiene información sobre el estado y el mensaje de error.
 * Este record se utiliza para encapsular detalles de errores en las respuestas de la API.
 *
 * @param status  El código de estado HTTP asociado con el error.
 * @param message El mensaje descriptivo del error.
 */
public record Errors(
        int status,
        String message
) {}