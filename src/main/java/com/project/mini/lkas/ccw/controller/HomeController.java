package com.project.mini.lkas.ccw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/home")
public class HomeController {
    
    @GetMapping()
    public String displayHome() {
        return "home";
    }
    
}
