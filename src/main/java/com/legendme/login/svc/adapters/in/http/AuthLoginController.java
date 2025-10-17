package com.legendme.login.svc.adapters.in.http;

import com.legendme.login.svc.adapters.in.http.dto.LoginRequest;
import com.legendme.login.svc.adapters.in.http.dto.LoginResponse;
import com.legendme.login.svc.adapters.out.http.UserDirectoryClient;
import com.legendme.login.svc.adapters.out.jpa.CredentialRepository;
import com.legendme.login.svc.application.port.out.JwtIssuerPort;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador HTTP responsable de gestionar el inicio de sesión de usuarios locales.
 *
 * Forma parte del adaptador de entrada del microservicio de autenticación (login-svc),
 * permitiendo a los clientes autenticarse mediante credenciales (email y contraseña)
 * y obtener un token JWT válido para futuras solicitudes.
 *
 * Funcionalidad principal:
 * 1. Recibe una solicitud POST en el endpoint `/auth/login` con las credenciales del usuario.
 * 2. Valida las credenciales utilizando el `AuthenticationManager` de Spring Security.
 * 3. Recupera los datos del usuario desde el servicio externo de usuarios (`UserDirectoryClient`).
 * 4. Genera un token JWT a través del puerto `JwtIssuerPort`.
 * 5. Retorna un objeto `LoginResponse` con el token y los datos esenciales del usuario.
 *
 * Componentes principales:
 * - authenticationManager: encargado de autenticar las credenciales del usuario.
 * - credentialRepository: accede a la base de datos local para obtener las credenciales registradas.
 * - jwtIssuer: puerto que genera el token JWT firmado.
 * - userDir: cliente HTTP que consulta el microservicio de usuarios para obtener información adicional.
 *
 * Variables configurables:
 * - accessExpMinutes: duración (en minutos) del token de acceso, configurable en `app.jwt.access-exp-minutes`.
 *
 * Excepciones:
 * - Lanza `ErrorException` con código `L-LOG-01` o `L-LOG-02` si las credenciales son inválidas
 *   o el usuario no existe.
 *
 * Endpoint:
 * - POST /auth/login → procesa el inicio de sesión y devuelve un `LoginResponse`.
 *
 * Ejemplo de flujo:
 * 1. El cliente envía email y password al endpoint `/auth/login`.
 * 2. El servicio valida las credenciales.
 * 3. Si son válidas, genera y devuelve un JWT con información del usuario autenticado.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthLoginController {
    private final AuthenticationManager authenticationManager;
    private final CredentialRepository credentialRepository;
    private final JwtIssuerPort jwtIssuer;
    private final UserDirectoryClient userDir;

    @Value("${app.jwt.access-exp-minutes:15}")
    private long accessExpMinutes;

    /**
     * Endpoint encargado de autenticar al usuario local mediante email y contraseña.
     *
     * Pasos:
     * 1. Convierte el email a minúsculas para evitar conflictos de mayúsculas.
     * 2. Valida las credenciales con el `AuthenticationManager`.
     * 3. Recupera la información del usuario desde el repositorio local y del servicio externo.
     * 4. Genera un JWT firmado con la información básica del usuario.
     * 5. Retorna un `LoginResponse` con el token y datos del usuario.
     *
     * @param req objeto {@link LoginRequest} con las credenciales de inicio de sesión.
     * @return ResponseEntity con {@link LoginResponse} si la autenticación es exitosa.
     * @throws ErrorException si las credenciales son inválidas o ocurre un error durante la autenticación.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Validated LoginRequest req) {
        String email = req.email().toLowerCase();

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, req.password())
            );
        } catch (BadCredentialsException | DisabledException | LockedException ex) {
            throw new ErrorException("Credenciales inválidas", "L-LOG-01", HttpStatus.UNAUTHORIZED);
        }

        var cred = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new ErrorException("Credenciales inválidas", "L-LOG-02", HttpStatus.UNAUTHORIZED));

        var user = userDir.findByEmail(email);
        String name = user != null && user.name() != null && !user.name().isBlank()
                ? user.name()
                : email.split("@")[0];

        String token = jwtIssuer.issueAccessToken(cred.getUserId(), cred.getEmail(), name);

        var res = new LoginResponse(
                token,
                "Bearer",
                accessExpMinutes * 60,
                cred.getUserId().toString(),
                cred.getEmail()
        );
        return ResponseEntity.ok(res);
    }
}