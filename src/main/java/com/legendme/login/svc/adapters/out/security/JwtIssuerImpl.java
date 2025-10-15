package com.legendme.login.svc.adapters.out.security;

import com.legendme.login.svc.application.port.out.JwtIssuerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Implementación del puerto {@link JwtIssuerPort} para la emisión de tokens JWT.
 * Este componente genera tokens de acceso y de actualización utilizando una clave secreta.
 */
@Component
public class JwtIssuerImpl implements JwtIssuerPort {
    private final String issuer; // Emisor del token JWT.
    private final java.security.Key key; // Clave utilizada para firmar los tokens.
    private final long accExp; // Tiempo de expiración del token de acceso en minutos.
    private final long refExp; // Tiempo de expiración del token de actualización en días.

    /**
     * Constructor que inicializa los valores necesarios para la emisión de tokens JWT.
     *
     * @param issuer El emisor del token, configurado en las propiedades de la aplicación.
     * @param secret La clave secreta utilizada para firmar los tokens.
     * @param accExp Tiempo de expiración del token de acceso en minutos.
     * @param refExp Tiempo de expiración del token de actualización en días.
     * @throws IllegalStateException Si la clave secreta no está definida o es demasiado corta.
     */
    public JwtIssuerImpl(
        @Value("${app.jwt.issuer}") String issuer,
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.access-exp-minutes}") long accExp,
        @Value("${app.jwt.refresh-exp-days}") long refExp) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("app.jwt.secret no está definido");
        }
        // Debe tener >= 32 bytes para HS256 (jjwt verifica esto)
        byte[] bytes = secret.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("app.jwt.secret es demasiado corta; usa >= 32 bytes");
        }

        this.issuer = issuer;
        this.key = io.jsonwebtoken.security.Keys.hmacShaKeyFor(bytes);;
        this.accExp = accExp;
        this.refExp = refExp;
    }

    /**
     * Genera un token de acceso JWT para un usuario.
     *
     * @param userId El identificador único del usuario.
     * @param email  El correo electrónico del usuario.
     * @param name   El nombre del usuario.
     * @return El token de acceso generado como una cadena compacta.
     */
    @Override
    public String issueAccessToken(UUID userId, String email, String name) {
        var now = java.time.Instant.now();
        return io.jsonwebtoken.Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(issuer)
                .claim("email", email)
                .claim("name", name)
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(now.plus(accExp, java.time.temporal.ChronoUnit.MINUTES)))
                .signWith(key)
                .compact();
    }

    /**
     * Genera un token de actualización JWT para un usuario.
     *
     * @param userId El identificador único del usuario.
     * @return El token de actualización generado como una cadena compacta.
     */
    @Override
    public String issueRefreshToken(UUID userId) {
        var now = java.time.Instant.now();
        return io.jsonwebtoken.Jwts.builder()
                .subject(String.valueOf(userId))
                .issuer(issuer)
                .claim("type", "refresh")
                .issuedAt(java.util.Date.from(now))
                .expiration(java.util.Date.from(now.plus(refExp, java.time.temporal.ChronoUnit.DAYS)))
                .signWith(key)
                .compact();
    }



}
