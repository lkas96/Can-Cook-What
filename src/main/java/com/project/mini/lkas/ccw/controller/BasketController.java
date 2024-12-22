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
        System.out.println(currentUser);

        baskets = bs.getAllBaskets(currentUser);

        model.addAttribute("baskets", baskets);

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
            HttpSession session) {

        List<String> ingredientList = Arrays.asList(ingredients.split(","));

        Basket b = new Basket(name, ingredientList);

        String currentUser = (String) session.getAttribute("loggedInUser");

        // System.out.println("PRINTING USERE--------------------");
        // System.out.println(currentUser);
        // System.out.println(b.toString());
        System.out.println(b.getId());

        bs.createBasket(currentUser, b);

        return "redirect:/basket";
    }

    @GetMapping("/delete/{basket-name}")
    public String postMethodName(@PathVariable("basket-name") String basketName, RedirectAttributes redirect, HttpSession session) {

        String user = (String) session.getAttribute("loggedInUser");

        bs.deleteBasket(user, basketName);

        String message = "Basket Item : " + basketName + " has been deleted.";

        redirect.addFlashAttribute("message", message);

        return "redirect:/basket";
    }

}
