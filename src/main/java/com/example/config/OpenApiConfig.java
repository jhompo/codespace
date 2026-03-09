package com.example.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI 3 / Swagger UI.
 * Acceso: http://localhost:8080/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title       = "Spring Boot Codespace API",
                version     = "1.0.0",
                description = "API reactiva con Spring WebFlux, Spring Security y Spring Data",
                contact     = @Contact(name = "Dev Team", email = "dev@example.com")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local")
        }
)
@SecurityScheme(
        name             = "bearerAuth",
        type             = SecuritySchemeType.HTTP,
        scheme           = "bearer",
        bearerFormat     = "JWT",
        description      = "Ingresa el token JWT obtenido del servidor de autenticación"
)
public class OpenApiConfig {}
