package com.prueba.stefanini.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configuracion del {@link RestTemplate} para la integracion con la API externa Petstore.
 *
 * <p>Construye el bean con la URL base y los timeouts definidos
 * en {@link PetstoreProperties}.</p>
 */
@Configuration
@RequiredArgsConstructor
public class RestTemplateConfig {

    private final PetstoreProperties petstoreProperties;

    /**
     * Crea el bean {@link RestTemplate} configurado con la URL base
     * y los timeouts de conexion y lectura de la API externa.
     *
     * @param builder constructor provisto por Spring Boot
     * @return instancia de {@link RestTemplate} lista para inyectar
     */
    @Bean
    public RestTemplate petstoreRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri(petstoreProperties.getBaseUrl())
                .connectTimeout(Duration.ofSeconds(petstoreProperties.getConnectTimeoutSeconds()))
                .readTimeout(Duration.ofSeconds(petstoreProperties.getReadTimeoutSeconds()))
                .build();
    }
}
