package com.project.mini.lkas.ccw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.mini.lkas.ccw.model.Recipe;
import com.project.mini.lkas.ccw.service.ListingService;
import com.project.mini.lkas.ccw.service.RecipeService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private RecipeService rs;

    @Autowired
    private ListingService ls;
    
    @GetMapping()
    public String displayHome(Model model, HttpSession session) {
        String currentUserEmail = (String) session.getAttribute("loggedInUser");

        // greet user with welcome message
        String welcomeMessage = "Welcome " + currentUserEmail + "!";
        model.addAttribute("welcomeMessage", welcomeMessage);

        //random recipe of the day
        Recipe randomOfTheDay = rs.recipeOfTheDay();
        model.addAttribute("recipe", randomOfTheDay);

        // baskets count summary
        int basketCount = rs.getBasketCount(currentUserEmail);
        String basketCountMessage = "You have " + basketCount + " basket items.";
        model.addAttribute("basketCountMessage", basketCountMessage);

        //latest meals added
        int latestMealsCount = ls.getLatestMealsCount();
        String latestMealsCountMessage = "There are " + latestMealsCount + " latest recipes added.";
        model.addAttribute("latestMealsCountMessage", latestMealsCountMessage);

        return "home";
    }
    
}
