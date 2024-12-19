package com.project.mini.lkas.ccw.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/logout")
public class LogoutController {

    @GetMapping()
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }


}
