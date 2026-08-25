package com.thewala.inventario;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetallesService implements UserDetailsService {

    private final UsuarioRepositorio repositorio;

    public UsuarioDetallesService(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public UserDetails loadUserByUsername(String nombre) throws UsernameNotFoundException {
        Usuario usuario = repositorio.findByUsuario(nombre)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + nombre));

        return User.withUsername(usuario.getUsuario())
                .password(usuario.getClave())
                .roles(usuario.getRol())
                .disabled(!usuario.isActivo())
                .build();
    }
}