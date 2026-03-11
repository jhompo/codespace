package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.Duration;
import java.util.List;

/**
 * Punto de entrada de la aplicación Spring Boot.
 *
 * <p>
 * Tecnologías principales:
 * <ul>
 * <li>Spring WebFlux – programación reactiva (no bloqueante)</li>
 * <li>Spring Security – autenticación y autorización con JWT</li>
 * <li>Spring Data R2DBC – acceso reactivo a PostgreSQL</li>
 * <li>Spring Data MongoDB Reactive – acceso reactivo a MongoDB</li>
 * <li>Spring Data Redis Reactive – caché reactiva</li>
 * </ul>
 */
@SpringBootApplication
@EnableR2dbcAuditing
@EnableReactiveMongoAuditing
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        /**
         * EJERCICIO: PROCESAMIENTO REACTIVO DE TRANSACCIONES BANCARIAS
         * 
         * Implementa un flujo reactivo que procese una lista de transacciones bancarias simuladas.
         * 
         * Cada transacción estará representada por un objeto:
         * public class TransactionDTO {
         *     private String id;
         *     private double amount;
         *     private String type; // Ejemplo: "DEPOSIT", "WITHDRAWAL"
         * }
         * 
         * El flujo debe cumplir los siguientes pasos:
         * 1. A partir de una lista de TransactionDTO, filtrar únicamente las transacciones de tipo "WITHDRAWAL".
         * 2. Convertir cada monto a su valor absoluto.
         * 3. Simular una transacción asíncrona, aplicando un retraso de 200 ms por transacción.
         * 4. Calcular el total acumulado de todos los montos procesados.
         * 5. Imprimir en consola:
         *    - Cada transacción procesada.
         *    - El total acumulado final.
         * 
         * Se valorará:
         * • Correcta aplicación del modelo reactivo (Flux/Mono).
         * • Código limpio y legible.
         * • Uso apropiado de operadores de Reactor (filter, map, flatMap, reduce, doOnNext, etc.).
         */
        System.out.println("Inicio del MiniReto Tecnico.");

       
    }

}
