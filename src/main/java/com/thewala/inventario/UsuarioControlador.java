package com.thewala.inventario;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UsuarioControlador {

    private final UsuarioRepositorio repositorio;
    private final PasswordEncoder codificador;

    public UsuarioControlador(UsuarioRepositorio repositorio, PasswordEncoder codificador) {
        this.repositorio = repositorio;
        this.codificador = codificador;
    }

    @GetMapping("/usuarios")
    public String listar(Model model) {
        model.addAttribute("usuarios", repositorio.findAll());
        return "usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("formularioUsuario", new Usuario());
        return "usuario_formulario";
    }

    @PostMapping("/usuarios")
    public String guardar(@ModelAttribute("formularioUsuario") Usuario usuario) {
        usuario.setClave(codificador.encode(usuario.getClave()));
        usuario.setActivo(true);
        repositorio.save(usuario);
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{usuarioId}/estado")
    public String cambiarEstado(@PathVariable Long usuarioId) {
        Usuario usuario = repositorio.findById(usuarioId).orElseThrow();
        usuario.setActivo(!usuario.isActivo());
        repositorio.save(usuario);
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{usuarioId}/eliminar")
    public String eliminar(@PathVariable Long usuarioId) {
        repositorio.deleteById(usuarioId);
        return "redirect:/usuarios";
    }
}