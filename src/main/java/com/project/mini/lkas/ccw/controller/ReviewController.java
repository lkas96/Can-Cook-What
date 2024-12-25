package com.project.mini.lkas.ccw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.mini.lkas.ccw.model.Review;
import com.project.mini.lkas.ccw.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private UserService us;


    @GetMapping("")
    public String displayForm(Model model, HttpSession session) {

        //get user from session
        String currentUserEmail = (String) session.getAttribute("loggedInUser");
        
        String currentUserName = us.getUser(currentUserEmail);

        //get current recipe object as well to display image/title/id
        //also needed when processing the review form
        model.addAttribute("recipe", session.getAttribute("currentRecipe"));

        Review rev = new Review();
        
        //in regular cotroller
        System.out.println("currentUserName: " + currentUserName);
        System.out.println("currentUserEmail: " + currentUserEmail);

        rev.setName(currentUserName);
        rev.setEmail(currentUserEmail);

        System.out.println("Review: " + rev.toString());

        model.addAttribute("review", rev);
        model.addAttribute("currentName", currentUserName);

        return "reviewForm";
        
    }
    

}
