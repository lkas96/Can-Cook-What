package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.catalina.connector.Response;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.constant.Url;
import com.project.mini.lkas.ccw.model.Review;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class ReviewRestService {

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

        //Take response and extract the new token
        JsonReader jr = Json.createReader(new StringReader(response.getBody()));
        JsonObject jo = jr.readObject();

        String newToken = jo.getString("access_token");

        System.out.println("---------NEWTOKEN IN REFRESH AUTH CODE---------");
        System.out.println("New token: " + newToken);
        return newToken;

    }

}
