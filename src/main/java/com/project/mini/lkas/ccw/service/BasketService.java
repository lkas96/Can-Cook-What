package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.model.Basket;
import com.project.mini.lkas.ccw.model.Ingredient;
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

    @Autowired
    private IngredientService is;

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
                // capitalize the first letter
                ingredient = ingredient.toLowerCase().trim();
                ingredient = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);

                ingredArrayBuilder.add(ingredient);
            }
            JsonArray ingredArray = ingredArrayBuilder.build();

            // Create a new JsonObject for the new basket
            JsonObject newBasket = Json.createObjectBuilder()
                    .add("id", basket.getId())
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

                // capitalize the first letter
                ingredient = ingredient.toLowerCase().trim();
                ingredient = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);

                ingredArrayBuilder.add(ingredient);
            }
            JsonArray ingredArray = ingredArrayBuilder.build();

            JsonObject eachBasket = Json.createObjectBuilder()
                    .add("id", basket.getId())
                    .add("basketName", basket.getName())
                    .add("ingredients", ingredArray)
                    .build();

            JsonArray basketArray = Json.createArrayBuilder().add(eachBasket).build();

            // "nested"
            JsonObject basketRecords = Json.createObjectBuilder()
                    .add("baskets", basketArray)
                    .build();

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

            // LOOP THROUGH FROM THE LAST INDEX
            // SO THE LATEST ONE WILL APPEAR ON TOP
            // ON THE BASKET LISTING PAGE YAAAAAS
            // EASIER THAN ADDED INFRONT FO THE WHATEVER REDIS MAP ARRAY
            // too dumb cant figure it out
            for (int i = basketArray.size() - 1; i >= 0; i--) {

                JsonObject aBasket = basketArray.getJsonObject(i);

                Basket b = new Basket();
                b.setId(aBasket.getString("id"));
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

    public void deleteBasket(String user, String basketId) {
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
            if (!basketId.equals(aBasket.getString("id"))) {
                // take the not equals one, skip through it
                // so essentially "deletes"
                updatedBasketsBuilder.add(aBasket);
            }
        }

        // Build the array now
        JsonArray updatedBasketsBuilt = updatedBasketsBuilder.build();

        // Create the JSON Object full again.
        JsonObject update = Json.createObjectBuilder()
                .add("baskets", updatedBasketsBuilt).build();

        mp.update(RedisKeys.ccwContainers, user, update.toString());
    }

    public List<String> validateIngredients(List<String> ingredientList) {

        // for sending back to the user
        List<String> invalidIngredients = new ArrayList<>();

        // Call ingredient service to get all the ingredients from db
        List<Ingredient> allIngredients = is.getAllIngredients();

        List<String> databaseList = new ArrayList<>();

        // cnvert the list of ingredients into a list of strings
        for (Ingredient i : allIngredients) {
            databaseList.add(i.getName().toLowerCase().trim());
        }

        // now loop check ugh
        // user list vs database list
        // REMMEBER TO COMPARE IN LOWERCASE
        // TRIM WHITE SPACE ALSO
        for (String ingredient : ingredientList) {
            if (!databaseList.contains(ingredient.toLowerCase().trim())) {
                invalidIngredients.add(ingredient);
            }
        }

        return invalidIngredients;
    }

    public Basket getBasketById(String user, String basketId) {

        String basketObject = mp.get(RedisKeys.ccwContainers, user);

        JsonReader jr = Json.createReader(new StringReader(basketObject));
        JsonObject containers = jr.readObject();
        JsonArray basketArray = containers.getJsonArray("baskets");

        // loop through and find matching basket
        for (int i = basketArray.size() - 1; i >= 0; i--) {

            JsonObject aBasket = basketArray.getJsonObject(i);

            if (aBasket.getString("id").equals(basketId)) {
                Basket b = new Basket();
                b.setId(aBasket.getString("id"));
                b.setName(aBasket.getString("basketName"));

                // Convert json array into List<String>
                JsonArray ingredients = aBasket.getJsonArray("ingredients");
                List<String> temp = new ArrayList<>();

                for (int x = 0; x < ingredients.size(); x++) {
                    temp.add(ingredients.getString(x));
                }

                b.setIngredients(temp);

                return b;
            }
        }
        return null; // no basket some error //should not get here.
    }

    public void editBasket(String basketId, String basketName, List<String> ingredients, String user) {
        // set the new values

        // Basket b = new Basket();
        // b.setId(basketId);
        // b.setIngredients(ingredients);

        // override/udpate the basket in redis
        String basketObject = mp.get(RedisKeys.ccwContainers, user);

        JsonReader jr = Json.createReader(new StringReader(basketObject));
        JsonObject jo = jr.readObject();

        // Read all the baskets first
        JsonArray basketsArray = jo.getJsonArray("baskets");

        JsonArrayBuilder updatedBasketsBuilder = Json.createArrayBuilder();

        // llop through the basket array then find the matching id one
        for (int bb = 0; bb < basketsArray.size(); bb++) {
            JsonObject aBasket = basketsArray.getJsonObject(bb);

            // find matching basket then update ingredients
            if (basketId.equals(aBasket.getString("id"))) {

                JsonArrayBuilder ingredArrayBuilder = Json.createArrayBuilder();

                // convert ingredients list to json array
                // trim whitespaces whatever capitailzse
                for (String ingredient : ingredients) {
                    // capitalize the first letter
                    ingredient = ingredient.toLowerCase().trim();
                    ingredient = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1);
                    ingredArrayBuilder.add(ingredient);
                }

                JsonArray ingredArray = ingredArrayBuilder.build();

                // replace the ingredient keyvalue array in the basket
                // update the name also
                // same hashkey, the valyes will just u[date override]
                JsonObject updatedBasket = Json.createObjectBuilder(aBasket)
                        .add("ingredients", ingredArray)
                        .add("basketName", basketName)
                        .build();
                
                updatedBasketsBuilder.add(updatedBasket);
            } else {
                // if not the same id, just add the basket as it is
                updatedBasketsBuilder.add(aBasket);
            }
        }

        // Build the array now
        JsonArray updatedBasketsBuilt = updatedBasketsBuilder.build();

        // Create the JSON Object full again.
        JsonObject update = Json.createObjectBuilder()
                .add("baskets", updatedBasketsBuilt).build();

        mp.update(RedisKeys.ccwContainers, user, update.toString());

    }

}