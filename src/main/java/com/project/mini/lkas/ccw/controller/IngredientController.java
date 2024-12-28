package com.project.mini.lkas.ccw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.mini.lkas.ccw.model.Ingredient;
import com.project.mini.lkas.ccw.model.Listing;
import com.project.mini.lkas.ccw.service.IngredientService;
import com.project.mini.lkas.ccw.service.ListingService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService is;

    @Autowired
    private ListingService ls;
    
    @GetMapping()
    public String displayAllIngredients(Model model) {

        List<Ingredient> ingredients = is.getAllIngredients();

        model.addAttribute("ingredients", ingredients);
        
        return "ingredientListing";
    }

    @GetMapping("/{ingredient-name}")
    public String displayIngredientRecipes(@PathVariable("ingredient-name") String ingredient, Model model, HttpSession session) {
        
        List<Listing> recipes = ls.getRecipesByIngredient(ingredient);

        if (recipes.isEmpty()) {
            model.addAttribute("universalMessage", "No recipes found with " + ingredient + ".");
        }

        model.addAttribute("listings", recipes);

        String title = "Recipes with " + ingredient;
        model.addAttribute("universalTitle", title);

        model.addAttribute("ingredientSaveButton", true);

        //save into session
        session.setAttribute("ingredientTitle", title);
        session.setAttribute("ingredientsListing", recipes);

        return "recipeListing";
    }
    
}
