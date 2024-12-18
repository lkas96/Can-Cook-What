package com.project.mini.lkas.ccw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String loginForm(@Valid @ModelAttribute("login") Login login, Model model, HttpSession httpsession) {

        Boolean loginSuccess = ls.loginValidation(login.getEmail(), login.getPassword());

        if (loginSuccess == false) {

            model.addAttribute("message",
                    "Invalid login details. Please check your email/id and password. Please try again.");

            return "loginForm";

        } else {

            httpsession.setAttribute("loggedInUser", login.getEmail());

            return "home";
        }
    }

}
