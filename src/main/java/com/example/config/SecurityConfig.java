package com.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

/**
 * Configuración de Spring Security para aplicación reactiva (WebFlux).
 *
 * <ul>
 * <li>Stateless: sin sesiones HTTP (JWT)</li>
 * <li>JWT decodificado vía Nimbus (dependencia oauth2-jose)</li>
 * <li>CSRF deshabilitado (API REST stateless)</li>
 * </ul>
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        @Value("${app.security.enabled:true}")
        private boolean securityEnabled;

        // Rutas públicas – sin autenticación
        private static final String[] PUBLIC_PATHS = {
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/health",
                        "/actuator/info"
        };

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
                // En entornos de desarrollo (por ejemplo Codespaces) se puede desactivar
                // fácilmente
                // la seguridad para facilitar el trabajo local. En producción, debe mantenerse
                // activa.
                if (!securityEnabled) {
                        return http
                                        .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                        .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                                        .build();
                }

                return http
                                // Sin estado – usamos JWT
                                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                                // CSRF deshabilitado para API REST
                                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                                // Cabeceras de seguridad
                                .headers(headers -> headers
                                                .contentTypeOptions(
                                                                ServerHttpSecurity.HeaderSpec.ContentTypeOptionsSpec::disable)
                                                .frameOptions(ServerHttpSecurity.HeaderSpec.FrameOptionsSpec::disable))
                                // Autorización de rutas
                                .authorizeExchange(exchanges -> exchanges
                                                .pathMatchers(PUBLIC_PATHS).permitAll()
                                                .pathMatchers(HttpMethod.OPTIONS).permitAll()
                                                .pathMatchers("/admin/**").hasRole("ADMIN")
                                                .anyExchange().authenticated())
                                // Validar JWT
                                .oauth2ResourceServer(oauth2 -> oauth2
                                                .jwt(jwt -> {
                                                }) // usa spring.security.oauth2.resourceserver.jwt del yml
                                )
                                .build();
        }
}
