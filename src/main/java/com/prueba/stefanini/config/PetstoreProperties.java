package com.prueba.stefanini.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Propiedades de configuracion para la integracion con la API externa Petstore.
 *
 * <p>Vincula el prefijo {@code petstore} del archivo {@code application.yaml}
 * a esta clase, centralizando toda la configuracion del cliente externo
 * y permitiendo validarla en tiempo de arranque.</p>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "petstore")
public class PetstoreProperties {

    /**
     * URL base de la API externa Petstore.
     * Ejemplo: {@code https://petstore.swagger.io/v2}
     */
    private String baseUrl;

    /**
     * Timeout de conexion en segundos hacia la API externa.
     */
    private int connectTimeoutSeconds = 5;

    /**
     * Timeout de lectura en segundos hacia la API externa.
     */
    private int readTimeoutSeconds = 10;
}
