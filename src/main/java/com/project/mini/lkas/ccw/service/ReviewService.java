package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
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
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonNumber;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class ReviewService {

    @Autowired
    private MapRepo mp;

    @Value("${blogger.client.id}")
    private String clientId;

    @Value("${blogger.client.secret}")
    private String clientSecret;

    @Value("${blogger.refresh.token}")
    private String refreshToken;

    @Value("${blogger.blog.id}")
    private String blogId;

    @Value("${blogger.api.key}")
    private String apiKey;

    RestTemplate restTemplate = new RestTemplate();

    // Helper method broken down
    // Used for postToBlogger method
    public JsonObject createJsonData(Review review) {

        // Create body content first.
        String title = "Review for " + review.getMealTitle();

        String body2 = "Review By : " + review.getName() + "<br><br>";
        String body3 = "<img src='" + review.getMealPicture() + "'><br><br>";
        String body4 = review.getReviewTitle() + "<br>";
        String body5 = review.getReviewMessage() + "<br><br>";
        String body6 = "Recipe ID: " + review.getIdMeal() + "<br><br>";

        // Concant all bodies
        String content = body2 + body3 + body4 + body5 + body6;

        // Create json object by parts first
        JsonObject bid = Json.createObjectBuilder().add("id", blogId).build();

        JsonObject built = Json.createObjectBuilder()
                .add("kind", "blogger#post")
                .add("blog", bid)
                .add("title", title)
                .add("content", content)
                .build();

        return built;

    }

    // Helper method broken down
    // Used for postToBlogger method
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

        return newToken;

    }

    public Boolean postToBlogger(Review review) {

        // Prepare json data for external send to blogger api
        JsonObject jsonPayload = createJsonData(review);

        // refresh the authcode
        String authCode = refreshAuthCode();

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
        review.setBloggerUrl(url);

        // Parse the input string to ZonedDateTime
        String published = jo.getString("published");

        // Parse the input string to ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(published);

        // Convert ZonedDateTime to epoch (seconds since Unix epoch)
        long epoch = zonedDateTime.toEpochSecond();

        review.setPublishOnEpoch(epoch);

        String postId = jo.getString("id");
        review.setPostId(postId);

        // Add review to redis
        saveReview(review);

        return true;
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
                    .add("publishOnEpoch", review.getPublishOnEpoch())
                    .add("postId", review.getPostId())
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
                    .add("publishOnEpoch", review.getPublishOnEpoch())
                    .add("postId", review.getPostId())
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

                JsonValue publishOnEpochValue = aReview.get("publishOnEpoch");
                long publishOnEpoch = ((JsonNumber) publishOnEpochValue).longValue();
                r.setPublishOnEpoch(publishOnEpoch);

                r.setPostId(aReview.getString("postId"));

                // Use helper instance variable in model to correct date fix
                // epoch to whatever format I want sickeningass stupid ass timezones and shit
                // I am too dumb for this
                // Convert epoch time to Instant
                Instant instant = Instant.ofEpochSecond(publishOnEpoch);

                // Convert to Singapore time zone (Asia/Singapore)
                ZonedDateTime singaporeTime = instant.atZone(ZoneId.of("Asia/Singapore"));

                // Format the date and time in the desired format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
                String formattedDate = singaporeTime.format(formatter);
                r.setHelperDate(formattedDate);

                retrieved.add(r);

            }

            // sort by epoch date
            // latest review first
            retrieved.sort(Comparator.comparingLong(Review::getPublishOnEpoch).reversed());

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

                    JsonValue publishOnEpochValue = aReview.get("publishOnEpoch");
                    long publishOnEpoch = ((JsonNumber) publishOnEpochValue).longValue();
                    r.setPublishOnEpoch(publishOnEpoch);

                    r.setPostId(aReview.getString("postId"));

                    // Use helper instance variable in model to correct date fix
                    // epoch to whatever format I want sickeningass stupid ass timezones and shit
                    // I am too dumb for this
                    // Convert epoch time to Instant
                    Instant instant = Instant.ofEpochSecond(publishOnEpoch);

                    // Convert to Singapore time zone (Asia/Singapore)
                    ZonedDateTime singaporeTime = instant.atZone(ZoneId.of("Asia/Singapore"));

                    // Format the date and time in the desired format
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
                    String formattedDate = singaporeTime.format(formatter);
                    r.setHelperDate(formattedDate);

                    retrieved.add(r);
                }
            }

            // sort by epoch date
            // latest review first
            retrieved.sort(Comparator.comparingLong(Review::getPublishOnEpoch).reversed());

            // RETURN POPULATED REVIEW LIST
            return retrieved;

        } else {
            // return empty list
            return retrieved;
        }
    }

    public Boolean deleteReview(String postId, String currentUser) {

        String newAuth = refreshAuthCode();

        String appendedUrl = Url.deletePost.replace("{BLOGID}", blogId).replace("{POSTID}", postId);

        RequestEntity<Void> request = RequestEntity
                .delete(appendedUrl)
                .header("Authorization", "Bearer " + newAuth)
                .build();

        ResponseEntity<Void> resp = restTemplate.exchange(request, Void.class);
        if (resp.getStatusCode().is2xxSuccessful()) {

            // Delete from server success
            // now delete from redis duh

            String existingReviews = mp.get(RedisKeys.ccwReviews, currentUser);

            JsonReader jr = Json.createReader(new StringReader(existingReviews));
            JsonObject existingReviewRecords = jr.readObject();
            JsonArray existingReviewArray = existingReviewRecords.getJsonArray("reviews");

            JsonArrayBuilder updatedArrayBuilder = Json.createArrayBuilder();

            // loops through the array of reviews
            // finds matching postid then skip adding to new array
            // since not like list cannot remove like that, do the opposite
            for (JsonValue value : existingReviewArray) {
                JsonObject aReview = (JsonObject) value;
                if (!aReview.getString("postId").equals(postId)) {
                    updatedArrayBuilder.add(aReview);
                }
            }

            // rebuild the new json object now
            JsonObject updatedRecords = Json.createObjectBuilder(existingReviewRecords)
                    .add("reviews", updatedArrayBuilder)
                    .build();

            mp.update(RedisKeys.ccwReviews, currentUser, updatedRecords.toString());

            return true;

        } else {

            return false;
        }
    }

    public Review getReviewById(String postId, String currentUserEmail) {

        // System.out.println("------------------------------------------------------------");
        // System.out.println("REVIEW CALLED");;
        // System.out.println("POST ID: " + postId);

        String reviewObject = mp.get(RedisKeys.ccwReviews, currentUserEmail);

        JsonReader jr = Json.createReader(new StringReader(reviewObject));
        JsonObject jo = jr.readObject();
        JsonArray reviewArray = jo.getJsonArray("reviews");

        // System.out.println("------------------------------------------------------------");
        // System.out.println("REVIEW ARRAY : " + reviewArray.toString());

        for (int i = 0; i < reviewArray.size(); i++) {

            JsonObject aReview = reviewArray.getJsonObject(i);

            // Match the post id found
            if (aReview.getString("postId").equals(postId)) {

                // System.out.println("------------------------------------------------------------");
                // System.out.println("gotten the review details : " + aReview.toString());

                Review r = new Review();
                r.setName(aReview.getString("name"));
                r.setEmail(aReview.getString("email"));
                r.setIdMeal(aReview.getString("idMeal"));
                r.setMealTitle(aReview.getString("mealTitle"));
                r.setMealPicture(aReview.getString("mealPicture"));
                r.setReviewTitle(aReview.getString("reviewTitle"));
                r.setReviewMessage(aReview.getString("reviewMessage"));
                r.setBloggerUrl(aReview.getString("bloggerUrl"));

                JsonValue publishOnEpochValue = aReview.get("publishOnEpoch");
                long publishOnEpoch = ((JsonNumber) publishOnEpochValue).longValue();
                r.setPublishOnEpoch(publishOnEpoch);

                r.setPostId(aReview.getString("postId"));

                // Use helper instance variable in model to correct date fix
                // epoch to whatever format I want sickeningass stupid ass timezones and shit
                // I am too dumb for this
                // Convert epoch time to Instant
                Instant instant = Instant.ofEpochSecond(publishOnEpoch);

                // Convert to Singapore time zone (Asia/Singapore)
                ZonedDateTime singaporeTime = instant.atZone(ZoneId.of("Asia/Singapore"));

                // Format the date and time in the desired format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm");
                String formattedDate = singaporeTime.format(formatter);
                r.setHelperDate(formattedDate);

                // System.out.println("------------------------------------------------------------");
                // System.out.println("gotten the review details : " + r.toString());

                return r;

            }
        }

        return null; // should not get here, did not include whatever
        // failsafe catch try pretty confident but bad practice w/e
    }

    public Boolean updateReview(String postId, Review review, String currentUser) {
        // update the post in redis first
        // then update the blogger one omg ew later when go back to controlelr
        // controller will call the next reviewrestservice to post

        String existingReviews = mp.get(RedisKeys.ccwReviews, currentUser);

        JsonReader jr = Json.createReader(new StringReader(existingReviews));
        JsonObject existingReviewRecords = jr.readObject();
        JsonArray existingReviewArray = existingReviewRecords.getJsonArray("reviews");

        JsonArrayBuilder updatedReviewsBuilder = Json.createArrayBuilder();

        // loops through the array of reviews
        // finds matching postid then update
        for (int i = 0; i < existingReviewArray.size(); i++) {
            JsonObject aReview = existingReviewArray.getJsonObject(i);

            if (aReview.getString("postId").equals(postId)) {
                // update the review
                // override whatever with the same hashkey
                JsonObject updatedReview = Json.createObjectBuilder(aReview)
                        .add("reviewTitle", review.getReviewTitle())
                        .add("reviewMessage", review.getReviewMessage())
                        .build();

                updatedReviewsBuilder.add(updatedReview);
            }
        }

        // build and update the array finalizwe into redsi //put will auto combine ya da
        // yada
        JsonArray updatedArray = updatedReviewsBuilder.build();

        JsonObject updatedRecords = Json.createObjectBuilder(existingReviewRecords)
                .add("reviews", updatedArray)
                .build();

        mp.update(RedisKeys.ccwContainers, currentUser, updatedRecords.toString());
        
        return true;
    }

    // Helper method broken down
    // Used for PATCHToBlogger method
    public JsonObject createJsonDataPatch(Review review) {

        // {
        //     "kind": "blogger#post",
        //     "id": "3445355871727114160",
        //     "blog": {
        //      "id": "8070105920543249955"
        //     },
        //     "url": "http://brettmorgan-test2.blogspot.com/2012/05/new-post_20.html",
        //     "selfLink": "https://www.googleapis.com/blogger/v3/blogs/8070105920543249955/posts/3445355871727114160",
        //     "title": "An updated post",
        //     "content": "With really <b>exciting</b> content..."
        //    }

        // Create body content first. since only need body for patch request
        String body2 = "Review By : " + review.getName() + "<br><br>";
        String body3 = "<img src='" + review.getMealPicture() + "'><br><br>";
        String body4 = review.getReviewTitle() + "<br>";
        String body5 = review.getReviewMessage() + "<br><br>";
        String body6 = "Recipe ID: " + review.getIdMeal() + "<br><br>";

        // Concant all bodies
        String content = body2 + body3 + body4 + body5 + body6;

        // Create json object by parts first
        JsonObject blogidObject = Json.createObjectBuilder().add("id", blogId).build();
        String selfLink = Url.selfLink.replace("{BLOGID}", blogId);
        String selfLink2 = selfLink.replace("{POSTID}", review.getPostId());

        JsonObject built = Json.createObjectBuilder()
                .add("kind", "blogger#post")
                .add("id", review.getPostId())
                .add("blog", blogidObject)
                .add("url", review.getBloggerUrl())
                .add("selfLink", selfLink2)
                .add("title", "Review for " + review.getMealTitle())
                .add("content", content)
                .build();

        return built;

    }

    public Boolean patchToBlogger(Review review) {
        //create the json data to patch to blogger
        //custom method to prepare teh json data, different from the post method one

        JsonObject jsonPayload = createJsonDataPatch(review);

        // refresh the authcode
        String authCode = refreshAuthCode();

        // PATCH NOT SUPPORTED BY RESTTEMPLATE
        // USE TRAIDITIONAL PUT METHOD API BLOGGER ISNTEAD
        // PATCH https://www.googleapis.com/blogger/v3/blogs/8070105920543249955/posts/3445355871727114160
        // Authorization: /* OAuth 2.0 token here */
        // Content-Type: application/json

        String appendedUrl1 = Url.updatePost.replace("{BLOGID}", blogId);
        String appendedUrl2 = appendedUrl1.replace("{POSTID}", review.getPostId());
        String appendedUrl3 = appendedUrl2.replace("{APIKEY}", apiKey);

        RequestEntity<String> request = RequestEntity
                .put(appendedUrl3)
                .header("Authorization", "Bearer " + authCode)
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonPayload.toString(), String.class);

        ResponseEntity<String> response = restTemplate.exchange(request, String.class);

        // take resp then get the updated date and update the redis
        JsonReader jr = Json.createReader(new StringReader(response.getBody()));
        JsonObject jo = jr.readObject();

        String updated = jo.getString("updated");

        //format the date thingy
        // Parse the input string to ZonedDateTime
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(updated);

        // Convert ZonedDateTime to epoch (seconds since Unix epoch)
        long epoch = zonedDateTime.toEpochSecond();

        review.setPublishOnEpoch(epoch);

        // add back redis
        updateReviewStage2(review);
    
        return true;
    }

    public void updateReviewStage2(Review review){
        //helper method to update the review in redis

        //just want to updae title/message/publishedon

        String existingReviews = mp.get(RedisKeys.ccwReviews, review.getEmail());

        JsonReader jr = Json.createReader(new StringReader(existingReviews));
        JsonObject existingReviewRecords = jr.readObject();
        JsonArray existingReviewArray = existingReviewRecords.getJsonArray("reviews");

        JsonArrayBuilder updatedReviewsBuilder = Json.createArrayBuilder();

        // loops through the array of reviews
        // finds matching postid then update
        for (int i = 0; i < existingReviewArray.size(); i++) {
            JsonObject aReview = existingReviewArray.getJsonObject(i);

            if (aReview.getString("postId").equals(review.getPostId())) {
                // update the review
                // override whatever with the same hashkey
                JsonObject updatedReview = Json.createObjectBuilder(aReview)
                        .add("reviewTitle", review.getReviewTitle())
                        .add("reviewMessage", review.getReviewMessage())
                        .add("publishOnEpoch", review.getPublishOnEpoch())
                        .build();

                updatedReviewsBuilder.add(updatedReview);
            }
        }

        JsonArray updatedArray = updatedReviewsBuilder.build();
        
        JsonObject updatedRecords = Json.createObjectBuilder(existingReviewRecords)
                .add("reviews", updatedArray)
                .build();
        
        mp.update(RedisKeys.ccwReviews, review.getEmail(), updatedRecords.toString());
    }


}
