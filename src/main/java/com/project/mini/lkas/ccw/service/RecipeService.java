package com.project.mini.lkas.ccw.service;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.project.mini.lkas.ccw.constant.RedisKeys;
import com.project.mini.lkas.ccw.constant.Url;
import com.project.mini.lkas.ccw.model.Listing;
import com.project.mini.lkas.ccw.model.Recipe;
import com.project.mini.lkas.ccw.repository.MapRepo;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;

@Service
public class RecipeService {

    @Autowired
    private MapRepo mp;

    @Value("${mealdb.apikey}")
    private String LAWSONKEY;

    RestTemplate restTemplate = new RestTemplate();

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

    public List<Listing> retrieveSavedRecipes(String currentUser) {

        List<Listing> results = new ArrayList<>();

        // check if have existing saved recipes in redis
        // if not, return empty list else get the redis list and return controller
        Boolean existingSaved = mp.hashKeyExists(RedisKeys.ccwSavedRecipes, currentUser);

        if (existingSaved == false) {
            return results;

        }

        // take currentuser saved recipes id from redis
        // get the recipe details from external api using the id
        // instantiate listing object again then send it back to controller

        String savedRecipesJson = mp.get(RedisKeys.ccwSavedRecipes, currentUser);

        JsonReader jr = Json.createReader(new StringReader(savedRecipesJson));
        JsonObject jo = jr.readObject();
        JsonArray savedRecipes = jo.getJsonArray("recipe_id");

        for (int i = 0; i < savedRecipes.size(); i++) {
            String recipeId = savedRecipes.getString(i);

            // get recipe details from external api
            String appendedUrl1 = Url.searchByMealId.replace("{APIKEY}", LAWSONKEY);
            String appendedUrl2 = appendedUrl1.replace("{MEALID}", recipeId);

            String dataFromApi = restTemplate.getForObject(appendedUrl2, String.class);

            // Now reading the api meal object
            JsonReader jr2 = Json.createReader(new StringReader(dataFromApi));
            JsonObject jo2 = jr2.readObject();

            JsonArray meals = jo2.getJsonArray("meals");

            for (int x = 0; x < meals.size(); x++) {

                JsonObject meal = meals.getJsonObject(x);

                Listing list = new Listing();
                list.setStrMeal(meal.getString("strMeal"));
                list.setStrMealThumb(meal.getString("strMealThumb"));
                list.setIdMeal(meal.getString("idMeal"));

                results.add(list);
            }
        }

        return results;
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

            // make a copy to add the new saved recipe id in afterwards
            for (JsonValue value : savedArray) {
                updatedArray.add(value);
            }

            // Check for duplicates then add if dont have
            if (!savedArray.contains(Json.createValue(recipeId))) {
                updatedArray.add(recipeId);
            }

            JsonObject updatedRecords = Json.createObjectBuilder()
                    .add("recipe_id", updatedArray)
                    .build();

            mp.update(RedisKeys.ccwSavedRecipes, user, updatedRecords.toString());

        } else {

            // means firsst saved enrty, so create it
            JsonArrayBuilder jab = Json.createArrayBuilder();
            jab.add(recipeId);

            JsonObject savedRecords = Json.createObjectBuilder()
                    .add("recipe_id", jab)
                    .build();

            mp.create(RedisKeys.ccwSavedRecipes, user, savedRecords.toString());
        }
    }

    public void deleteSavedRecipe(String currentUser, String recipeId) {

        String existingSavedRecipes = mp.get(RedisKeys.ccwSavedRecipes, currentUser);

        JsonReader jr = Json.createReader(new StringReader(existingSavedRecipes));
        JsonObject savedRecords = jr.readObject();
        JsonArray savedArray = savedRecords.getJsonArray("recipe_id");

        JsonArrayBuilder updatedArray = Json.createArrayBuilder();

        // Add the ID that dont matches into updated array then replace it after
        for (JsonValue value : savedArray) {

            // cast to string to compare if not its cmparing "example" with example
            // the quiotation marks, so cannot work
            // replace the '"' with "" to remove the quotation marks
            String valueString = value.toString().replace("\"", "");

            if (!valueString.toString().equals(recipeId)) {
                System.out.println(value);
                updatedArray.add(value);
            }
        }

        System.out.println("Updated Array: " + updatedArray.toString());

        JsonObject updatedRecords = Json.createObjectBuilder()
                .add("recipe_id", updatedArray)
                .build();

        mp.update(RedisKeys.ccwSavedRecipes, currentUser, updatedRecords.toString());

    }

    public Recipe getRecipeDetails(String recipeId) {

        // Take recipe ID, then call the API get details of the recipe

        // System.out.println("IN REST SERVICE NOW
        // ------------------------------------------");
        String appendUrl1 = Url.searchByMealId.replace("{APIKEY}", LAWSONKEY);
        String appendUrl2 = appendUrl1.replace("{MEALID}", recipeId);

        // call the api now, take the json response map back to recipe object and return
        // to controller
        String jsonData = restTemplate.getForObject(appendUrl2, String.class);
        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();
        JsonArray meals = jo.getJsonArray("meals");

        // Start index 0, because only have one meal
        JsonObject mealdata = meals.getJsonObject(0);

        // Use helper method to check for null value thingy
        Recipe recipe = new Recipe(
                getJsonString(mealdata, "idMeal"),
                getJsonString(mealdata, "strMeal"),
                getJsonString(mealdata, "strCategory"),
                getJsonString(mealdata, "strArea"),
                getJsonString(mealdata, "strInstructions"),
                getJsonString(mealdata, "strMealThumb"),
                getJsonString(mealdata, "strYoutube"),
                getJsonString(mealdata, "strIngredient1"),
                getJsonString(mealdata, "strIngredient2"),
                getJsonString(mealdata, "strIngredient3"),
                getJsonString(mealdata, "strIngredient4"),
                getJsonString(mealdata, "strIngredient5"),
                getJsonString(mealdata, "strIngredient6"),
                getJsonString(mealdata, "strIngredient7"),
                getJsonString(mealdata, "strIngredient8"),
                getJsonString(mealdata, "strIngredient9"),
                getJsonString(mealdata, "strIngredient10"),
                getJsonString(mealdata, "strIngredient11"),
                getJsonString(mealdata, "strIngredient12"),
                getJsonString(mealdata, "strIngredient13"),
                getJsonString(mealdata, "strIngredient14"),
                getJsonString(mealdata, "strIngredient15"),
                getJsonString(mealdata, "strIngredient16"),
                getJsonString(mealdata, "strIngredient17"),
                getJsonString(mealdata, "strIngredient18"),
                getJsonString(mealdata, "strIngredient19"),
                getJsonString(mealdata, "strIngredient20"),
                getJsonString(mealdata, "strMeasure1"),
                getJsonString(mealdata, "strMeasure2"),
                getJsonString(mealdata, "strMeasure3"),
                getJsonString(mealdata, "strMeasure4"),
                getJsonString(mealdata, "strMeasure5"),
                getJsonString(mealdata, "strMeasure6"),
                getJsonString(mealdata, "strMeasure7"),
                getJsonString(mealdata, "strMeasure8"),
                getJsonString(mealdata, "strMeasure9"),
                getJsonString(mealdata, "strMeasure10"),
                getJsonString(mealdata, "strMeasure11"),
                getJsonString(mealdata, "strMeasure12"),
                getJsonString(mealdata, "strMeasure13"),
                getJsonString(mealdata, "strMeasure14"),
                getJsonString(mealdata, "strMeasure15"),
                getJsonString(mealdata, "strMeasure16"),
                getJsonString(mealdata, "strMeasure17"),
                getJsonString(mealdata, "strMeasure18"),
                getJsonString(mealdata, "strMeasure19"),
                getJsonString(mealdata, "strMeasure20"));

        return recipe;
    }

    // Helper method to check for null values in ingredients or the measurement
    // thingy
    private String getJsonString(JsonObject jsonObject, String key) {
        JsonValue value = jsonObject.get(key);
        if (value != null && value.getValueType() == JsonValue.ValueType.STRING) {
            return ((JsonString) value).getString();
        }
        return null; // Return null if key doesn't exist or isn't a string
    }

    public Recipe getRandomRecipe() {

        String appendUrl = Url.randomOne.replace("{APIKEY}", LAWSONKEY);

        String jsonData = restTemplate.getForObject(appendUrl, String.class);
        JsonReader jr = Json.createReader(new StringReader(jsonData));
        JsonObject jo = jr.readObject();
        JsonArray meals = jo.getJsonArray("meals");

        // Start index 0, because only have one meal
        JsonObject mealdata = meals.getJsonObject(0);

        // Use helper method to check for null value thingy
        Recipe recipe = new Recipe(
                getJsonString(mealdata, "idMeal"),
                getJsonString(mealdata, "strMeal"),
                getJsonString(mealdata, "strCategory"),
                getJsonString(mealdata, "strArea"),
                getJsonString(mealdata, "strInstructions"),
                getJsonString(mealdata, "strMealThumb"),
                getJsonString(mealdata, "strYoutube"),
                getJsonString(mealdata, "strIngredient1"),
                getJsonString(mealdata, "strIngredient2"),
                getJsonString(mealdata, "strIngredient3"),
                getJsonString(mealdata, "strIngredient4"),
                getJsonString(mealdata, "strIngredient5"),
                getJsonString(mealdata, "strIngredient6"),
                getJsonString(mealdata, "strIngredient7"),
                getJsonString(mealdata, "strIngredient8"),
                getJsonString(mealdata, "strIngredient9"),
                getJsonString(mealdata, "strIngredient10"),
                getJsonString(mealdata, "strIngredient11"),
                getJsonString(mealdata, "strIngredient12"),
                getJsonString(mealdata, "strIngredient13"),
                getJsonString(mealdata, "strIngredient14"),
                getJsonString(mealdata, "strIngredient15"),
                getJsonString(mealdata, "strIngredient16"),
                getJsonString(mealdata, "strIngredient17"),
                getJsonString(mealdata, "strIngredient18"),
                getJsonString(mealdata, "strIngredient19"),
                getJsonString(mealdata, "strIngredient20"),
                getJsonString(mealdata, "strMeasure1"),
                getJsonString(mealdata, "strMeasure2"),
                getJsonString(mealdata, "strMeasure3"),
                getJsonString(mealdata, "strMeasure4"),
                getJsonString(mealdata, "strMeasure5"),
                getJsonString(mealdata, "strMeasure6"),
                getJsonString(mealdata, "strMeasure7"),
                getJsonString(mealdata, "strMeasure8"),
                getJsonString(mealdata, "strMeasure9"),
                getJsonString(mealdata, "strMeasure10"),
                getJsonString(mealdata, "strMeasure11"),
                getJsonString(mealdata, "strMeasure12"),
                getJsonString(mealdata, "strMeasure13"),
                getJsonString(mealdata, "strMeasure14"),
                getJsonString(mealdata, "strMeasure15"),
                getJsonString(mealdata, "strMeasure16"),
                getJsonString(mealdata, "strMeasure17"),
                getJsonString(mealdata, "strMeasure18"),
                getJsonString(mealdata, "strMeasure19"),
                getJsonString(mealdata, "strMeasure20"));

        return recipe;
    }

    public Recipe recipeOfTheDay() {
        // reuse existing method
        // get on random recipe and save to redis
        // check for time if 0000 then refresh the recipe in redis bam

        // check if recipe of the day exists
        // if yes, return the recipe
        // if no, get random recipe and save to redis

        // check if exists firrst
        if (mp.hashKeyExists(RedisKeys.ccwRecipeOfTheDay, "recipe")) {
            // continue
            String existingRecipeJson = mp.get(RedisKeys.ccwRecipeOfTheDay, "recipe");
            JsonReader jr = Json.createReader(new StringReader(existingRecipeJson));
            JsonObject existingRecipe = jr.readObject();

            String retrievedDate = existingRecipe.getString("refreshDate");

            String currentDate = LocalDate.now().toString();

            // Compare the two dates, if different then refresh the recipe
            if (!retrievedDate.equals(currentDate)) {

                // get random recipe FIRST
                Recipe randomRecipe = getRandomRecipe();

                // create json object
                JsonObject recipeJson = Json.createObjectBuilder()
                        .add("refreshDate", LocalDate.now().toString())
                        .add("idMeal", randomRecipe.getIdMeal())
                        .add("strMeal", randomRecipe.getStrMeal())
                        .add("strCategory", randomRecipe.getStrCategory())
                        .add("strArea", randomRecipe.getStrArea())
                        .add("strInstructions", randomRecipe.getStrInstructions())
                        .add("strMealThumb", randomRecipe.getStrMealThumb())
                        .add("strYoutube", randomRecipe.getStrYoutube())
                        .add("strIngredient1", randomRecipe.getStrIngredient1())
                        .add("strIngredient2", randomRecipe.getStrIngredient2())
                        .add("strIngredient3", randomRecipe.getStrIngredient3())
                        .add("strIngredient4", randomRecipe.getStrIngredient4())
                        .add("strIngredient5", randomRecipe.getStrIngredient5())
                        .add("strIngredient6", randomRecipe.getStrIngredient6())
                        .add("strIngredient7", randomRecipe.getStrIngredient7())
                        .add("strIngredient8", randomRecipe.getStrIngredient8())
                        .add("strIngredient9", randomRecipe.getStrIngredient9())
                        .add("strIngredient10", randomRecipe.getStrIngredient10())
                        .add("strIngredient11", randomRecipe.getStrIngredient11())
                        .add("strIngredient12", randomRecipe.getStrIngredient12())
                        .add("strIngredient13", randomRecipe.getStrIngredient13())
                        .add("strIngredient14", randomRecipe.getStrIngredient14())
                        .add("strIngredient15", randomRecipe.getStrIngredient15())
                        .add("strIngredient16", randomRecipe.getStrIngredient16())
                        .add("strIngredient17", randomRecipe.getStrIngredient17())
                        .add("strIngredient18", randomRecipe.getStrIngredient18())
                        .add("strIngredient19", randomRecipe.getStrIngredient19())
                        .add("strIngredient20", randomRecipe.getStrIngredient20())
                        .add("strMeasure1", randomRecipe.getStrMeasure1())
                        .add("strMeasure2", randomRecipe.getStrMeasure2())
                        .add("strMeasure3", randomRecipe.getStrMeasure3())
                        .add("strMeasure4", randomRecipe.getStrMeasure4())
                        .add("strMeasure5", randomRecipe.getStrMeasure5())
                        .add("strMeasure6", randomRecipe.getStrMeasure6())
                        .add("strMeasure7", randomRecipe.getStrMeasure7())
                        .add("strMeasure8", randomRecipe.getStrMeasure8())
                        .add("strMeasure9", randomRecipe.getStrMeasure9())
                        .add("strMeasure10", randomRecipe.getStrMeasure10())
                        .add("strMeasure11", randomRecipe.getStrMeasure11())
                        .add("strMeasure12", randomRecipe.getStrMeasure12())
                        .add("strMeasure13", randomRecipe.getStrMeasure13())
                        .add("strMeasure14", randomRecipe.getStrMeasure14())
                        .add("strMeasure15", randomRecipe.getStrMeasure15())
                        .add("strMeasure16", randomRecipe.getStrMeasure16())
                        .add("strMeasure17", randomRecipe.getStrMeasure17())
                        .add("strMeasure18", randomRecipe.getStrMeasure18())
                        .add("strMeasure19", randomRecipe.getStrMeasure19())
                        .add("strMeasure20", randomRecipe.getStrMeasure20())
                        .build();

                // save to redis
                mp.create(RedisKeys.ccwRecipeOfTheDay, "recipe", recipeJson.toString());
            } else {
                // if same date then return current from redis
                return new Recipe(
                        existingRecipe.getString("idMeal"),
                        existingRecipe.getString("strMeal"),
                        existingRecipe.getString("strCategory"),
                        existingRecipe.getString("strArea"),
                        existingRecipe.getString("strInstructions"),
                        existingRecipe.getString("strMealThumb"),
                        existingRecipe.getString("strYoutube"),
                        existingRecipe.getString("strIngredient1"),
                        existingRecipe.getString("strIngredient2"),
                        existingRecipe.getString("strIngredient3"),
                        existingRecipe.getString("strIngredient4"),
                        existingRecipe.getString("strIngredient5"),
                        existingRecipe.getString("strIngredient6"),
                        existingRecipe.getString("strIngredient7"),
                        existingRecipe.getString("strIngredient8"),
                        existingRecipe.getString("strIngredient9"),
                        existingRecipe.getString("strIngredient10"),
                        existingRecipe.getString("strIngredient11"),
                        existingRecipe.getString("strIngredient12"),
                        existingRecipe.getString("strIngredient13"),
                        existingRecipe.getString("strIngredient14"),
                        existingRecipe.getString("strIngredient15"),
                        existingRecipe.getString("strIngredient16"),
                        existingRecipe.getString("strIngredient17"),
                        existingRecipe.getString("strIngredient18"),
                        existingRecipe.getString("strIngredient19"),
                        existingRecipe.getString("strIngredient20"),
                        existingRecipe.getString("strMeasure1"),
                        existingRecipe.getString("strMeasure2"),
                        existingRecipe.getString("strMeasure3"),
                        existingRecipe.getString("strMeasure4"),
                        existingRecipe.getString("strMeasure5"),
                        existingRecipe.getString("strMeasure6"),
                        existingRecipe.getString("strMeasure7"),
                        existingRecipe.getString("strMeasure8"),
                        existingRecipe.getString("strMeasure9"),
                        existingRecipe.getString("strMeasure10"),
                        existingRecipe.getString("strMeasure11"),
                        existingRecipe.getString("strMeasure12"),
                        existingRecipe.getString("strMeasure13"),
                        existingRecipe.getString("strMeasure14"),
                        existingRecipe.getString("strMeasure15"),
                        existingRecipe.getString("strMeasure16"),
                        existingRecipe.getString("strMeasure17"),
                        existingRecipe.getString("strMeasure18"),
                        existingRecipe.getString("strMeasure19"),
                        existingRecipe.getString("strMeasure20"));
            }
        }
        // get random recipe FIRST
        Recipe randomRecipe = getRandomRecipe();

        // create json object
        JsonObject recipeJson = Json.createObjectBuilder()
                .add("refreshDate", LocalDate.now().toString())
                .add("idMeal", randomRecipe.getIdMeal())
                .add("strMeal", randomRecipe.getStrMeal())
                .add("strCategory", randomRecipe.getStrCategory())
                .add("strArea", randomRecipe.getStrArea())
                .add("strInstructions", randomRecipe.getStrInstructions())
                .add("strMealThumb", randomRecipe.getStrMealThumb())
                .add("strYoutube", randomRecipe.getStrYoutube())
                .add("strIngredient1", randomRecipe.getStrIngredient1())
                .add("strIngredient2", randomRecipe.getStrIngredient2())
                .add("strIngredient3", randomRecipe.getStrIngredient3())
                .add("strIngredient4", randomRecipe.getStrIngredient4())
                .add("strIngredient5", randomRecipe.getStrIngredient5())
                .add("strIngredient6", randomRecipe.getStrIngredient6())
                .add("strIngredient7", randomRecipe.getStrIngredient7())
                .add("strIngredient8", randomRecipe.getStrIngredient8())
                .add("strIngredient9", randomRecipe.getStrIngredient9())
                .add("strIngredient10", randomRecipe.getStrIngredient10())
                .add("strIngredient11", randomRecipe.getStrIngredient11())
                .add("strIngredient12", randomRecipe.getStrIngredient12())
                .add("strIngredient13", randomRecipe.getStrIngredient13())
                .add("strIngredient14", randomRecipe.getStrIngredient14())
                .add("strIngredient15", randomRecipe.getStrIngredient15())
                .add("strIngredient16", randomRecipe.getStrIngredient16())
                .add("strIngredient17", randomRecipe.getStrIngredient17())
                .add("strIngredient18", randomRecipe.getStrIngredient18())
                .add("strIngredient19", randomRecipe.getStrIngredient19())
                .add("strIngredient20", randomRecipe.getStrIngredient20())
                .add("strMeasure1", randomRecipe.getStrMeasure1())
                .add("strMeasure2", randomRecipe.getStrMeasure2())
                .add("strMeasure3", randomRecipe.getStrMeasure3())
                .add("strMeasure4", randomRecipe.getStrMeasure4())
                .add("strMeasure5", randomRecipe.getStrMeasure5())
                .add("strMeasure6", randomRecipe.getStrMeasure6())
                .add("strMeasure7", randomRecipe.getStrMeasure7())
                .add("strMeasure8", randomRecipe.getStrMeasure8())
                .add("strMeasure9", randomRecipe.getStrMeasure9())
                .add("strMeasure10", randomRecipe.getStrMeasure10())
                .add("strMeasure11", randomRecipe.getStrMeasure11())
                .add("strMeasure12", randomRecipe.getStrMeasure12())
                .add("strMeasure13", randomRecipe.getStrMeasure13())
                .add("strMeasure14", randomRecipe.getStrMeasure14())
                .add("strMeasure15", randomRecipe.getStrMeasure15())
                .add("strMeasure16", randomRecipe.getStrMeasure16())
                .add("strMeasure17", randomRecipe.getStrMeasure17())
                .add("strMeasure18", randomRecipe.getStrMeasure18())
                .add("strMeasure19", randomRecipe.getStrMeasure19())
                .add("strMeasure20", randomRecipe.getStrMeasure20())
                .build();

        // save to redis
        mp.create(RedisKeys.ccwRecipeOfTheDay, "recipe", recipeJson.toString());

        return randomRecipe;

    }

    public int getBasketCount(String user) {
        // check if baskets exists
        // if yes, return the count
        // if no, return 0

        String existingBasketJson = mp.get(RedisKeys.ccwContainers, user);

        if (existingBasketJson != null) {
            JsonObject existingBasketRecords = Json.createReader(new StringReader(existingBasketJson)).readObject();
            JsonArray currentBasketArray = existingBasketRecords.getJsonArray("baskets");
            return currentBasketArray.size();
        }

        return 0;
    }

}
