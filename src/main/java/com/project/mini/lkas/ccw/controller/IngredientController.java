package com.project.mini.lkas.ccw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.project.mini.lkas.ccw.model.Ingredient;
import com.project.mini.lkas.ccw.service.IngredientService;



@Controller
@RequestMapping("/ingredients")
public class IngredientController {

    @Autowired
    private IngredientService is;
    
    @GetMapping()
    public String displayAllIngredients(Model model) {

        List<Ingredient> ingredients = is.getAllIngredients();

        model.addAttribute("ingredients", ingredients);
        
        return "ingredientListing";
    }
    
}
