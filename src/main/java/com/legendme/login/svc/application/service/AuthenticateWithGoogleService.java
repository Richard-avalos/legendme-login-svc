package com.legendme.login.svc.application.service;

import com.legendme.login.svc.adapters.out.http.dto.UserResponse;
import com.legendme.login.svc.domain.model.AuthTokens;
import com.legendme.login.svc.domain.usecase.AuthenticateWithGoogle;
import com.legendme.login.svc.application.port.out.GoogleTokenVerifierPort;
import com.legendme.login.svc.application.port.out.GoogleTokenVerifierPort.VerifiedGoogleUser;
import com.legendme.login.svc.application.port.out.JwtIssuerPort;
import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.shared.dto.GoogleUserPayload;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * Servicio que implementa la autenticación con Google.
 * Este servicio verifica el token de Google, actualiza o inserta la información del usuario
 * y genera los tokens de autenticación correspondientes.
 */
@Slf4j
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
     * y los tokens de autenticación generados.
     */
    @Override
    public Result authenticate(String idToken) {
        log.info("Iniciando autenticación con Google, capa de servicio");
        if (idToken == null || idToken.isEmpty()) {
            throw new ErrorException("Token de Google no proporcionado", "G-LOG-05", HttpStatus.BAD_REQUEST);
        }

        final VerifiedGoogleUser g;
        final UserResponse user;
        final AuthTokens tokens;

        g = googleVerifier.verify(idToken);

        if (Boolean.FALSE.equals(g.emailVerified())) {
            throw new ErrorException(String.format("El email %s, no esta verificado por google", g.email())
                    , "G-LOG-06", HttpStatus.UNAUTHORIZED);
        }

        user = userDir.upsertGoogleUser(new GoogleUserPayload(
                g.sub(), g.email(), g.name(), g.picture(), g.emailVerified()
        ));

        tokens = jwt.pair(user.id(), user.email(), user.name());


        return new Result(user.id(), user.email(), user.name(), tokens);
    }
}
