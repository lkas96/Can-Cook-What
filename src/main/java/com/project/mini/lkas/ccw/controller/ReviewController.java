package com.project.mini.lkas.ccw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.project.mini.lkas.ccw.model.Recipe;
import com.project.mini.lkas.ccw.model.Review;
import com.project.mini.lkas.ccw.service.ReviewService;
import com.project.mini.lkas.ccw.service.UserService;

import jakarta.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    private UserService us;

    @Autowired
    private ReviewService rs;

    @GetMapping("")
    public String displayForm(Model model, HttpSession session) {

        // get user from session
        String currentUserEmail = (String) session.getAttribute("loggedInUser");

        String currentUserName = us.getUser(currentUserEmail);

        // get current recipe object as well to display image/title/id
        // also needed when processing the review form
        model.addAttribute("recipe", session.getAttribute("currentRecipe"));

        Review rev = new Review();

        rev.setName(currentUserName);
        rev.setEmail(currentUserEmail);

        model.addAttribute("review", rev);
        model.addAttribute("currentName", currentUserName);

        return "reviewForm";

    }

    @PostMapping("/new")
    public String createReview(@ModelAttribute Review review, HttpSession session, RedirectAttributes redirect) {

        // haven add the recipe details yet part 2 of model
        Recipe current = (Recipe) session.getAttribute("currentRecipe");
        review.setIdMeal(current.getIdMeal()); // get dish id
        review.setMealTitle(current.getStrMeal()); // get dish name
        review.setMealPicture(current.getStrMealThumb()); // get thumbnail

        Boolean successful = rs.postToBlogger(review);

        if (successful == true) {

            String message = "Review posted successfully!";
            redirect.addFlashAttribute("message", message);

            return "redirect:/review/myreviews";

        } else {

            String message = "Review posting failed!";
            redirect.addFlashAttribute("message2", message);

            return "redirect:/review/new";

        }
    }

    @GetMapping("/myreviews")
    public String displayMyReviews(Model model, HttpSession session) {

        String currentUserEmail = (String) session.getAttribute("loggedInUser");

        List<Review> reviews = rs.getReviews(currentUserEmail);

        model.addAttribute("reviews", reviews);

        return "reviewListing";

    }

    @GetMapping("community")
    public String displayAllReviews(Model model, HttpSession session) {

        List<Review> reviews = rs.getAllReviews();

        model.addAttribute("allreviews", reviews);

        return "reviewListingAll";

    }

    @GetMapping("/delete/{post-id}")
    public String postMethodName(@PathVariable("post-id") String postId, RedirectAttributes redirect, HttpSession session) {

        String currentUser = (String) session.getAttribute("loggedInUser");

        Boolean successful = rs.deleteReview(postId, currentUser);

        if (successful == false) {

            String message = "Review deletion failed!";

            redirect.addFlashAttribute("message2", message);

            return "redirect:/review/myreviews";

        } else {

            String message = "Review deleted successfully!";

            redirect.addFlashAttribute("message", message);
    
            return "redirect:/review/myreviews";
        }

        
    }

}
