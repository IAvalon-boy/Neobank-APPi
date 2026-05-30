package com.banco.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransaccionController {

    @GetMapping("/transacciones")
    public String transacciones(Model model) {
        model.addAttribute("paginaActiva", "transacciones");
        return "transacciones";
    }
}
