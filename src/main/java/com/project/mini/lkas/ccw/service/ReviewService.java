package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.constant.Url;
import com.project.mini.lkas.ccw.model.Review;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ReviewService {

    @Autowired
    private MapRepo mp;

    @Value("${blogger.blog.id}")
    private String blogid;

    @Value("${blogger.client.id}")
    private String clientId;

    @Value("${blogger.client.secret}")
    private String clientSecret;

    @Value("${blogger.refresh.token}")
    private String refreshToken;

    RestTemplate restTemplate = new RestTemplate();

    public JsonObject createJsonData(Review review) {

        // {
        // "kind": "blogger#post",
        // "blog": {
        // "id": "8070105920543249955"
        // },
        // "title": "A new post",
        // "content": "With <b>exciting</b> content..."
        // }

        // Create body content first.
        String title = "<h1>Review for " + review.getMealTitle() + "</h1>";

        String body2 = "Reviewed by : " + review.getName() + "<br><br>";
        String body3 = "<img src='" + review.getMealPicture() + "'><br><br>";
        String body4 = review.getReviewTitle() + "<br>";
        String body5 = review.getReviewMessage() + "<br><br>";

        // Concant all bodies
        String content = body2 + body3 + body4 + body5;

        // Create json object by parts first
        JsonObject bid = Json.createObjectBuilder().add("id", blogid).build();

        JsonObject built = Json.createObjectBuilder()
                .add("kind", "blogger#post")
                .add("blog", bid)
                .add("title", title)
                .add("content", content)
                .build();

        return built;

    }

    public String refreshAuthCode() {

        // do the request nbody
        // get the response
        // parse the response
        // then rethrn the new oath code

        JsonObject jsonPayload = Json.createObjectBuilder()
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("refresh_token", refreshToken)
                .add("grant_type", "refresh_token")
                .build();

        RequestEntity<String> request = RequestEntity
                .post(Url.refreshAuthCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonPayload.toString(), String.class);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        // Take response and extract the new token
        JsonReader jr = Json.createReader(new StringReader(response.getBody()));
        JsonObject jo = jr.readObject();

        String newToken = jo.getString("access_token");

        System.out.println("---------NEWTOKEN IN REFRESH AUTH CODE---------");
        System.out.println("New token: " + newToken);
        return newToken;

    }

    public void saveReview(Review review) {
        // Stringyfy the review object json
        // format goal json is like
        // {"reviews": [{entry1}, {entry2}, {entry3}]}

        // Check if the user got entries saved in the ccwSavedRecipes
        Boolean ifExists = mp.hashKeyExists(RedisKeys.ccwReviews, review.getEmail());

        if (ifExists == true) {
            // Means have existing reviews, so append the entry and add on
            String existingReviews = mp.get(RedisKeys.ccwReviews, review.getEmail());

            JsonReader jr = Json.createReader(new StringReader(existingReviews));
            JsonObject existingReviewRecords = jr.readObject();

            JsonArray exsistingReviewArray = existingReviewRecords.getJsonArray("reviews");

            // Create Json Object for the new review
            // the new {entry}
            JsonObject newReview = Json.createObjectBuilder()
                    .add("name", review.getName())
                    .add("email", review.getEmail())
                    .add("idMeal", review.getIdMeal())
                    .add("mealTitle", review.getMealTitle())
                    .add("mealPicture", review.getMealPicture())
                    .add("reviewTitle", review.getReviewTitle())
                    .add("reviewMessage", review.getReviewMessage())
                    .add("bloggerUrl", review.getBloggerUrl())
                    .add("publishedOn", review.getPublishedOn())
                    .build();

            // add new entry to the array [{existing entry}, {added entry}]
            JsonArray updatedReviewArray = Json.createArrayBuilder(exsistingReviewArray)
                    .add(newReview)
                    .build();

            // rebuild the new json object now
            JsonObject updatedRecords = Json.createObjectBuilder(existingReviewRecords)
                    .add("reviews", updatedReviewArray)
                    .build();

            mp.update(RedisKeys.ccwReviews, review.getEmail(), updatedRecords.toString());

        } else {
            // means firsst saved enrty, so create it
            // build the {entry}
            JsonObject aReview = Json.createObjectBuilder()
                    .add("name", review.getName())
                    .add("email", review.getEmail())
                    .add("idMeal", review.getIdMeal())
                    .add("mealTitle", review.getMealTitle())
                    .add("mealPicture", review.getMealPicture())
                    .add("reviewTitle", review.getReviewTitle())
                    .add("reviewMessage", review.getReviewMessage())
                    .add("bloggerUrl", review.getBloggerUrl())
                    .add("publishedOn", review.getPublishedOn())
                    .build();

            JsonArray reviewArray = Json.createArrayBuilder().add(aReview).build();

            // build the {review: [{entry1}, {entry2}, {entry3}]}
            JsonObject reviewRecords = Json.createObjectBuilder()
                    .add("reviews", reviewArray)
                    .build();

            mp.create(RedisKeys.ccwReviews, review.getEmail(), reviewRecords.toString());
        }
    }

    public List<Review> getReviews(String currentUserEmail) {
        List<Review> retrieved = new ArrayList<>();

        // check if have or not else return empty array list
        if (mp.hashKeyExists(RedisKeys.ccwReviews, currentUserEmail) == true) {
            String reviewObject = mp.get(RedisKeys.ccwReviews, currentUserEmail);

            JsonReader jr = Json.createReader(new StringReader(reviewObject));
            JsonObject jo = jr.readObject();
            JsonArray reviewArray = jo.getJsonArray("reviews");

            for (int i = 0; i < reviewArray.size(); i++) {

                JsonObject aReview = reviewArray.getJsonObject(i);

                Review r = new Review();
                r.setName(aReview.getString("name"));
                r.setEmail(aReview.getString("email"));
                r.setIdMeal(aReview.getString("idMeal"));
                r.setMealTitle(aReview.getString("mealTitle"));
                r.setMealPicture(aReview.getString("mealPicture"));
                r.setReviewTitle(aReview.getString("reviewTitle"));
                r.setReviewMessage(aReview.getString("reviewMessage"));
                r.setBloggerUrl(aReview.getString("bloggerUrl"));
                r.setPublishedOn(aReview.getString("publishedOn"));

                retrieved.add(r);

            }

            // RETURN POPULATED REVIEW LIST
            return retrieved;

        } else {
            // return empty list
            return retrieved;
        }
    }

    public List<Review> getAllReviews() {
        List<Review> retrieved = new ArrayList<>();

        if (mp.keyExists(RedisKeys.ccwReviews) == true) {

            List<Object> listOfReviews = mp.getValues(RedisKeys.ccwReviews);

            // outer loop to get each review object
            // basically it will go through each hashkey
            // so basically go through each person review then add to list

            for (int i = 0; i < listOfReviews.size(); i++) {
                String reviewObject = listOfReviews.get(i).toString();

                JsonReader jr = Json.createReader(new StringReader(reviewObject));
                JsonObject jo = jr.readObject();
                JsonArray reviewArray = jo.getJsonArray("reviews");

                // inner loop now to instantiate each review object
                for (int x = 0; x < reviewArray.size(); x++) {

                    JsonObject aReview = reviewArray.getJsonObject(x);

                    Review r = new Review();
                    r.setName(aReview.getString("name"));
                    r.setEmail(aReview.getString("email"));
                    r.setIdMeal(aReview.getString("idMeal"));
                    r.setMealTitle(aReview.getString("mealTitle"));
                    r.setMealPicture(aReview.getString("mealPicture"));
                    r.setReviewTitle(aReview.getString("reviewTitle"));
                    r.setReviewMessage(aReview.getString("reviewMessage"));
                    r.setBloggerUrl(aReview.getString("bloggerUrl"));
                    r.setPublishedOn(aReview.getString("publishedOn"));

                    retrieved.add(r);
                }
            }

            // Sort the list by publishedOn date in descending order (latest first)
            retrieved.sort((r1, r2) -> {
                // Parse the 'publishedOn' string to ZonedDateTime
                DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
                ZonedDateTime date1 = ZonedDateTime.parse(r1.getPublishedOn(), formatter);
                ZonedDateTime date2 = ZonedDateTime.parse(r2.getPublishedOn(), formatter);

                // Sort in descending order (latest first)
                return date2.compareTo(date1);
            });
            
            // RETURN POPULATED REVIEW LIST
            return retrieved;

        } else {
            // return empty list
            return retrieved;
        }
    }

}
