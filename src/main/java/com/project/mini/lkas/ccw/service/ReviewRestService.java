package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.constant.Url;
import com.project.mini.lkas.ccw.model.Ingredient;
import com.project.mini.lkas.ccw.model.Post;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ReviewRestService {

        RestTemplate restTemplate = new RestTemplate();

        @Value("${blogger.blog.id}")
        private String blogId;

        @Value("${blogger.api.key}")
        private String apiKey;

    public List<Post> getAllReviews() {

        String appendUrl1 = Url.getAllPosts.replace("{BLOGID}", blogId);
        String appendUrl2 = appendUrl1.replace("{APIKEY}",apiKey);
        String appendUrl3 = appendUrl2.replace("{HOWMANYPOSTS}", "500");

        String posts = restTemplate.getForObject(appendUrl3, String.class);

        JsonReader jr = Json.createReader(new StringReader(posts));
        JsonObject jo = jr.readObject();

        JsonArray postsArray = jo.getJsonArray("items");

        List<Post> blogPosts = new ArrayList<>();

        //for loop to iterate through the postsArray
        for (int i = 0; i < postsArray.size(); i++) {
            JsonObject aPost = postsArray.getJsonObject(i);
            Post p = new Post(aPost.getString("published"), aPost.getString("url"), aPost.getString("title"), aPost.getString("content"));
            blogPosts.add(p);
        }

        return blogPosts;

    }

    public List<Ingredient> getAllIngredients() {
       
        String appendUrl = Url.getAllIngredients.replace("{APIKEY}",apiKey);

        String ingredients = restTemplate.getForObject(appendUrl, String.class);

        JsonReader jr = Json.createReader(new StringReader(ingredients));
        JsonObject jo = jr.readObject();

        JsonArray ingredientsArray = jo.getJsonArray("meals");

        List<Ingredient> ingredientList = new ArrayList<>();

        //for loop to iterate through the postsArray
        for (int i = 0; i < ingredientsArray.size(); i++) {
            JsonObject anIngredient = ingredientsArray.getJsonObject(i);
            Ingredient ing = new Ingredient(anIngredient.getString("idIngredient"), anIngredient.getString("strIngredient"));
            ingredientList.add(ing);
        }

        return ingredientList;

    }

}
