package com.project.mini.lkas.ccw.controller;

import java.io.StringReader;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.constant.Url;
import com.project.mini.lkas.ccw.model.Recipe;
import com.project.mini.lkas.ccw.model.Review;
import com.project.mini.lkas.ccw.service.ReviewService;
import com.project.mini.lkas.ccw.service.UserService;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private UserService us;

    @Autowired
    private ReviewService rs;

    RestTemplate restTemplate = new RestTemplate();

    @Value("${blogger.blog.id}")
    private String blogId;

    @Value("${blogger.api.key}")
    private String apiKey;

    @GetMapping("")
    public String displayForm(Model model, HttpSession session) {

        // get user from session
        String currentUserEmail = (String) session.getAttribute("loggedInUser");

        String currentUserName = us.getUser(currentUserEmail);

        // get current recipe object as well to display image/title/id
        // also needed when processing the review form
        model.addAttribute("recipe", session.getAttribute("currentRecipe"));

        Review rev = new Review();

        // in regular cotroller
        System.out.println("currentUserName: " + currentUserName);
        System.out.println("currentUserEmail: " + currentUserEmail);

        rev.setName(currentUserName);
        rev.setEmail(currentUserEmail);

        System.out.println("Review: " + rev.toString());

        model.addAttribute("review", rev);
        model.addAttribute("currentName", currentUserName);

        return "reviewForm";

    }

    @PostMapping("/new")
    public String createReview(@ModelAttribute Review review, HttpSession session) {

        // haven add the recipe details yet part 2 of model
        Recipe current = (Recipe) session.getAttribute("currentRecipe");
        review.setIdMeal(current.getIdMeal()); // get dish id
        review.setMealTitle(current.getStrMeal()); // get dish name
        review.setMealPicture(current.getStrMealThumb()); // get thumbnail

        // Prepare json data for external send to blogger api
        JsonObject jsonPayload = rs.createJsonData(review);

        // refresh the authcode
        String authCode = rs.refreshAuthCode();

        // https://developers.google.com/blogger/docs/3.0/using#AddingAPost
        // POST https://www.googleapis.com/blogger/v3/blogs/8070105920543249955/posts/
        // Authorization: /* OAuth 2.0 token here */
        // Content-Type: application/json

        String appendedUrl = Url.postToBlogger.replace("{BLOGID}", blogId);
        String appendedUrl2 = appendedUrl + "?key=" + apiKey;

        RequestEntity<String> request = RequestEntity
                .post(appendedUrl2)
                .header("Authorization", "Bearer " + authCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonPayload.toString(), String.class);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        // Take response and extract the new token
        JsonReader jr = Json.createReader(new StringReader(response.getBody()));
        JsonObject jo = jr.readObject();

        // Extract the url and date published and add it to the review object
        // then add review object into redis records

        String url = jo.getString("url");
        String published = jo.getString("published");
        review.setBloggerUrl(url);
        review.setPublishedOn(published);

        // Add review to redis
        rs.saveReview(review);

        return "redirect:/review/myreviews";
    }

    @GetMapping("/myreviews")
    public String displayMyReviews(Model model, HttpSession session) {

        // get user from session
        String currentUserEmail = (String) session.getAttribute("loggedInUser");

        // get current review object
        List<Review> reviews = rs.getReviews(currentUserEmail);

        model.addAttribute("reviews", reviews);

        return "reviewListing";

    }

}
