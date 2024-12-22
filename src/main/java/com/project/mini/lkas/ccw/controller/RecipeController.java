package com.project.mini.lkas.ccw.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.project.mini.lkas.ccw.model.Listing;
import com.project.mini.lkas.ccw.model.Recipe;
import com.project.mini.lkas.ccw.service.ListingRestService;
import com.project.mini.lkas.ccw.service.RecipeService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService rs;

    @Autowired
    private ListingRestService lrs;

    @GetMapping("/find/{basket-id}")
    public String getListOfRecipes(@PathVariable("basket-id") String basketId, HttpSession session, Model model) {

        // System.out.println("Current User: " + currentUser);
        // System.out.println("Basket ID: " + basketId);

        //Retrieve Ingredient converted String
        String currentUser = (String) session.getAttribute("loggedInUser");
        String ingredientSearchString = rs.retrieveIngredientString(currentUser, basketId);
        System.out.println("Ingredient String: " + ingredientSearchString);

        //Call the RECIPE api
        List<Listing> results = lrs.getListOfRecipes(ingredientSearchString);

        model.addAttribute("listings", results);

        return "recipeListing";
    }

    @GetMapping("/view/{recipe-id}")
    public String viewARecipe(@PathVariable("recipe-id") String recipeId, Model model) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {

        //Get the recipe details
        //Instantiate recipe object
        Recipe recipe = lrs.getRecipeDetails(recipeId);
        model.addAttribute("recipe", recipe);

        //Helper method to retrieve the ingredient measurement pair
        List<String> ingredientMeasurementPair = rs.retrieveIngredientMeasurementPair(recipe);
        model.addAttribute("pairs", ingredientMeasurementPair);

        //Helper method to put the ingredients into a list for image generation
        List<String> ingredients = rs.retrieveIngredientList(recipe);
        model.addAttribute("list", ingredients);

        return "recipeDetails";

    }
    
    


}
