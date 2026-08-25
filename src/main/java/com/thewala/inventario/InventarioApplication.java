package com.thewala.inventario;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Punto de entrada de la aplicacion.
 * Al ejecutar main(), Spring Boot levanta un servidor web (Tomcat)
 * y deja la aplicacion escuchando en http://localhost:9090
 */
@SpringBootApplication
public class InventarioApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventarioApplication.class, args);
    }

    /**
     * Se ejecuta una sola vez al arrancar: si la tabla de usuarios esta vacia,
     * crea los dos usuarios iniciales con la clave cifrada.
     */
    @Bean
    public CommandLineRunner crearUsuariosIniciales(UsuarioRepositorio repositorio,
                                                    PasswordEncoder codificador) {
        return args -> {
            if (repositorio.count() == 0) {
                Usuario admin = new Usuario();
                admin.setUsuario("admin");
                admin.setClave(codificador.encode("12345"));
                admin.setRol("ADMIN");
                admin.setActivo(true);
                repositorio.save(admin);

                Usuario consulta = new Usuario();
                consulta.setUsuario("consulta");
                consulta.setClave(codificador.encode("consulta"));
                consulta.setRol("CONSULTA");
                consulta.setActivo(true);
                repositorio.save(consulta);

                System.out.println(">>> Usuarios iniciales creados: admin y consulta");
            }
        };
    }
}