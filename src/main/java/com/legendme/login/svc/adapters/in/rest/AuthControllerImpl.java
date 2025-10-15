package com.legendme.login.svc.adapters.in.rest;

import com.legendme.login.svc.domain.usecase.AuthenticateWithGoogle;
import com.legendme.login.svc.application.port.in.AuthController;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para gestionar las operaciones de autenticación.
 * Implementa el contrato definido en {@link AuthController}.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {
   private final AuthenticateWithGoogle useCase;

   /**
    * Endpoint para autenticar a un usuario utilizando Google Sign-In.
    *
    * @param req La solicitud de inicio de sesión con Google, que contiene el token de identificación.
    * @return Una respuesta HTTP con los tokens de autenticación y la información del usuario.
    */
   @PostMapping("/google")
    public ResponseEntity<AuthResponse> google(@Valid @RequestBody GoogleSignInRequest req) {
       var r = useCase.authenticate(req.idToken());
       return ResponseEntity.ok(new AuthResponse(r.tokens().accessToken(), r.tokens().refreshToken(), r.userId(), r.email(), r.name()));
   }

   @GetMapping("/api/secure/ping")
   public String pingSecure() { return "pong-secure"; }

}
