package com.project.mini.lkas.ccw.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.project.mini.lkas.ccw.model.Post;
import com.project.mini.lkas.ccw.service.ReviewRestService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api")
public class ReviewRestController {

    @Autowired
    private ReviewRestService rrs;
    
    @GetMapping("/allReviews")
    public ResponseEntity<List<Post>> getAllReviews() {
        List<Post> reviews = rrs.getAllReviews();
        
        return ResponseEntity.ok().body(reviews);
    }
    
}
