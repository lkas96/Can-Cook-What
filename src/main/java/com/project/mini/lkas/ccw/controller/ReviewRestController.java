package com.project.mini.lkas.ccw.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.project.mini.lkas.ccw.model.Ingredient;
import com.project.mini.lkas.ccw.model.Post;
import com.project.mini.lkas.ccw.service.ReviewRestService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/all")
public class ReviewRestController {

    @Autowired
    private ReviewRestService rrs;
    
    @GetMapping("/reviews")
    public ResponseEntity<List<Post>> getAllReviews() {

        List<Post> reviews = rrs.getAllReviews();
        
        return ResponseEntity.ok().body(reviews);
    }

    //Get all list of ingredients from the api db
    @GetMapping("/ingredients")
    public ResponseEntity<List<Ingredient>> getAllIngredients() {

        List<Ingredient> ingredients = rrs.getAllIngredients();
        
        return ResponseEntity.ok().body(ingredients);

    }
    
}
