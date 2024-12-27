package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.constant.Url;
import com.project.mini.lkas.ccw.model.Ingredient;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;



@Service
public class IngredientService {

    RestTemplate restTemplate = new RestTemplate();

    public List<Ingredient> getAllIngredients() {
        
        String ingres = restTemplate.getForObject(Url.getAllIngredients, String.class);

        JsonReader jr = Json.createReader(new StringReader(ingres));
        JsonObject jo = jr.readObject();

        JsonArray ingredientsArray = jo.getJsonArray("meals");
        
        List<Ingredient> ingredients = new ArrayList<>();
        
        //loop through the array instantiate ingredient object
        for (int i = 0; i < ingredientsArray.size(); i++) {
            JsonObject anIngredient = ingredientsArray.getJsonObject(i);
            Ingredient ing = new Ingredient(anIngredient.getString("idIngredient"), anIngredient.getString("strIngredient"));
            ingredients.add(ing);
        }

        return ingredients;
    }

}
