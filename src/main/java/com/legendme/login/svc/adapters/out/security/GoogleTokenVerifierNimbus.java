package com.legendme.login.svc.adapters.out.security;

import com.legendme.login.svc.application.port.out.GoogleTokenVerifierPort;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * Implementación del puerto {@link GoogleTokenVerifierPort} utilizando la biblioteca Nimbus JOSE+JWT.
 * Este componente verifica y valida los tokens de Google.
 */
@Component
public class GoogleTokenVerifierNimbus implements GoogleTokenVerifierPort {

    private final JWKSource<SecurityContext> jwkSource; // Fuente de claves JWK para la verificación de firmas.
    private final String clientId; // ID del cliente configurado para la aplicación.

    /**
     * Constructor que inicializa la fuente de claves JWK y el ID del cliente.
     *
     * @param jwkUri   La URI del conjunto de claves JWK proporcionada por Google.
     * @param clientId El ID del cliente configurado para la aplicación.
     * @throws Exception Si ocurre un error al inicializar la fuente de claves JWK.
     */
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
     * Verifica y valida un token de identificación de Google.
     *
     * @param idTokenStr El token de identificación proporcionado por Google.
     * @return Un objeto {@link VerifiedGoogleUser} que contiene la información del usuario verificado.
     * @throws BadCredentialsException Si el token es inválido o no cumple con los criterios de validación.
     */
    @Override
    public VerifiedGoogleUser verify(String idTokenStr) {
        try {
            var jwt = SignedJWT.parse(idTokenStr);

            var proc = new DefaultJWTProcessor<SecurityContext>();
            proc.setJWSKeySelector(new JWSVerificationKeySelector<>(
                    JWSAlgorithm.RS256, jwkSource));

            var claims = proc.process(jwt, null);

            var iss = claims.getIssuer();

            if (!"accounts.google.com".equals(iss) && !"https://accounts.google.com".equals(iss)) {
                throw new org.springframework.security.authentication.BadCredentialsException("issuer invalid");
            }

            if (!claims.getAudience().contains(clientId)) {
                throw new org.springframework.security.authentication.BadCredentialsException("audience invalid");
            }

            if (claims.getExpirationTime() == null || new java.util.Date().after(claims.getExpirationTime())) {
                throw new org.springframework.security.authentication.BadCredentialsException("token expired");
            }

            return new VerifiedGoogleUser(
                    claims.getSubject(),
                    claims.getStringClaim("email"),
                    Boolean.TRUE.equals(claims.getBooleanClaim("email_verified")),
                    claims.getStringClaim("name"),
                    claims.getStringClaim("picture")
            );

        } catch (Exception e) {
            throw new BadCredentialsException("invalid token");
        }
    }

}


