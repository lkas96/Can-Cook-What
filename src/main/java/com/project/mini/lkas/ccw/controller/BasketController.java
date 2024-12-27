package com.project.mini.lkas.ccw.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.project.mini.lkas.ccw.model.Basket;
import com.project.mini.lkas.ccw.service.BasketService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/basket")
public class BasketController {

    @Autowired
    private BasketService bs;

    @GetMapping()
    public String getListOfBaskets(Model model, HttpSession session) {

        List<Basket> baskets = new ArrayList<>();

        String currentUser = (String) session.getAttribute("loggedInUser");

        baskets = bs.getAllBaskets(currentUser);

        model.addAttribute("baskets", baskets);

        String title = "Your Baskets";
        model.addAttribute("universalTitle", title);

        return "basketListing";
    }

    @GetMapping("/create")
    public String displayBasketCreationForm(Model model) {

        Basket b = new Basket();

        model.addAttribute("basket", b);

        return "basketForm";
    }

    @PostMapping("/create")
    public String addBasket(@RequestParam String name, @RequestParam String ingredients, Model model,
            HttpSession session, RedirectAttributes redirect) {

        //primary processing
        List<String> ingredientList = Arrays.asList(ingredients.split(","));
        

        //custom validation to see if the ingredients list matches whatever mealdb api has
        //if not, return an error message with whatever that ingredient is
                
        List<String> invalidIngredient = bs.validateIngredients(ingredientList);

        //if invalid contains something, return the error message
        if (invalidIngredient.size() > 0) {
            redirect.addFlashAttribute("invalidIngredient", invalidIngredient);

            return "redirect:/basket/create";
        }

        Basket b = new Basket(name, ingredientList);

        String currentUser = (String) session.getAttribute("loggedInUser");

        bs.createBasket(currentUser, b);

        redirect.addFlashAttribute("message", "Basket (" + name + ") has been created successfully.");

        return "redirect:/basket";
    }

    @GetMapping("/delete/{basket-name}")
    public String postMethodName(@PathVariable("basket-name") String basketName, RedirectAttributes redirect, HttpSession session) {

        String user = (String) session.getAttribute("loggedInUser");

        bs.deleteBasket(user, basketName);

        String message = "Basket (" + basketName + ") has been deleted.";

        redirect.addFlashAttribute("message", message);

        return "redirect:/basket";
    }

}
