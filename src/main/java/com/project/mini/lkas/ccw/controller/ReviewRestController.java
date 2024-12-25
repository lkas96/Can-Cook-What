package com.project.mini.lkas.ccw.controller;

import java.io.StringReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;

import com.project.mini.lkas.ccw.model.Review;
import com.project.mini.lkas.ccw.service.ReviewRestService;
import com.project.mini.lkas.ccw.constant.Url;
import com.project.mini.lkas.ccw.model.Recipe;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/review")
public class ReviewRestController {

    @Autowired
    private ReviewRestService rrs;

    RestTemplate restTemplate = new RestTemplate();

    @Value("${blogger.blog.id}")
    private String blogId;

    @Value("${blogger.api.key}")
    private String apiKey;

    @PostMapping("/new")
    public String createReview(@ModelAttribute Review review, HttpSession session) {

        // System.out.println("rest controller hit");
        // System.out.println("Review: " + review.toString());
        // haven add the recipe details yet part 2 of model

        Recipe current = (Recipe) session.getAttribute("currentRecipe");

        review.setidMeal(current.getIdMeal()); // get dish id
        review.setMealTitle(current.getStrMeal()); // get dish name
        review.setMealPicture(current.getStrMealThumb()); // get thumbnail

        // Now review is properly set up
        System.out.println("Review: " + review.toString());

        

        // Prepare json data for external send to blogger api
        // Create the json object
        // Pass to the blogger api post url thingy
        // get the response
        // if response okay, add to redis database log entry and return success page
        // else return error page and try again later.

        JsonObject jsonPayload = rrs.createJsonData(review);

        System.out.println("---------JSON PAYLOAD TO HTML FORMAT JSON BODY DATA---------");
        System.out.println("JsonPayload: " + jsonPayload.toString());

        //refresh the authcode
        String authCode = rrs.refreshAuthCode();

        System.out.println("---------BACK TO REST CONTROLLER---------");
        System.out.println("AuthCode: " + authCode);

        // https://developers.google.com/blogger/docs/3.0/using#AddingAPost
        // POST https://www.googleapis.com/blogger/v3/blogs/8070105920543249955/posts/
        // Authorization: /* OAuth 2.0 token here */
        // Content-Type: application/json

        String appendedUrl = Url.postToBlogger.replace("{BLOGID}", blogId);
        String appendedUrl2 = appendedUrl + "?key=" + apiKey;

        RequestEntity<String> request = RequestEntity
                .post(appendedUrl2)
                .header("Authorization","Bearer " + authCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonPayload.toString(), String.class);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        //Take response and extract the new token
        JsonReader jr = Json.createReader(new StringReader(response.getBody()));
        JsonObject jo = jr.readObject();

        System.out.println("Response: " + jo.toString());


        






        

        return "";
    }

}
