package com.project.mini.lkas.ccw.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.project.mini.lkas.ccw.model.Ingredient;
import com.project.mini.lkas.ccw.model.Post;
import com.project.mini.lkas.ccw.model.User;
import com.project.mini.lkas.ccw.service.ApiRestService;
import com.project.mini.lkas.ccw.service.UserService;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping(path = "/api", produces = "application/json")
public class ApiRestController {

    @Autowired
    private ApiRestService ars;

    @Autowired
    private UserService us;

    @GetMapping("/users")
    public ResponseEntity<String> getAllUsers() {
        List<User> users = us.getAllUsers();

        //build the json response but do not reveal password
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        for (User user : users) {
            JsonObject jsonData = Json.createObjectBuilder()
                    .add("email", user.getEmail())
                    .add("name", user.getName())
                    .build();
            
            arrayBuilder.add(jsonData);
        }

        String built = arrayBuilder.build().toString();
        
        return ResponseEntity.ok().body(built);
    }
    
    
    //get from blogger api, liimited to latest 500 posts only
    @GetMapping("/reviews")
    public ResponseEntity<List<Post>> getAllReviews() {

        List<Post> reviews = ars.getAllReviews();
        
        return ResponseEntity.ok().body(reviews);
    }

    //Get all list of ingredients from the api db
    @GetMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {

        List<Ingredient> ingredients = ars.getAllIngredients();
        
        return ResponseEntity.ok().body(ingredients);

    }
    
}
