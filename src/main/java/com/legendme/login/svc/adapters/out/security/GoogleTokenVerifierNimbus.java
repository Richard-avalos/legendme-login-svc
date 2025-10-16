package com.legendme.login.svc.adapters.out.security;

import com.legendme.login.svc.application.port.out.GoogleTokenVerifierPort;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * Clase responsable de verificar y validar tokens ID emitidos por Google.
 *
 * Utiliza la librería Nimbus JOSE + JWT para comprobar la firma del token
 * contra las claves públicas JWK de Google y realizar validaciones adicionales
 * sobre su contenido.
 *
 * Flujo general:
 * 1. Descarga las claves públicas (JWK) desde la URL configurada de Google.
 * 2. Recibe un token ID (JWT) desde el cliente.
 * 3. Parsea y valida la firma del token utilizando el algoritmo RS256.
 * 4. Verifica los campos estándar del token:
 *    - Issuer (iss): debe ser accounts.google.com o https://accounts.google.com.
 *    - Audience (aud): debe contener el clientId de la aplicación.
 *    - Expiration: debe estar vigente (no expirado).
 * 5. Si todas las validaciones son correctas, retorna un objeto VerifiedGoogleUser
 *    con la información del usuario autenticado.
 * 6. Si ocurre un error o el token es inválido, lanza una ErrorException con
 *    el estado HTTP 401 (no autorizado).
 *
 * Parámetros del constructor:
 * - jwkUri: URL del endpoint de claves públicas de Google (JWK).
 * - clientId: identificador de cliente configurado en Google Cloud.
 *
 */
@Slf4j
@Component
public class GoogleTokenVerifierNimbus implements GoogleTokenVerifierPort {

    private final JWKSource<SecurityContext> jwkSource; // Fuente de claves JWK para la verificación de firmas.
    private final String clientId; // ID del cliente configurado para la aplicación.


    public GoogleTokenVerifierNimbus(
            @Value("${google.jwks-uri}") URL jwkUri,
            @Value("${google.client-id}") String clientId) throws Exception {
        this.jwkSource = JWKSourceBuilder
                .create(jwkUri)
                .retrying(true)
                .build();
        this.clientId = clientId;
    }

    /**
     * Verifica la validez y autenticidad de un token ID de Google.
     *
     * Pasos del proceso:
     * 1. Parsea el token recibido (JWT).
     * 2. Configura un procesador JWT que usa las claves JWK públicas de Google.
     * 3. Valida la firma del token.
     * 4. Comprueba el issuer, audience y la expiración.
     * 5. Si el token es válido, construye un objeto VerifiedGoogleUser con los datos del usuario.
     *
     * @param idTokenStr cadena del token JWT emitido por Google.
     * @return un objeto VerifiedGoogleUser con la información del usuario autenticado.
     * @throws ErrorException si el token no es válido, está expirado o no pertenece al clientId configurado.
     */

    @Override
    public VerifiedGoogleUser verify(String idTokenStr) {
        try {
            log.info("Iniciando verification de token de Google");
            var jwt = SignedJWT.parse(idTokenStr);

            var proc = new DefaultJWTProcessor<SecurityContext>();
            proc.setJWSKeySelector(new JWSVerificationKeySelector<>(
                    JWSAlgorithm.RS256, jwkSource));

            var claims = proc.process(jwt, null);

            var iss = claims.getIssuer();

            if (!"accounts.google.com".equals(iss) && !"https://accounts.google.com".equals(iss)) {
                throw new ErrorException("Issuer invalido", "G-LOG-01", HttpStatus.BAD_REQUEST);
            }

            if (!claims.getAudience().contains(clientId)) {
                throw new ErrorException("Audience invalido", "G-LOG-02", HttpStatus.BAD_REQUEST);
            }

            if (claims.getExpirationTime() == null || new java.util.Date().after(claims.getExpirationTime())) {
                throw new ErrorException("Expiration invalido", "G-LOG-03", HttpStatus.BAD_REQUEST);
            }

            log.info("Google token verificado exitosamente");
            return new VerifiedGoogleUser(
                    claims.getSubject(),
                    claims.getStringClaim("email"),
                    Boolean.TRUE.equals(claims.getBooleanClaim("email_verified")),
                    claims.getStringClaim("name"),
                    claims.getStringClaim("picture")
            );

        } catch (Exception e) {
            throw new ErrorException("Token invalido", "G-LOG-04", HttpStatus.UNAUTHORIZED);
        }
    }

}


