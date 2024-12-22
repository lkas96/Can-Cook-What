package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.model.Listing;
import com.project.mini.lkas.ccw.model.Recipe;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

import com.project.mini.lkas.ccw.constant.Url;

@Service
public class ListingRestService {

    @Value("${mealdb.apikey}")
    private String LAWSONKEY;

    RestTemplate restTemplate = new RestTemplate();

    public List<Listing> getListOfRecipes(String ingredientSearchString) {

        // System.out.println("IN REST SERVICE NOW
        // ------------------------------------------");
        List<Listing> results = new ArrayList<>();

        // Append URL to add the APIKEY value
        String appendedUrl1 = Url.searchByIngredients.replace("{APIKEY}", LAWSONKEY);
        String appendedUrl2 = appendedUrl1.replace("{INGREDIENTS}", ingredientSearchString);

        System.out.println(appendedUrl2);

        // Eexternal api returns array of meals
        // json convert back to object
        // add to result array and return
        System.out.println("ATTEMPING TO GET DATA FROM EXTERNAL API");
        String jsonData = restTemplate.getForObject(appendedUrl2, String.class);

        // ResponseEntity<String> response = restTemplate.exchange(appendedUrl2,
        // HttpMethod.GET, HttpEntity.EMPTY, String.class);
        // System.out.println("Headers: " + response.getHeaders());

        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();

        // System.out.println(jo);

        JsonArray meals = jo.getJsonArray("meals");

        for (int i = 0; i < meals.size(); i++) {

            JsonObject meal = meals.getJsonObject(i);

            Listing list = new Listing();
            list.setStrMeal(meal.getString("strMeal"));
            list.setStrMealThumb(meal.getString("strMealThumb"));
            list.setIdMeal(meal.getString("idMeal"));

            results.add(list);
        }

        return results;
    }

    public Recipe getRecipeDetails(String recipeId) {

        // Take recipe ID, then call the API get details of the recipe

        System.out.println("IN REST SERVICE NOW ------------------------------------------");
        String appendUrl1 = Url.searchByMealId.replace("{APIKEY}", LAWSONKEY);
        String appendUrl2 = appendUrl1.replace("{MEALID}", recipeId);

        // call the api now, take the json response map back to recipe object and return
        // to controller
        String jsonData = restTemplate.getForObject(appendUrl2, String.class);
        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();
        JsonArray meal = jo.getJsonArray("meals");

        Recipe recipe = new Recipe(
                meal.getJsonObject(0).getString("idMeal"),
                meal.getJsonObject(0).getString("strMeal"),
                meal.getJsonObject(0).getString("strCategory"),
                meal.getJsonObject(0).getString("strArea"),
                meal.getJsonObject(0).getString("strInstructions"),
                meal.getJsonObject(0).getString("strMealThumb"),
                meal.getJsonObject(0).getString("strYoutube"),
                meal.getJsonObject(0).getString("strIngredient1"),
                meal.getJsonObject(0).getString("strIngredient2"),
                meal.getJsonObject(0).getString("strIngredient3"),
                meal.getJsonObject(0).getString("strIngredient4"),
                meal.getJsonObject(0).getString("strIngredient5"),
                meal.getJsonObject(0).getString("strIngredient6"),
                meal.getJsonObject(0).getString("strIngredient7"),
                meal.getJsonObject(0).getString("strIngredient8"),
                meal.getJsonObject(0).getString("strIngredient9"),
                meal.getJsonObject(0).getString("strIngredient10"),
                meal.getJsonObject(0).getString("strIngredient11"),
                meal.getJsonObject(0).getString("strIngredient12"),
                meal.getJsonObject(0).getString("strIngredient13"),
                meal.getJsonObject(0).getString("strIngredient14"),
                meal.getJsonObject(0).getString("strIngredient15"),
                meal.getJsonObject(0).getString("strIngredient16"),
                meal.getJsonObject(0).getString("strIngredient17"),
                meal.getJsonObject(0).getString("strIngredient18"),
                meal.getJsonObject(0).getString("strIngredient19"),
                meal.getJsonObject(0).getString("strIngredient20"),
                meal.getJsonObject(0).getString("strMeasure1"),
                meal.getJsonObject(0).getString("strMeasure2"),
                meal.getJsonObject(0).getString("strMeasure3"),
                meal.getJsonObject(0).getString("strMeasure4"),
                meal.getJsonObject(0).getString("strMeasure5"),
                meal.getJsonObject(0).getString("strMeasure6"),
                meal.getJsonObject(0).getString("strMeasure7"),
                meal.getJsonObject(0).getString("strMeasure8"),
                meal.getJsonObject(0).getString("strMeasure9"),
                meal.getJsonObject(0).getString("strMeasure10"),
                meal.getJsonObject(0).getString("strMeasure11"),
                meal.getJsonObject(0).getString("strMeasure12"),
                meal.getJsonObject(0).getString("strMeasure13"),
                meal.getJsonObject(0).getString("strMeasure14"),
                meal.getJsonObject(0).getString("strMeasure15"),
                meal.getJsonObject(0).getString("strMeasure16"),
                meal.getJsonObject(0).getString("strMeasure17"),
                meal.getJsonObject(0).getString("strMeasure18"),
                meal.getJsonObject(0).getString("strMeasure19"),
                meal.getJsonObject(0).getString("strMeasure20"));

                return recipe;
    }

}
