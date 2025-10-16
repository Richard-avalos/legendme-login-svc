package com.legendme.login.svc.shared;

import com.legendme.login.svc.shared.dto.Errors;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejador global de excepciones para la aplicación.
 * Proporciona control centralizado de excepciones y genera respuestas adecuadas
 * para los errores que ocurren durante la ejecución.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Maneja las excepciones de tipo {@link ErrorException}.
     *
     * @param ex  La excepción lanzada.
     * @param req La solicitud HTTP que provocó la excepción.
     * @return Una respuesta HTTP con el estado y el cuerpo del error.
     */
    @ExceptionHandler(ErrorException.class)
    public ResponseEntity<Errors> handleErrorException(ErrorException ex, HttpServletRequest req) {
        HttpStatus status = ex.status() != null ? ex.status() : HttpStatus.BAD_REQUEST;
        Errors body = new Errors(
                status.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Maneja las excepciones de tipo {@link IllegalArgumentException}.
     *
     * @param ex  La excepción lanzada.
     * @param req La solicitud HTTP que provocó la excepción.
     * @return Una respuesta HTTP con el estado y el cuerpo del error.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Errors> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        Errors body = new Errors(
                status.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(status).body(body);
    }


}
