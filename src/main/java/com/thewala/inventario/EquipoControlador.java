package com.thewala.inventario;

import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.List;

@Controller
public class EquipoControlador {

    private final EquipoRepositorio repositorio;
    private final PerifericoRepositorio perifericoRepositorio;

    public EquipoControlador(EquipoRepositorio repositorio, PerifericoRepositorio perifericoRepositorio) {
        this.repositorio = repositorio;
        this.perifericoRepositorio = perifericoRepositorio;
    }

    @GetMapping("/equipos")
    public String listar(@RequestParam(required = false) String texto, Model model) {
        List<Equipo> equipos;
        if (texto == null || texto.isBlank()) {
            equipos = repositorio.findAll(Sort.by("placa"));
        } else {
            equipos = repositorio.buscar(texto);
        }
        model.addAttribute("equipos", equipos);
        model.addAttribute("texto", texto);
        return "equipos";
    }
    @GetMapping("/equipos/nuevo")
    public String mostrarFormulario(Model model) {
        model.addAttribute("equipo", new Equipo());
        return "equipo_formulario";
    }

    @PostMapping("/equipos")
    public String guardar(@Valid @ModelAttribute Equipo equipo, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return "equipo_formulario";
        }
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
    @GetMapping("/equipos/excel")
    public ResponseEntity<byte[]> exportarExcel(@RequestParam(required = false) String texto) throws Exception {
        List<Equipo> equipos;
        if (texto == null || texto.isBlank()) {
            equipos = repositorio.findAll(Sort.by("placa"));
        } else {
            equipos = repositorio.buscar(texto);
        }

        byte[] archivo = ExcelExportador.generar(equipos);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventario_thewala.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(archivo);
    }
    @GetMapping("/equipos/pdf")
    public ResponseEntity<byte[]> exportarPdf(@RequestParam(required = false) String texto) throws Exception {
        List<Equipo> equipos;
        if (texto == null || texto.isBlank()) {
            equipos = repositorio.findAll(Sort.by("placa"));
        } else {
            equipos = repositorio.buscar(texto);
        }

        byte[] archivo = PdfExportador.generar(equipos);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventario_thewala.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
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