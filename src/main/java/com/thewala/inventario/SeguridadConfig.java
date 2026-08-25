package com.thewala.inventario;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SeguridadConfig {

    @Bean
    public PasswordEncoder codificador() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain reglas(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(permisos -> permisos.requestMatchers("/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/logout").authenticated()
                        .requestMatchers("/equipos/nuevo", "/equipos/*/editar").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login.defaultSuccessUrl("/", true))
                .logout(salida -> salida.logoutSuccessUrl("/"));
        return http.build();
    }
}