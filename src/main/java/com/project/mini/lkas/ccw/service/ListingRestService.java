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
public class ListingRestService {

    @Value("${mealdb.apikey}")
    private String LAWSONKEY;

    RestTemplate restTemplate = new RestTemplate();

    public List<Listing> getListOfRecipes(String ingredientSearchString) {
        
        System.out.println("IN REST SERVICE NOW ------------------------------------------");
        List<Listing> results = new ArrayList<>();

        //Append URL to add the APIKEY value
        String appendedUrl1 = Url.searchByIngredients.replace("{APIKEY}", LAWSONKEY);
        String appendedUrl2 = appendedUrl1.replace("{INGREDIENTS}", ingredientSearchString);
        
        System.out.println(appendedUrl2);

        //Eexternal api returns array of meals
        //json convert back to object
        //add to result array and return
        System.out.println("ATTEMPING TO GET DATA FROM EXTERNAL API");
        String jsonData = restTemplate.getForObject(appendedUrl2, String.class);

//   ResponseEntity<String> response = restTemplate.exchange(appendedUrl2, HttpMethod.GET, HttpEntity.EMPTY, String.class);
//   System.out.println("Headers: " + response.getHeaders());

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

}
