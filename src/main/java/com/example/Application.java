package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

/**
 * Punto de entrada de la aplicación Spring Boot.
 *
 * <p>Tecnologías principales:
 * <ul>
 *   <li>Spring WebFlux  – programación reactiva (no bloqueante)</li>
 *   <li>Spring Security – autenticación y autorización con JWT</li>
 *   <li>Spring Data R2DBC – acceso reactivo a PostgreSQL</li>
 *   <li>Spring Data MongoDB Reactive – acceso reactivo a MongoDB</li>
 *   <li>Spring Data Redis Reactive – caché reactiva</li>
 * </ul>
 */
@SpringBootApplication
@EnableR2dbcAuditing
@EnableReactiveMongoAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
