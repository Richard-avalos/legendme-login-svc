package com.legendme.login.svc.application.service;

import com.legendme.login.svc.adapters.in.http.dto.RegisterRequest;
import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.domain.usecase.AuthRegisterControllerLocal;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación y registro local.
 * <p>
 * Valida los campos obligatorios del registro y delega en el puerto {@link UserDirectoryPort}
 * la verificación de existencia por email y la creación del usuario.
 * Maneja y propaga errores de negocio mediante {@link ErrorException} y registra eventos con SLF4J.
 * </p>
 * <p>
 * Responsabilidades:
 * - Validar datos de entrada del registro.
 * - Verificar existencia del correo.
 * - Crear usuario local.
 * - Estandarizar el manejo de errores.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateRegisterLocalService implements AuthRegisterControllerLocal {
    private final UserDirectoryPort userDir;

    /**
     * Autentica y registra un usuario en el directorio local
     * Reglas de validación:
     * - \`name\`, \`lastName\`, \`username\` y \`email\` no deben ser nulos ni vacíos.
     * - \`birthDate\` no debe ser nula.
     * - El \`email\` no debe existir previamente.
     * @param registerRequest datos de registro del usuario.
     * @return resultado con \`id\`, \`username\` y \`email\` del usuario creado.
     * @throws ErrorException cuando faltan datos, el email ya existe o ante errores inesperados.
     */
    @Override
    public Result authenticate(RegisterRequest registerRequest) {
        final Boolean exist;
        try {

            log.info("Iniciando la autenticacion local, capa de servicio, request: name {}", registerRequest.name(), registerRequest.lastName(), registerRequest.username(), registerRequest.birthDate(), registerRequest.email());

            if (registerRequest.name() == null || registerRequest.name().isEmpty()) {
                throw new ErrorException("Nombre no proporcionado", "L-REG-01", HttpStatus.BAD_REQUEST);
            }
            if (registerRequest.lastName() == null || registerRequest.lastName().isEmpty()) {
                throw new ErrorException("Apellido no proporcionado", "L-REG-02", HttpStatus.BAD_REQUEST);
            }
            if (registerRequest.username() == null || registerRequest.username().isEmpty()) {
                throw new ErrorException("Usuario no proporcionado", "L-REG-03", HttpStatus.BAD_REQUEST);
            }
            if (registerRequest.birthDate() == null) {
                throw new ErrorException("Fecha de nacimiento no proporcionada", "L-REG-04", HttpStatus.BAD_REQUEST);
            }
            if (registerRequest.email() == null || registerRequest.email().isEmpty()) {
                throw new ErrorException("Correo no proporcionado", "L-REG-06", HttpStatus.BAD_REQUEST);
            }
            exist = userDir.existByEmail(registerRequest.email());
            if (exist) {
                throw new ErrorException("El email ya existe", "L-REG-09", HttpStatus.BAD_REQUEST);
            }
            var createdUser = userDir.createLocalUser(registerRequest);
            return new Result(createdUser.id(), createdUser.username(), createdUser.email());
        } catch (ErrorException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado en la autenticacion local: {}", e.getMessage());
            throw new ErrorException("Error inesperado en la autenticacion local", "L-REG-99", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}