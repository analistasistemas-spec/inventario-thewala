package com.thewala.inventario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 * Al ejecutar main(), Spring Boot levanta un servidor web (Tomcat)
 * y deja la aplicacion escuchando en http://localhost:8090
 */
@SpringBootApplication
public class InventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }
}
