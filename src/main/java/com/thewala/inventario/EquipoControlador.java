package com.thewala.inventario;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class EquipoControlador {

    private final EquipoRepositorio repositorio;
    private final PerifericoRepositorio perifericoRepositorio;

    public EquipoControlador(EquipoRepositorio repositorio, PerifericoRepositorio perifericoRepositorio) {
        this.repositorio = repositorio;
        this.perifericoRepositorio = perifericoRepositorio;
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

    @GetMapping("/equipos/{id}/editar")
    public String mostrarEdicion(@PathVariable Long id, Model model) {
        Equipo equipo = repositorio.findById(id).orElseThrow();
        model.addAttribute("equipo", equipo);
        return "equipo_formulario";
    }

    @PostMapping("/equipos/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        repositorio.deleteById(id);
        return "redirect:/equipos";
    }
    @GetMapping("/equipos/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Equipo equipo = repositorio.findById(id).orElseThrow();
        model.addAttribute("equipo", equipo);
        model.addAttribute("perifericos", perifericoRepositorio.findByEquipoId(id));
        model.addAttribute("nuevoPeriferico", new Periferico());
        return "equipo_detalle";
    }

    @PostMapping("/equipos/{equipoId}/perifericos")
    public String agregarPeriferico(@PathVariable Long equipoId, @ModelAttribute Periferico periferico) {
        Equipo equipo = repositorio.findById(equipoId).orElseThrow();
        periferico.setEquipo(equipo);
        perifericoRepositorio.save(periferico);
        return "redirect:/equipos/" + equipoId;
    }

    @PostMapping("/perifericos/{id}/eliminar")
    public String eliminarPeriferico(@PathVariable Long id) {
        Periferico periferico = perifericoRepositorio.findById(id).orElseThrow();
        Long equipoId = periferico.getEquipo().getId();
        perifericoRepositorio.deleteById(id);
        return "redirect:/equipos/" + equipoId;
    }
}