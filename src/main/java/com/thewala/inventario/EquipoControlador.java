package com.thewala.inventario;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}