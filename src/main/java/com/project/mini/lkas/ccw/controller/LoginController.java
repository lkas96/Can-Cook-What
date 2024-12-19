package com.project.mini.lkas.ccw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import com.project.mini.lkas.ccw.model.Login;
import com.project.mini.lkas.ccw.service.LoginService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService ls;

    @GetMapping()
    public String displayLoginForm(Model model) {

        if (model.containsAttribute("login")){

            return "loginForm";

        } else {

            Login l  = new Login();

            model.addAttribute("login", l);
    
            return "loginForm";

        }
    }
    

    @PostMapping("/validate")
    public RedirectView loginForm(@Valid @ModelAttribute("login") Login login, RedirectAttributes redirect, HttpSession httpsession) {

        Boolean loginSuccess = ls.loginValidation(login.getEmail(), login.getPassword());

        if (loginSuccess == false) {

            redirect.addFlashAttribute("message",
                    "Invalid login details. Please check your email/id and password. Please try again.");

            return new RedirectView("/login");

        } else {

            httpsession.setAttribute("loggedInUser", login.getEmail());

            return new RedirectView("/home");
        }
    }

}
