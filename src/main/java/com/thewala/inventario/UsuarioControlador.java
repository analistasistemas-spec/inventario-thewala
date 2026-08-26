package com.thewala.inventario;

import java.security.Principal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @GetMapping("/usuarios/{usuarioId}/editar")
    public String editar(@PathVariable Long usuarioId, Model model) {
        Usuario existente = repositorio.findById(usuarioId).orElseThrow();

        // Copia para el formulario: la clave se deja vacia a proposito
        Usuario formulario = new Usuario();
        formulario.setId(existente.getId());
        formulario.setUsuario(existente.getUsuario());
        formulario.setRol(existente.getRol());
        formulario.setActivo(existente.isActivo());

        model.addAttribute("formularioUsuario", formulario);
        return "usuario_formulario";
    }

    @PostMapping("/usuarios")
    public String guardar(@ModelAttribute("formularioUsuario") Usuario usuario,
                          RedirectAttributes mensajes) {
        if (usuario.getId() == null) {
            // Usuario nuevo: la clave es obligatoria
            if (usuario.getClave() == null || usuario.getClave().isBlank()) {
                mensajes.addFlashAttribute("error", "La clave es obligatoria para un usuario nuevo");
                return "redirect:/usuarios/nuevo";
            }
            usuario.setClave(codificador.encode(usuario.getClave()));
            usuario.setActivo(true);
        } else {
            // Usuario existente
            Usuario existente = repositorio.findById(usuario.getId()).orElseThrow();
            usuario.setActivo(existente.isActivo());

            if (usuario.getClave() == null || usuario.getClave().isBlank()) {
                usuario.setClave(existente.getClave());          // no la cambiaron
            } else {
                usuario.setClave(codificador.encode(usuario.getClave()));
            }
        }

        repositorio.save(usuario);
        mensajes.addFlashAttribute("mensaje", "Usuario guardado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{usuarioId}/estado")
    public String cambiarEstado(@PathVariable Long usuarioId, Principal conectado,
                                RedirectAttributes mensajes) {
        Usuario usuario = repositorio.findById(usuarioId).orElseThrow();

        if (usuario.getUsuario().equals(conectado.getName())) {
            mensajes.addFlashAttribute("error", "No puedes desactivar tu propio usuario");
            return "redirect:/usuarios";
        }
        if (usuario.isActivo() && esUltimoAdministrador(usuario)) {
            mensajes.addFlashAttribute("error", "No puedes desactivar al ultimo administrador");
            return "redirect:/usuarios";
        }

        usuario.setActivo(!usuario.isActivo());
        repositorio.save(usuario);
        mensajes.addFlashAttribute("mensaje", "Estado actualizado");
        return "redirect:/usuarios";
    }

    @PostMapping("/usuarios/{usuarioId}/eliminar")
    public String eliminar(@PathVariable Long usuarioId, Principal conectado,
                           RedirectAttributes mensajes) {
        Usuario usuario = repositorio.findById(usuarioId).orElseThrow();

        if (usuario.getUsuario().equals(conectado.getName())) {
            mensajes.addFlashAttribute("error", "No puedes eliminar tu propio usuario");
            return "redirect:/usuarios";
        }
        if (esUltimoAdministrador(usuario)) {
            mensajes.addFlashAttribute("error", "No puedes eliminar al ultimo administrador");
            return "redirect:/usuarios";
        }

        repositorio.deleteById(usuarioId);
        mensajes.addFlashAttribute("mensaje", "Usuario eliminado");
        return "redirect:/usuarios";
    }

    /** Devuelve true si este usuario es ADMIN y no queda ningun otro ADMIN activo. */
    private boolean esUltimoAdministrador(Usuario usuario) {
        if (!"ADMIN".equals(usuario.getRol())) {
            return false;
        }
        long administradoresActivos = repositorio.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRol()) && u.isActivo())
                .count();
        return administradoresActivos <= 1;
    }
}