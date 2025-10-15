package com.legendme.login.svc.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuración para el cliente WebClient utilizado para interactuar con el servicio de usuarios.
 */
@Configuration
public class UserClientConfig {
    /**
     * Define un bean de WebClient configurado con la URL base del servicio de usuarios.
     *
     * @param baseUrl La URL base del servicio de usuarios, obtenida de las propiedades de configuración.
     * @return Una instancia de {@link WebClient} configurada con la URL base proporcionada.
     */
    @Bean
    WebClient userWebClient(@Value("${user.service.base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
