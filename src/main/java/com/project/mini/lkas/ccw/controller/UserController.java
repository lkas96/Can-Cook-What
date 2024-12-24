package com.project.mini.lkas.ccw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String createUserAccount(@Valid @ModelAttribute("user") User user, BindingResult result, Model model, RedirectAttributes redirect) {

        if (result.hasErrors()) {
            return "registerForm";
        }

        //ADD VALIDATION FOR EXISTING EMAIL
        Boolean emailRegistered = us.emailRegistered(user.getEmail());

        if (emailRegistered == true) {
            String message = "Email already registered. Please use another email.";

            model.addAttribute("emailUsed", message);

            return "registerForm";
        }

        us.createUser(user);

        redirect.addFlashAttribute("success", "Registration successful. Welcome to the CCW family! You may now log in. 😊");

        return "redirect:/login";
    }

}
