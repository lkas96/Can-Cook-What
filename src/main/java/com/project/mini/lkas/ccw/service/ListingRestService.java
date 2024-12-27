package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.model.Listing;
import com.project.mini.lkas.ccw.model.Recipe;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

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

        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();

        // System.out.println(jo);

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

    public Recipe getRecipeDetails(String recipeId) {

        // Take recipe ID, then call the API get details of the recipe

        // System.out.println("IN REST SERVICE NOW ------------------------------------------");
        String appendUrl1 = Url.searchByMealId.replace("{APIKEY}", LAWSONKEY);
        String appendUrl2 = appendUrl1.replace("{MEALID}", recipeId);

        // call the api now, take the json response map back to recipe object and return
        // to controller
        String jsonData = restTemplate.getForObject(appendUrl2, String.class);
        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();
        JsonArray meals = jo.getJsonArray("meals");

        // Start index 0, because only have one meal
        JsonObject mealdata = meals.getJsonObject(0);

        // Use helper method to check for null value thingy
        Recipe recipe = new Recipe(
                getJsonString(mealdata, "idMeal"),
                getJsonString(mealdata, "strMeal"),
                getJsonString(mealdata, "strCategory"),
                getJsonString(mealdata, "strArea"),
                getJsonString(mealdata, "strInstructions"),
                getJsonString(mealdata, "strMealThumb"),
                getJsonString(mealdata, "strYoutube"),
                getJsonString(mealdata, "strIngredient1"),
                getJsonString(mealdata, "strIngredient2"),
                getJsonString(mealdata, "strIngredient3"),
                getJsonString(mealdata, "strIngredient4"),
                getJsonString(mealdata, "strIngredient5"),
                getJsonString(mealdata, "strIngredient6"),
                getJsonString(mealdata, "strIngredient7"),
                getJsonString(mealdata, "strIngredient8"),
                getJsonString(mealdata, "strIngredient9"),
                getJsonString(mealdata, "strIngredient10"),
                getJsonString(mealdata, "strIngredient11"),
                getJsonString(mealdata, "strIngredient12"),
                getJsonString(mealdata, "strIngredient13"),
                getJsonString(mealdata, "strIngredient14"),
                getJsonString(mealdata, "strIngredient15"),
                getJsonString(mealdata, "strIngredient16"),
                getJsonString(mealdata, "strIngredient17"),
                getJsonString(mealdata, "strIngredient18"),
                getJsonString(mealdata, "strIngredient19"),
                getJsonString(mealdata, "strIngredient20"),
                getJsonString(mealdata, "strMeasure1"),
                getJsonString(mealdata, "strMeasure2"),
                getJsonString(mealdata, "strMeasure3"),
                getJsonString(mealdata, "strMeasure4"),
                getJsonString(mealdata, "strMeasure5"),
                getJsonString(mealdata, "strMeasure6"),
                getJsonString(mealdata, "strMeasure7"),
                getJsonString(mealdata, "strMeasure8"),
                getJsonString(mealdata, "strMeasure9"),
                getJsonString(mealdata, "strMeasure10"),
                getJsonString(mealdata, "strMeasure11"),
                getJsonString(mealdata, "strMeasure12"),
                getJsonString(mealdata, "strMeasure13"),
                getJsonString(mealdata, "strMeasure14"),
                getJsonString(mealdata, "strMeasure15"),
                getJsonString(mealdata, "strMeasure16"),
                getJsonString(mealdata, "strMeasure17"),
                getJsonString(mealdata, "strMeasure18"),
                getJsonString(mealdata, "strMeasure19"),
                getJsonString(mealdata, "strMeasure20"));

        return recipe;
    }

    // Helper method to check for null values in ingredients or the measurement
    // thingy
    private String getJsonString(JsonObject jsonObject, String key) {
        JsonValue value = jsonObject.get(key);
        if (value != null && value.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) value).getString();
        }
        return null; // Return null if key doesn't exist or isn't a string
    }

    public Recipe getRandomRecipe() {

        String appendUrl = Url.randomOne.replace("{APIKEY}", LAWSONKEY);

        String jsonData = restTemplate.getForObject(appendUrl, String.class);
        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();
        JsonArray meals = jo.getJsonArray("meals");

        // Start index 0, because only have one meal
        JsonObject mealdata = meals.getJsonObject(0);

        // Use helper method to check for null value thingy
        Recipe recipe = new Recipe(
                getJsonString(mealdata, "idMeal"),
                getJsonString(mealdata, "strMeal"),
                getJsonString(mealdata, "strCategory"),
                getJsonString(mealdata, "strArea"),
                getJsonString(mealdata, "strInstructions"),
                getJsonString(mealdata, "strMealThumb"),
                getJsonString(mealdata, "strYoutube"),
                getJsonString(mealdata, "strIngredient1"),
                getJsonString(mealdata, "strIngredient2"),
                getJsonString(mealdata, "strIngredient3"),
                getJsonString(mealdata, "strIngredient4"),
                getJsonString(mealdata, "strIngredient5"),
                getJsonString(mealdata, "strIngredient6"),
                getJsonString(mealdata, "strIngredient7"),
                getJsonString(mealdata, "strIngredient8"),
                getJsonString(mealdata, "strIngredient9"),
                getJsonString(mealdata, "strIngredient10"),
                getJsonString(mealdata, "strIngredient11"),
                getJsonString(mealdata, "strIngredient12"),
                getJsonString(mealdata, "strIngredient13"),
                getJsonString(mealdata, "strIngredient14"),
                getJsonString(mealdata, "strIngredient15"),
                getJsonString(mealdata, "strIngredient16"),
                getJsonString(mealdata, "strIngredient17"),
                getJsonString(mealdata, "strIngredient18"),
                getJsonString(mealdata, "strIngredient19"),
                getJsonString(mealdata, "strIngredient20"),
                getJsonString(mealdata, "strMeasure1"),
                getJsonString(mealdata, "strMeasure2"),
                getJsonString(mealdata, "strMeasure3"),
                getJsonString(mealdata, "strMeasure4"),
                getJsonString(mealdata, "strMeasure5"),
                getJsonString(mealdata, "strMeasure6"),
                getJsonString(mealdata, "strMeasure7"),
                getJsonString(mealdata, "strMeasure8"),
                getJsonString(mealdata, "strMeasure9"),
                getJsonString(mealdata, "strMeasure10"),
                getJsonString(mealdata, "strMeasure11"),
                getJsonString(mealdata, "strMeasure12"),
                getJsonString(mealdata, "strMeasure13"),
                getJsonString(mealdata, "strMeasure14"),
                getJsonString(mealdata, "strMeasure15"),
                getJsonString(mealdata, "strMeasure16"),
                getJsonString(mealdata, "strMeasure17"),
                getJsonString(mealdata, "strMeasure18"),
                getJsonString(mealdata, "strMeasure19"),
                getJsonString(mealdata, "strMeasure20"));

        return recipe;
    }

    public List<Listing> getRandomTenRecipe() {

        List<Listing> results = new ArrayList<>();

        // Append URL to add the APIKEY value
        String appendedUrl = Url.randomTen.replace("{APIKEY}", LAWSONKEY);

        // Eexternal api returns array of meals
        // json convert back to object
        // add to result array and return
        String jsonData = restTemplate.getForObject(appendedUrl, String.class);

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

    public List<Listing> searchRecipe(String searchString) {
        
        //Call the mealdb api add the searchquery to the url
        //return the list of recipes
        //bam done

        List<Listing> results = new ArrayList<>();

        String appendUrl1 = Url.searchRecipe.replace("{APIKEY}", LAWSONKEY);
        String appendUrl2 = appendUrl1.replace("{SEARCHSTRING}", searchString);

        String jsonData = restTemplate.getForObject(appendUrl2, String.class);

        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();
        
        //Similar to search by ingredients however this returns thewholl recipe details and not a list of recipes title/images
        //msut check properly, take out only listing variables from recipe json dump

        if (jo.isNull("meals")) {
            
            return results; //return empty array

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
