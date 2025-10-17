package com.legendme.login.svc.adapters.out.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuración de beans de seguridad para autenticación básica.
 *
 * Proporciona los componentes necesarios para la autenticación de usuarios locales:
 * - {PasswordEncoder}: codificador de contraseñas usando BCrypt (factor 12).
 * - {DaoAuthenticationProvider}: proveedor de autenticación que valida credenciales usando {CredentialUserDetailsService}.
 * - {AuthenticationManager}: gestor de autenticación de Spring Security.
 *
 * Uso principal:
 * Inyectar estos beans en los servicios y controladores de autenticación para gestionar login y verificación de contraseñas.
 */
@Configuration
@RequiredArgsConstructor
public class BasicAuthBeansConfig {
    private final CredentialUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}