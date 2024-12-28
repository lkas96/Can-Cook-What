package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.constant.Url;
import com.project.mini.lkas.ccw.model.Listing;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class RecipeRestService {

    @Autowired
    private MapRepo mp;

    @Value("${mealdb.apikey}")
    private String LAWSONKEY;

    RestTemplate restTemplate = new RestTemplate();

    public List<Listing> retrieveSavedRecipes(String currentUser) {

        List<Listing> results = new ArrayList<>();

        //check if have existing saved recipes in redis
        // if not, return empty list else get the redis list and return controller
        Boolean existingSaved = mp.hashKeyExists(RedisKeys.ccwSavedRecipes, currentUser);

        if (existingSaved == false) {
            return results;

        }

        // take currentuser saved recipes id from redis
        // get the recipe details from external api using the id
        // instantiate listing object again then send it back to controller

        String savedRecipesJson = mp.get(RedisKeys.ccwSavedRecipes, currentUser);

        JsonReader jr = Json.createReader(new StringReader(savedRecipesJson));
        JsonObject jo = jr.readObject();
        JsonArray savedRecipes = jo.getJsonArray("recipe_id");

        for (int i = 0; i < savedRecipes.size(); i++) {
            String recipeId = savedRecipes.getString(i);

            // get recipe details from external api
            String appendedUrl1 = Url.searchByMealId.replace("{APIKEY}", LAWSONKEY);
            String appendedUrl2 = appendedUrl1.replace("{MEALID}", recipeId);

            String dataFromApi = restTemplate.getForObject(appendedUrl2, String.class);

            //Now reading the api meal object
            JsonReader jr2 = Json.createReader(new StringReader(dataFromApi));
            JsonObject jo2 = jr2.readObject();

            JsonArray meals = jo2.getJsonArray("meals");

            for (int x = 0; x < meals.size(); x++) {

                JsonObject meal = meals.getJsonObject(x);

                Listing list = new Listing();
                list.setStrMeal(meal.getString("strMeal"));
                list.setStrMealThumb(meal.getString("strMealThumb"));
                list.setIdMeal(meal.getString("idMeal"));

                results.add(list);
            }
        }

        return results;
    }

    public List<Listing> retrieveLatestRecipes() {
        List<Listing> newlyAdded = new ArrayList<>();

        //take from mealdb api dduh
        String appendedUrl = Url.latestRecipes.replace("{APIKEY}", LAWSONKEY);

        String dataFromApi = restTemplate.getForObject(appendedUrl, String.class);

        JsonReader jr = Json.createReader(new StringReader(dataFromApi));
        JsonObject jo = jr.readObject();

        JsonArray meals = jo.getJsonArray("meals");

        for (int i = 0; i < meals.size(); i++) {
            JsonObject meal = meals.getJsonObject(i);

            Listing list = new Listing();
            list.setStrMeal(meal.getString("strMeal"));
            list.setStrMealThumb(meal.getString("strMealThumb"));
            list.setIdMeal(meal.getString("idMeal"));

            newlyAdded.add(list);
        }

        return newlyAdded;
    }

}
