package com.thewala.inventario;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller

public class HolaControlador {

    @GetMapping("/hola")
    public String saludar(Model model) {
        model.addAttribute("nombre", "IPS The Wala");
        return "hola";
    }
}