package com.project.mini.lkas.ccw.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.project.mini.lkas.ccw.model.Basket;
import com.project.mini.lkas.ccw.model.Listing;
import com.project.mini.lkas.ccw.model.Recipe;
import com.project.mini.lkas.ccw.service.BasketService;
import com.project.mini.lkas.ccw.service.ListingService;
import com.project.mini.lkas.ccw.service.RecipeService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/recipe")
public class RecipeController {

    @Autowired
    private RecipeService rs;

    @Autowired
    private ListingService ls;

    @Autowired
    private BasketService bs;

    @Value("${mealdb.apikey}")
    private String LAWSONKEY;

    RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/find/{basket-id}")
    public String getListOfRecipes(@PathVariable("basket-id") String basketId, HttpSession session, Model model,
            RedirectAttributes redirect) {

        // Retrieve Ingredient converted String
        String currentUser = (String) session.getAttribute("loggedInUser");
        String ingredientSearchString = rs.retrieveIngredientString(currentUser, basketId);

        // Call the RECIPE api
        List<Listing> results = ls.getListOfRecipes(ingredientSearchString);

        if (results.isEmpty()) {
            String message = "No recipes found with these combination of ingredients. Please try other combinations.";
            redirect.addFlashAttribute("fail", message);

            return "redirect:/recipe/basket";
        }

        model.addAttribute("listings", results);
        model.addAttribute("universalTitle", "Matching Recipes Found");

        // save into session for quicksave feature
        session.setAttribute("searchListings", results);

        return "recipeListing";
    }

    @GetMapping("/view/{recipe-id}")
    public String viewARecipe(@PathVariable("recipe-id") String recipeId, Model model, HttpSession session)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {

        // Get the recipe details
        // Instantiate recipe object
        Recipe recipe = rs.getRecipeDetails(recipeId);
        model.addAttribute("recipe", recipe);

        // Helper method for ingredient measurement pair
        List<String> ingredientMeasurementPair = rs.retrieveIngredientMeasurementPair(recipe);
        model.addAttribute("pairs", ingredientMeasurementPair);

        // Helper method for igredient icon thing
        List<String> ingredients = rs.retrieveIngredientList(recipe);
        model.addAttribute("list", ingredients);

        // Save the most current recipe details to session
        // Used in ReviewController.java
        session.setAttribute("currentRecipe", recipe);

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

        String title = "My Saved Recipes";
        redirect.addFlashAttribute("univeralMessage", title);

        return "redirect:/recipe/view/" + recipeId;
    }

    @GetMapping("/saved")
    public String showSavedListing(Model model, HttpSession session) {
        // Get current user
        String currentUser = (String) session.getAttribute("loggedInUser");

        List<Listing> listings = rs.retrieveSavedRecipes(currentUser);

        model.addAttribute("listings", listings);

        return "saveListing";

    }

    @GetMapping("/delete/{recipe-id}")
    public String deleteSavedListing(@PathVariable("recipe-id") String recipeId, HttpSession session,
            RedirectAttributes redirect) {
        // Get current user
        String currentUser = (String) session.getAttribute("loggedInUser");

        // Delete the saved recipe
        rs.deleteSavedRecipe(currentUser, recipeId);

        String message = "Recipe has been deleted successfully.";

        redirect.addFlashAttribute("message", message);

        return "redirect:/recipe/saved";
    }

    @GetMapping("/randomOne")
    public String getRandomRecipe(Model model, HttpSession session)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        // Call the random recipe api
        Recipe recipe = rs.getRandomRecipe();
        model.addAttribute("recipe", recipe);

        // Get ingredients thingy
        // Helper method for ingredient measurement pair
        List<String> ingredientMeasurementPair = rs.retrieveIngredientMeasurementPair(recipe);
        model.addAttribute("pairs", ingredientMeasurementPair);

        // Helper method for igredient icon thing
        List<String> ingredients = rs.retrieveIngredientList(recipe);
        model.addAttribute("list", ingredients);

        // Save the most current recipe details to session
        // Used in ReviewController.java
        session.setAttribute("currentRecipe", recipe);

        return "recipeDetails";
    }

    @GetMapping("/randomTen")
    public String getTenRandomRecipe(Model model, HttpSession session) {
        // Call the random recipe api
        List<Listing> listings = ls.getRandomTenRecipe();
        model.addAttribute("listings", listings);

        // save in session to persist for quicksave function
        session.setAttribute("tenlistings", listings);

        String title = "Ten Random Recipes";
        model.addAttribute("universalTitle", title);

        // helper to show the save button
        model.addAttribute("save", "true");

        return "recipeListing";
    }

    @GetMapping("/randomTen/quicksave/{recipe-id}")
    public String quickSaveRecipe(@PathVariable("recipe-id") String recipeId, Model model, HttpSession session,
            RedirectAttributes redirect) {

        // Get user, send user and recipe ID to redis
        String currentUser = (String) session.getAttribute("loggedInUser");
        rs.saveRecipe(currentUser, recipeId);

        // retrieve from saved attributed to show the same list of recipes
        model.addAttribute("listings", session.getAttribute("tenlistings"));

        String message = "Recipe has been saved successfully.";
        // redirect.addFlashAttribute("message", message);
        model.addAttribute("message", message);

        String title = "Ten Random Recipes";
        // redirect.addFlashAttribute("universalTitle", title);
        model.addAttribute("universalTitle", title);

        // helper to show the save button
        model.addAttribute("save", "true");

        return "recipeListing";
    }

    @GetMapping("/search")
    public String displaySearchPage() {
        return "recipeSearch";
    }

    @PostMapping("/search")
    public String searchRecipe(@RequestParam String search, Model model, HttpSession session) {
        List<Listing> listings = ls.searchRecipe(search);

        if (listings.isEmpty()) {
            String message = "No recipes found. Please try another search term for the dish name or make sure the recipe ID you are looking up is 5 digits long.";
            model.addAttribute("fail", message);

            return "recipeSearch";
        }

        model.addAttribute("listings", listings);

        String title = "Matching Recipes Found";
        model.addAttribute("universalTitle", title);

        // save into session for quicksave feature
        session.setAttribute("searchListings", listings);

        return "recipeListing";
    }

    @GetMapping("/basket")
    public String searchWithBasket(Model model, HttpSession session) {
        List<Basket> baskets = new ArrayList<>();

        String currentUser = (String) session.getAttribute("loggedInUser");

        baskets = bs.getAllBaskets(currentUser);

        model.addAttribute("baskets", baskets);

        String title = "Choose a basket to search for recipes";
        model.addAttribute("universalTitle", title);

        return "basketListing";
    }

    @GetMapping("/latest")
    public String latestRecipesAdded(Model model, HttpSession session) {
        List<Listing> listings = ls.retrieveLatestRecipes();

        model.addAttribute("listings", listings);

        // save in session to persist for quicksave function
        session.setAttribute("latestListings", listings);

        String title = "Latest Added Recipes";
        model.addAttribute("universalTitle", title);

        return "recipeListing";
    }

    @GetMapping("/latest/quicksave/{recipe-id}")
    public String latestQuickSaveRecipe(@PathVariable("recipe-id") String recipeId, Model model, HttpSession session,
            RedirectAttributes redirect) {

        // Get user, send user and recipe ID to redis
        String currentUser = (String) session.getAttribute("loggedInUser");
        rs.saveRecipe(currentUser, recipeId);

        // retrieve from saved attributed to show the same list of recipes
        model.addAttribute("listings", session.getAttribute("latestListings"));

        String message = "Recipe has been saved successfully.";
        model.addAttribute("message", message);

        String title = "Latest Added Recipes";
        model.addAttribute("universalTitle", title);

        return "recipeListing";
    }

    @GetMapping("/matching/quicksave/{recipe-id}")
    public String matchingQuickSaveRecipe(@PathVariable("recipe-id") String recipeId, Model model, HttpSession session,
            RedirectAttributes redirect) {

        // retrieve from saved attributed to show the same list of recipes
        model.addAttribute("listings", session.getAttribute("searchListings"));

        String message = "Recipe has been saved successfully.";
        // redirect.addFlashAttribute("message", message);
        model.addAttribute("message", message);

        String title = "Matching Recipes Found";
        // redirect.addFlashAttribute("universalTitle", title);
        model.addAttribute("universalTitle", title);

        return "recipeListing";
    }

}
