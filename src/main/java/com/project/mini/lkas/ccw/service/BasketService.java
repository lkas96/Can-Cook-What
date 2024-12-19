package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.model.Basket;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class BasketService {

    @Autowired
    private MapRepo mp;

    public void createBasket(String user, Basket basket) {

        // Basket demo,[demo1, demo2, demo3]

        // Check if user got entry in ccwContainers
        Boolean ifExists = mp.hashKeyExists(RedisKeys.ccwContainers, user);

        if (ifExists == true) {
            // Means have existing basket, so append the entry

            // Retrieve existing basket records first, then add on object, then save again.
            String existingBasketJson = mp.get(RedisKeys.ccwContainers, user);

            JsonObject existingBasketRecords = Json.createReader(new StringReader(existingBasketJson)).readObject();

            JsonArray currentBasketArray = existingBasketRecords.getJsonArray("baskets");

            // //For BasketValue
            // //Basket : [name, ingredients]
            // Because it is a List string, for loop through and add one by one.
            JsonArrayBuilder ingredArrayBuilder = Json.createArrayBuilder();
            for (String ingredient : basket.getIngredients()) {
                ingredArrayBuilder.add(ingredient);
            }
            JsonArray ingredArray = ingredArrayBuilder.build();

            // Create a new JsonObject for the new basket
            JsonObject newBasket = Json.createObjectBuilder()
                    .add("basketName", basket.getName())
                    .add("ingredients", ingredArray)
                    .build();

            // add new basket to existing array [basket 1 stuff], [basket 2 stuff] , ....
            JsonArray updatedBasketArray = Json.createArrayBuilder(currentBasketArray)
                    .add(newBasket)
                    .build();

            // update
            JsonObject updatedBasketRecords = Json.createObjectBuilder(existingBasketRecords)
                    .add("baskets", updatedBasketArray)
                    .build();

            mp.create(RedisKeys.ccwContainers, user, updatedBasketRecords.toString());

        } else {
            // Means add new as first time.

            // //For BasketValue
            // //Basket : [name, ingredients]
            // Because it is a List string, for loop through and add one by one.
            JsonArrayBuilder ingredArrayBuilder = Json.createArrayBuilder();
            for (String ingredient : basket.getIngredients()) {
                ingredArrayBuilder.add(ingredient);
            }
            JsonArray ingredArray = ingredArrayBuilder.build();

            JsonObject eachBasket = Json.createObjectBuilder()
                    .add("basketName", basket.getName())
                    .add("ingredients", ingredArray)
                    .build();

            JsonArray basketArray = Json.createArrayBuilder().add(eachBasket).build();

            // "nested"
            JsonObject basketRecords = Json.createObjectBuilder()
                    .add("email", user)
                    .add("baskets", basketArray)
                    .build();

            System.out.println(basketRecords.toString());

            mp.create(RedisKeys.ccwContainers, user, basketRecords.toString());
        }

    }

    public List<Basket> getAllBaskets(String user) {

        // check if exissts
        if (mp.hashKeyExists(RedisKeys.ccwContainers, user) == true) {
            String basketObject = mp.get(RedisKeys.ccwContainers, user);

            JsonReader jr = Json.createReader(new StringReader(basketObject));
            JsonObject containers = jr.readObject();
            JsonArray basketArray = containers.getJsonArray("baskets");

            List<Basket> baskets = new ArrayList<>();

            for (int i = 0; i < basketArray.size(); i++) {

                JsonObject aBasket = basketArray.getJsonObject(i);

                Basket b = new Basket();
                b.setName(aBasket.getString("basketName"));

                // Convert json array into List<String>
                JsonArray ingredients = aBasket.getJsonArray("ingredients");
                List<String> temp = new ArrayList<>();

                for (int x = 0; x < ingredients.size(); x++) {
                    temp.add(ingredients.getString(x));
                }

                b.setIngredients(temp);

                baskets.add(b);
            }

            return baskets;

        } else {

            List<Basket> emptyBasket = new ArrayList<>();
            return emptyBasket;
        }

    }

    public void deleteBasket(String user, String basketName) {
        String basketObject = mp.get(RedisKeys.ccwContainers, user);

        JsonReader jr = Json.createReader(new StringReader(basketObject));
        JsonObject jo = jr.readObject();

        // Read all the baskets first
        JsonArray basketsArray = jo.getJsonArray("baskets");

        // Temp array to store for before after delete
        // Create empty, loop through the basket arrray, find the name matches and do
        // not add that to the updated array.
        JsonArrayBuilder updatedBasketsBuilder = Json.createArrayBuilder();

        for (int bb = 0; bb < basketsArray.size(); bb++) {
            JsonObject aBasket = basketsArray.getJsonObject(bb);
            if (!basketName.equals(aBasket.getString("basketName"))) {
                // take the not equals one, skip through it
                // so essentially "deletes"
                updatedBasketsBuilder.add(aBasket);
            }
        }

        //Build the array now
        JsonArray updatedBasketsBuilt = updatedBasketsBuilder.build();

        //Create the JSON Object full again.
        JsonObject update = Json.createObjectBuilder()
        .add("baskets", updatedBasketsBuilt).build();

        mp.update(RedisKeys.ccwContainers, user, update.toString());
    }

}
