package com.thewala.inventario;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioControlador {

    private final EquipoRepositorio equipoRepositorio;
    private final PerifericoRepositorio perifericoRepositorio;

    public InicioControlador(EquipoRepositorio equipoRepositorio,
                             PerifericoRepositorio perifericoRepositorio) {
        this.equipoRepositorio = equipoRepositorio;
        this.perifericoRepositorio = perifericoRepositorio;
    }

    @GetMapping("/")
    public String inicio(Model model) {
        model.addAttribute("totalEquipos", equipoRepositorio.count());
        model.addAttribute("totalPerifericos", perifericoRepositorio.count());
        model.addAttribute("porSede", equipoRepositorio.contarPorSede());
        model.addAttribute("porEstado", equipoRepositorio.contarPorEstado());
        model.addAttribute("porTipo", equipoRepositorio.contarPorTipo());
        return "inicio";
    }
}