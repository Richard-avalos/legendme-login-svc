package com.legendme.login.svc.application.service;

import com.legendme.login.svc.domain.model.AuthTokens;
import com.legendme.login.svc.domain.usecase.AuthenticateWithGoogle;
import com.legendme.login.svc.application.port.out.GoogleTokenVerifierPort;
import com.legendme.login.svc.application.port.out.JwtIssuerPort;
import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.shared.dto.GoogleUserPayload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio que implementa la autenticación con Google.
 * Este servicio verifica el token de Google, actualiza o inserta la información del usuario
 * y genera los tokens de autenticación correspondientes.
 */
@Service
@RequiredArgsConstructor
public class AuthenticateWithGoogleService implements AuthenticateWithGoogle {

    private final GoogleTokenVerifierPort googleVerifier; // Puerto para verificar tokens de Google.
    private final UserDirectoryPort userDir; // Puerto para gestionar el directorio de usuarios.
    private final JwtIssuerPort jwt; // Puerto para emitir tokens JWT.

    /**
     * Autentica a un usuario utilizando un token de Google.
     *
     * @param idToken El token de identificación proporcionado por Google.
     * @return Un objeto {@link Result} que contiene la información del usuario autenticado
     *         y los tokens de autenticación generados.
     */
    @Override
    public Result authenticate(String idToken) {
        var g = googleVerifier.verify(idToken); // Verifica el token de Google.

        // Importante: Aqui es donde se va a definir la logica del usuario
        var user = userDir.upsertGoogleUser(new GoogleUserPayload(
                g.sub(), g.email(), g.name(), g.picture(), g.emailVerified()
        ), "GOOGLE"); // Inserta o actualiza la información del usuario.

        AuthTokens tokens = jwt.pair(user.userId(), user.email(), user.name()); // Genera los tokens de autenticación.
        return new Result(user.userId(), user.email(), user.name(), tokens); // Retorna el resultado de la autenticación.
    }
}
