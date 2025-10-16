package com.legendme.login.svc.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuración central de cliente HTTP para la aplicación.
 *
 * Esta clase define y expone un bean compartido de tipo RestClient,
 * que se utiliza para realizar llamadas HTTP a servicios externos
 * (por ejemplo, otros microservicios dentro del ecosistema Legendme).
 *
 * El bean creado se puede inyectar en cualquier componente mediante @Autowired
 * o a través de inyección por constructor.
 *
 * Beneficios:
 * - Centraliza la configuración del cliente HTTP.
 * - Permite extender fácilmente con timeouts, interceptores o headers globales.
 * - Facilita pruebas unitarias al poder mockear el RestClient.
 *
 * Ejemplo de uso:
 * {@code
 * @Service
 * public class UserDirectoryClient {
 *     private final RestClient restClient;
 *
 *     public UserDirectoryClient(RestClient restClient) {
 *         this.restClient = restClient;
 *     }
 * }
 * }
 *
 */
@Configuration
public class RestClientConfig {

    /**
     * Define e inicializa un bean RestClient compartido.
     *
     * Este cliente puede ser utilizado para ejecutar peticiones HTTP
     * a otros servicios REST de manera sencilla y reutilizable.
     *
     * @return instancia configurada de RestClient.
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder().build();
    }
}

