package com.project.mini.lkas.ccw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import com.project.mini.lkas.ccw.model.User;
import com.project.mini.lkas.ccw.service.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/register")
public class UserController {

    @Autowired
    private UserService us;

    @GetMapping()
    public String displayRegistrationForm(Model model) {

        User u = new User();

        model.addAttribute("user", u);

        return "registerForm";
    }

    @PostMapping("/create")
    public String createUserAccount(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {

        if (result.hasErrors()) {
            return "registerForm";
        }

        us.createUser(user);

        String welcomeMessage = "Registered successfully! You may now login.";

        model.addAttribute("message", welcomeMessage);

        return "registerForm";
    }

}
