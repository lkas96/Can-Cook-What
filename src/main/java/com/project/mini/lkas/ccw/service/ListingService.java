package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.model.Listing;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import com.project.mini.lkas.ccw.constant.Url;

@Service
public class ListingService {

    @Value("${mealdb.apikey}")
    private String LAWSONKEY;

    RestTemplate restTemplate = new RestTemplate();

    public List<Listing> getListOfRecipes(String ingredientSearchString) {

        List<Listing> results = new ArrayList<>();

        // Append URL to add the APIKEY value
        String appendedUrl1 = Url.searchByIngredients.replace("{APIKEY}", LAWSONKEY);
        String appendedUrl2 = appendedUrl1.replace("{INGREDIENTS}", ingredientSearchString);


        // Eexternal api returns array of meals
        // json convert back to object
        // add to result array and return
        String jsonData = restTemplate.getForObject(appendedUrl2, String.class);

        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();

        if (jo.isNull("meals")) {
            return results; // return empty array
        }

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

    public List<Listing> getRandomTenRecipe() {

        List<Listing> results = new ArrayList<>();

        // Append URL to add the APIKEY value
        String appendedUrl = Url.randomTen.replace("{APIKEY}", LAWSONKEY);

        // Eexternal api returns array of meals
        // json convert back to object
        // add to result array and return
        String jsonData = restTemplate.getForObject(appendedUrl, String.class);

        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();

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

    public List<Listing> searchRecipe(String searchString) {

        // FIRST PROCESS, NOW ADDING SEARCH BY NAME AND ALSO MEAL ID

        // Take the search string see if text or numbers
        if (searchString.matches(".*\\d.*")) {
            // If numbers, search by meal id

            List<Listing> results = new ArrayList<>();

            String appendUrl1 = Url.searchByMealId.replace("{APIKEY}", LAWSONKEY);
            String appendUrl2 = appendUrl1.replace("{MEALID}", searchString);

            String jsonData = restTemplate.getForObject(appendUrl2, String.class);

            JsonReader jr = Json.createReader(new StringReader(jsonData));
            JsonObject jo = jr.readObject();

            if (jo.isNull("meals")) {

                return results; // return empty array

            } else {
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

        } else {

            //does not match the numbers so search by name

            // Call the mealdb api add the searchquery to the url
            // return the list of recipes
            // bam done

            List<Listing> results = new ArrayList<>();

            String appendUrl1 = Url.searchRecipe.replace("{APIKEY}", LAWSONKEY);
            String appendUrl2 = appendUrl1.replace("{SEARCHSTRING}", searchString);

            String jsonData = restTemplate.getForObject(appendUrl2, String.class);

            JsonReader jr = Json.createReader(new StringReader(jsonData));
            JsonObject jo = jr.readObject();

            // Similar to search by ingredients however this returns thewholl recipe details
            // and not a list of recipes title/images
            // msut check properly, take out only listing variables from recipe json dump

            if (jo.isNull("meals")) {

                return results; // return empty array

            } else {
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

        }

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

    public int getLatestMealsCount() {
        //reuse retrievelatestrecipes method
        //just count and return duh

        List<Listing> latestMeals = retrieveLatestRecipes();

        return latestMeals.size();
    }

}
