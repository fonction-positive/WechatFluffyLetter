package com.fluffyletter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    @GetMapping("/admin")
    public String adminNoSlash() {
        return "redirect:/admin/";
    }

    @GetMapping("/admin/")
    public String adminWithSlash() {
        return "forward:/admin/index.html";
    }
}
