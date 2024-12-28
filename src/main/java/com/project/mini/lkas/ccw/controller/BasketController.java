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

        String title = "My Baskets";
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

        // primary processing
        List<String> ingredientList = Arrays.asList(ingredients.split(","));

        // custom validation to see if the ingredients list matches whatever mealdb api
        // has
        // if not, return an error message with whatever that ingredient is

        List<String> invalidIngredient = bs.validateIngredients(ingredientList);

        // if invalid contains something, return the error message
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

    @GetMapping("/delete")
    public String postMethodName(@RequestParam("basket-name") String basketName, @RequestParam("basket-id") String basketId, RedirectAttributes redirect,
            HttpSession session) {

        String user = (String) session.getAttribute("loggedInUser");

        bs.deleteBasket(user, basketId);

        System.out.println("Basket ID: " + basketId);
        System.out.println("Basket Name: " + basketName);

        String message = "Basket (" + basketName + ") has been deleted.";

        redirect.addFlashAttribute("message", message);

        return "redirect:/basket";
    }

    @GetMapping("/edit/{basket-id}")
    public String updateForm(@PathVariable("basket-id") String basketId, Model model, HttpSession session) {

        String currentUser = (String) session.getAttribute("loggedInUser");

        Basket b = bs.getBasketById(currentUser, basketId);

        // additional processing to remove [] from the ingredients list
        String ingredients = b.getIngredients().toString();
        ingredients = ingredients.substring(1, ingredients.length() - 1);

        // since cannot set as basket model must be array
        // add to attribute for display purposes on form
        model.addAttribute("ingredients", ingredients);

        model.addAttribute("basket", b);

        //Save to session for edit if got errors
        session.setAttribute("basket", b);
        session.setAttribute("ingredients", ingredients);

        return "basketFormEdit";
    }

    @PostMapping("/edit/{basket-id}")
    public String editBasket(@RequestParam String id, @RequestParam String name, @RequestParam String ingredients,
            Model model, HttpSession session, RedirectAttributes redirect) {

        String message = "Basket has been updated successully.";

        //validate inrgedients first
        // primary processing
        List<String> ingredientList = Arrays.asList(ingredients.split(","));

        // custom validation to see if the ingredients list matches whatever mealdb api
        // has
        // if not, return an error message with whatever that ingredient is

        List<String> invalidIngredient = bs.validateIngredients(ingredientList);

        // if invalid contains something, return the error message
        if (invalidIngredient.size() > 0) {
            redirect.addFlashAttribute("invalidIngredient", invalidIngredient);

            //get session   attributes
            Basket b = (Basket) session.getAttribute("basket");
            String ing = (String) session.getAttribute("ingredients");

            System.out.println("Basket: " + b);
            System.out.println("Ingredients: " + ing);

            redirect.addFlashAttribute("basket", (Basket) session.getAttribute("basket"));
            redirect.addFlashAttribute("ingredients", (String) session.getAttribute("ingredients"));

            return "redirect:/basket/edit/" + id;
        }

        // if valid, update the basket
        bs.editBasket(id, name, ingredientList, (String) session.getAttribute("loggedInUser"));





        redirect.addFlashAttribute("message", message);

        return "redirect:/basket";
    }

}
