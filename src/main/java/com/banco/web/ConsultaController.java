package com.banco.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConsultaController {

    @GetMapping("/consulta")
    public String consulta(Model model) {
        model.addAttribute("paginaActiva", "consulta");
        return "consulta";
    }
}
