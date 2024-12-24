package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.model.Recipe;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Service
public class RecipeService {

    @Autowired
    private MapRepo mp;

    public String retrieveIngredientString(String user, String basketId) {

        // Access currentUser's basket and get the ingredients
        // Convert the ingredients to string, ready for use in the URL
        String existingBasketJson = mp.get(RedisKeys.ccwContainers, user);

        JsonObject existingBasketRecords = Json.createReader(new StringReader(existingBasketJson)).readObject();

        JsonArray currentBasketArray = existingBasketRecords.getJsonArray("baskets");

        for (int i = 0; i < currentBasketArray.size(); i++) {

            JsonObject basket = currentBasketArray.getJsonObject(i);

            if (basket.getString("id").equals(basketId)) {

                JsonArray ingredients = basket.getJsonArray("ingredients");

                // Convert the ingredients to string, ready for use in the URL
                StringBuilder sb = new StringBuilder();

                // Piece string array into a single string, comma seperated
                for (int j = 0; j < ingredients.size(); j++) {
                    sb.append(ingredients.getString(j).trim()); // trimm remove whitespace
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

    public List<String> retrieveIngredientMeasurementPair(Recipe recipe)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {

        // Take recipe, pair the ingredients/measurements
        // ignore "" and NULL values
        // return a list of strings

        List<String> pairs = new ArrayList<>();

        String ingredientString = "StrIngredient";
        String measurementString = "StrMeasure";

        for (int i = 1; i < 21; i++) {
            // Check if null or empty
            // If ingredient is null/empty, measurement will be too
            // only do one check.

            String tempIngredient = ingredientString + i;
            String tempMeasurement = measurementString + i;

            // Custom getter method
            // Loop through the get methods because there are 20 ingredients and
            // measurements
            Method getIngredientMethod = recipe.getClass().getMethod("get" + tempIngredient);
            Method getMeasurementMethod = recipe.getClass().getMethod("get" + tempMeasurement);

            String ingredient = (String) getIngredientMethod.invoke(recipe);
            String measurement = (String) getMeasurementMethod.invoke(recipe);

            if (ingredient != null && !ingredient.isEmpty()) {
                pairs.add(ingredient + " , " + measurement);
            }
        }

        return pairs;

    }

    public List<String> retrieveIngredientList(Recipe recipe)
            throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {

        List<String> list = new ArrayList<>();

        String ingredientString = "StrIngredient";

        for (int i = 1; i < 21; i++) {
            String tempIngredient = ingredientString + i;

            // Custom getter method
            // Loop through the get methods because there are 20 ingredients and
            // measurements
            Method getIngredientMethod = recipe.getClass().getMethod("get" + tempIngredient);

            String ingredient = (String) getIngredientMethod.invoke(recipe);

            if (ingredient != null && !ingredient.isEmpty()) {
                list.add(ingredient);
            }
        }

        return list;
    }

    public void saveRecipe(String user, String recipeId) {

        // Check if the user got entries saved in the ccwSavedRecipes
        Boolean ifExists = mp.hashKeyExists(RedisKeys.ccwSavedRecipes, user);
    
        if (ifExists == true) {
            // Means have existing basket, so append the entry
            String existingSavedRecipes = mp.get(RedisKeys.ccwSavedRecipes, user);

            JsonReader jr = Json.createReader(new StringReader(existingSavedRecipes));
            JsonObject savedRecords = jr.readObject();
            JsonArray savedArray = savedRecords.getJsonArray("recipe_id");
            
            JsonArrayBuilder updatedArray = Json.createArrayBuilder();
    
            //make a copy to add the new saved recipe id in afterwards
            for (JsonValue value : savedArray) {
                updatedArray.add(value);
            }

            //Check for duplicates then add if dont have
            if (!savedArray.contains(Json.createValue(recipeId))) {
                updatedArray.add(recipeId);
            }

            JsonObject updatedRecords = Json.createObjectBuilder()
                                            .add("recipe_id", updatedArray)
                                            .build();

            mp.update(RedisKeys.ccwSavedRecipes, user, updatedRecords.toString());

        } else {

            //means firsst saved enrty, so create it
            JsonArrayBuilder jab = Json.createArrayBuilder();
            jab.add(recipeId);
    
            JsonObject savedRecords = Json.createObjectBuilder()
                                          .add("recipe_id", jab)
                                          .build();
    
            mp.create(RedisKeys.ccwSavedRecipes, user, savedRecords.toString());
        }
    }
    

}
