package com.project.mini.lkas.ccw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping() //handles root url
public class SiteController {

    @GetMapping("")
    public String displayLandingPage() {
        return "redirect:/landing.html";
    }  
    
}
