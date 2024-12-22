package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@Service
public class RecipeService {

    @Autowired
    private MapRepo mp;

    public String retrieveIngredientString(String user, String basketId) {

        //Access currentUser's basket and get the ingredients
        //Convert the ingredients to string, ready for use in the URL
        String existingBasketJson = mp.get(RedisKeys.ccwContainers, user);

        JsonObject existingBasketRecords = Json.createReader(new StringReader(existingBasketJson)).readObject();

        JsonArray currentBasketArray = existingBasketRecords.getJsonArray("baskets");

        for (int i = 0; i < currentBasketArray.size(); i++) {

            JsonObject basket = currentBasketArray.getJsonObject(i);

            if (basket.getString("id").equals(basketId)) {

                JsonArray ingredients = basket.getJsonArray("ingredients");

                //Convert the ingredients to string, ready for use in the URL
                StringBuilder sb = new StringBuilder();

                //Piece string array into a single string, comma seperated
                for (int j = 0; j < ingredients.size(); j++) {
                    sb.append(ingredients.getString(j).trim()); //trimm remove whitespace
                    if (j != ingredients.size() - 1) {
                        sb.append(",");
                    }
                }

                String ingredientSearchString = sb.toString();

                return ingredientSearchString;
            }
        }

        return null;
    }

}
