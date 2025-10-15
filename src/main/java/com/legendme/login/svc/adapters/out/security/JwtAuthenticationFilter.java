package com.legendme.login.svc.adapters.out.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.security.Key;
import java.util.List;

/**
 * Filtro de autenticación JWT que se ejecuta una vez por solicitud.
 * Este filtro valida el token JWT presente en el encabezado de autorización
 * y establece la autenticación en el contexto de seguridad de Spring.
 */
@Component
public class JwtAuthenticationFilter extends org.springframework.web.filter.OncePerRequestFilter {
    private final Key key;

    /**
     * Constructor que inicializa la clave secreta utilizada para firmar y verificar los tokens JWT.
     *
     * @param secret La clave secreta configurada en las propiedades de la aplicación.
     */
    public JwtAuthenticationFilter(
            @Value("${app.jwt.secret}") String secret ) {
                this.key = Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
    }

    /**
     * Método que filtra las solicitudes HTTP para validar el token JWT.
     *
     * @param request     La solicitud HTTP entrante.
     * @param response    La respuesta HTTP saliente.
     * @param filterChain La cadena de filtros que permite continuar con el procesamiento.
     * @throws jakarta.servlet.ServletException Si ocurre un error relacionado con el servlet.
     * @throws java.io.IOException              Si ocurre un error de entrada/salida.
     */
    @Override
    protected void doFilterInternal(
            jakarta.servlet.http.HttpServletRequest request,
            jakarta.servlet.http.HttpServletResponse response,
            jakarta.servlet.FilterChain filterChain
    ) throws jakarta.servlet.ServletException, java.io.IOException {
        if (org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        var auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Claims claims = Jwts.parser()
                                .verifyWith((SecretKey) key)
                                .build()
                                .parseSignedClaims(token)
                                .getPayload();

                String userId = claims.getSubject();
                String email = claims.get("email", String.class);
                String name = claims.get("name", String.class);

                var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
                var principal = new AuthPrincipal(userId, email, name);
                var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception ignored) {}
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Clase interna que representa los detalles del usuario autenticado.
     *
     * @param userId El identificador único del usuario.
     * @param email  El correo electrónico del usuario.
     * @param name   El nombre del usuario.
     */
    public record AuthPrincipal(String userId, String email, String name) {}



}
