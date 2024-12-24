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
import com.project.mini.lkas.ccw.service.RecipeRestService;
import com.project.mini.lkas.ccw.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService rs;

    @Autowired
    private ListingRestService lrs;

    @Autowired
    private RecipeRestService rrs;

    @GetMapping("/find/{basket-id}")
    public String getListOfRecipes(@PathVariable("basket-id") String basketId, HttpSession session, Model model) {

        // System.out.println("Current User: " + currentUser);
        // System.out.println("Basket ID: " + basketId);

        // Retrieve Ingredient converted String
        String currentUser = (String) session.getAttribute("loggedInUser");
        String ingredientSearchString = rs.retrieveIngredientString(currentUser, basketId);
        System.out.println("Ingredient String: " + ingredientSearchString);

        // Call the RECIPE api
        List<Listing> results = lrs.getListOfRecipes(ingredientSearchString);

        model.addAttribute("listings", results);

        return "recipeListing";
    }

    @GetMapping("/view/{recipe-id}")
    public String viewARecipe(@PathVariable("recipe-id") String recipeId, Model model)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {

        // Get the recipe details
        // Instantiate recipe object
        Recipe recipe = lrs.getRecipeDetails(recipeId);
        model.addAttribute("recipe", recipe);

        // Helper method for ingredient measurement pair
        List<String> ingredientMeasurementPair = rs.retrieveIngredientMeasurementPair(recipe);
        model.addAttribute("pairs", ingredientMeasurementPair);

        // Helper method for igredient icon thing
        List<String> ingredients = rs.retrieveIngredientList(recipe);
        model.addAttribute("list", ingredients);

        return "recipeDetails";

    }

    @GetMapping("/save/{recipe-id}")
    public String saveRecipe(@PathVariable("recipe-id") String recipeId, Model model, HttpSession session,
            RedirectAttributes redirect) {

        // Get user, send user and recipe ID to redis
        String currentUser = (String) session.getAttribute("loggedInUser");
        rs.saveRecipe(currentUser, recipeId);

        String message = "Recipe has been saved successfully.";
        redirect.addFlashAttribute("message", message);

        return "redirect:/recipe/view/" + recipeId;
    }

    @GetMapping("/saved")
    public String showSavedListing(Model model, HttpSession session) {
        // Get current user
        String currentUser = (String) session.getAttribute("loggedInUser");

        List<Listing> listings = rrs.retrieveSavedRecipes(currentUser);

        model.addAttribute("listings", listings);

        System.out.println("Saved Listings: " + listings);

        return "saveListing";

    }

    @GetMapping("/delete/{recipe-id}")
    public String deleteSavedListing(@PathVariable("recipe-id") String recipeId, HttpSession session, RedirectAttributes redirect) {
        // Get current user
        String currentUser = (String) session.getAttribute("loggedInUser");

        // Delete the saved recipe
        rs.deleteSavedRecipe(currentUser, recipeId);

        String message = "Recipe has been deleted successfully.";
        
        redirect.addFlashAttribute("message", message);

        return "redirect:/recipe/saved";
    }

    @GetMapping("randomOne")
    public String getRandomRecipe(Model model) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        // Call the random recipe api
        Recipe recipe = lrs.getRandomRecipe();
        model.addAttribute("recipe", recipe);

        // Get ingredients thingy
        // Helper method for ingredient measurement pair
        List<String> ingredientMeasurementPair = rs.retrieveIngredientMeasurementPair(recipe);
        model.addAttribute("pairs", ingredientMeasurementPair);

        // Helper method for igredient icon thing
        List<String> ingredients = rs.retrieveIngredientList(recipe);
        model.addAttribute("list", ingredients);

        return "recipeDetails";
    }

    @GetMapping("randomTen")
    public String getTenRandomRecipe(Model model) {
        // Call the random recipe api
        List<Listing> listings = lrs.getRandomTenRecipe();
        model.addAttribute("listings", listings);

        return "recipeListing";
    }

}
