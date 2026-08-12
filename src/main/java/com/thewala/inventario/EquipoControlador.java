package com.thewala.inventario;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EquipoControlador {

    private final EquipoRepositorio repositorio;

    public EquipoControlador(EquipoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping("/equipos")
    public String listar(Model model) {
        model.addAttribute("equipos", repositorio.findAll());
        return "equipos";
    }
    @GetMapping("/equipos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("equipo", new Equipo());
        return "equipo_formulario";
    }

    @PostMapping("/equipos")
    public String guardar(@ModelAttribute Equipo equipo) {
        repositorio.save(equipo);
        return "redirect:/equipos";
    }
}