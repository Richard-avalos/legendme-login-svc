package com.legendme.login.svc.adapters.in.http;

import com.legendme.login.svc.adapters.in.http.dto.RegisterRequest;
import com.legendme.login.svc.adapters.in.http.dto.RegisterResponse;
import com.legendme.login.svc.adapters.out.jpa.CredentialEntity;
import com.legendme.login.svc.adapters.out.jpa.CredentialRepository;
import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Locale;
import java.util.UUID;

/**
 * Controlador HTTP responsable del registro de nuevos usuarios locales.
 *
 * Este componente forma parte del adaptador de entrada del microservicio de autenticación (login-svc),
 * y se encarga de gestionar el proceso completo de creación de usuarios dentro del sistema.
 * Combina operaciones locales (persistencia de credenciales) con llamadas remotas al servicio
 * de usuarios (legendme-users-svc) a través del puerto `UserDirectoryPort`.
 *
 * Funcionalidad principal:
 * 1. Recibe la solicitud de registro desde el cliente en el endpoint `/auth/register`.
 * 2. Valida que el correo no esté previamente registrado en la base de datos local.
 * 3. Crea el usuario en el servicio de usuarios (`UserDirectoryPort.createLocalUser`).
 * 4. Persiste las credenciales (email y contraseña encriptada) en la base de datos local.
 * 5. Retorna un objeto `RegisterResponse` con la información del nuevo usuario y su estado.
 *
 * Componentes principales:
 * - credentialRepository: gestiona el acceso a la tabla de credenciales locales.
 * - passwordEncoder: encripta la contraseña antes de almacenarla.
 * - userDir: puerto que comunica con el servicio externo de usuarios.
 *
 * Excepciones:
 * - Lanza `ErrorException` con código `L-REG-04` si el email ya existe localmente.
 * - Lanza `ErrorException` con código `L-REG-05` si existe un conflicto remoto no resoluble.
 *
 * Endpoint:
 * - POST /auth/register → crea un nuevo usuario local y devuelve su información.
 *
 * Ejemplo de flujo:
 * 1. El cliente envía nombre, apellido, email y contraseña al endpoint `/auth/register`.
 * 2. El servicio valida duplicados locales y remotos.
 * 3. Si to do es válido, crea el usuario en ambos servicios (UsersSvc y LoginSvc).
 * 4. Devuelve un `RegisterResponse` con los datos del usuario y estado “ACTIVE”.
 */

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthRegisterController {
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDirectoryPort userDir;

    /**
     * Endpoint que gestiona el proceso de registro de un nuevo usuario local.
     *
     * Pasos del proceso:
     * 1. Normaliza el email a minúsculas para evitar duplicados por mayúsculas.
     * 2. Verifica si el correo ya existe en la base de datos local.
     * 3. Llama al servicio externo de usuarios para crear el registro correspondiente.
     * 4. En caso de conflicto remoto (409), intenta recuperar el usuario existente.
     * 5. Guarda las credenciales en la base local (con hash de la contraseña).
     * 6. Retorna un objeto {@link RegisterResponse} con los datos del usuario creado.
     *
     * @param req objeto {@link RegisterRequest} con la información de registro.
     * @return ResponseEntity con {@link RegisterResponse} y estado HTTP 201 (CREATED).
     * @throws ErrorException si el email ya está registrado o si ocurre un error al crear el usuario.
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Validated RegisterRequest req) {
        final String email = req.email().toLowerCase(Locale.ROOT);

        if (credentialRepository.findByEmail(email).isPresent()) {
            throw new ErrorException("El email ya está registrado", "L-REG-04", HttpStatus.CONFLICT);
        }

        UUID userId;
        try {
            var created = userDir.createLocalUser(
                    req.firstName(),
                    req.lastName(),
                    email.split("@")[0],
                    email,
                    req.password()
            );
            userId = created.id();

        } catch (ErrorException ex) {
            if (ex.status() == HttpStatus.CONFLICT) {
                var existing = userDir.findByEmail(email);
                if (existing == null) {
                    throw new ErrorException("Email ya existe en Users Service, pero no se puede recuperar",
                            "L-REG-05", HttpStatus.CONFLICT);
                }
                userId = existing.id();
            } else {
                throw ex;
            }
        }

        var cred = new CredentialEntity();
        cred.setUserId(userId);
        cred.setEmail(email);
        cred.setPasswordHash(passwordEncoder.encode(req.password()));
        cred.setStatus(CredentialEntity.CredentialStatus.ACTIVE);
        credentialRepository.save(cred);

        var res = new RegisterResponse(
                userId.toString(),
                req.firstName(),
                req.lastName(),
                email,
                cred.getStatus().name()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}