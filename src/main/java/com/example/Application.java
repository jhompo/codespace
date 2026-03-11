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
        System.out.println("Inicio del Ejercicio.");

        List<TransactionDTO> transactions = List.of(
                new TransactionDTO("1", -100, "WITHDRAWAL"),
                new TransactionDTO("2", 200, "DEPOSIT"),
                new TransactionDTO("3", 50, "WITHDRAWAL"),
                new TransactionDTO("4", -30, "WITHDRAWAL"));

        Flux.fromIterable(transactions)
                .filter(tx -> "WITHDRAWAL".equalsIgnoreCase(tx.getType()))

                .flatMap(tx -> {
                    if (tx.getAmount() <= 0) {
                        System.out.println("⚠️ Transacción " + tx.getId() + " con monto negativo: " + tx.getAmount() );
                    }
                    tx.setAmount(Math.abs(tx.getAmount()));
                    return Mono.just(tx).delayElement(Duration.ofMillis(200));
                })

                .doOnNext(tx -> System.out.println("Procesada: " + tx.getId() + " - Monto: " + tx.getAmount()))
                .map(TransactionDTO::getAmount)
                .reduce(0.0, Double::sum)
                .doOnNext(total -> System.out.println("Total acumulado: " + total))
                .subscribe();
    }

}
