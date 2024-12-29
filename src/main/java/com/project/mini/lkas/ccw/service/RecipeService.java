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

    public List<Recipe> retrieveSavedRecipes(String currentUser) {

        List<Recipe> results = new ArrayList<>();

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
        JsonArray savedRecipes = jo.getJsonArray("saved_recipes");

        for (int i = savedRecipes.size() - 1; i >= 0; i--) {

            JsonObject recipeObject = savedRecipes.getJsonObject(i);

            Recipe recipe = new Recipe();
            recipe.setIdMeal(recipeObject.getString("idMeal"));
            recipe.setStrMeal(recipeObject.getString("strMeal"));
            recipe.setStrCategory(recipeObject.getString("strCategory"));
            recipe.setStrArea(recipeObject.getString("strArea"));
            recipe.setStrInstructions(recipeObject.getString("strInstructions"));
            recipe.setStrMealThumb(recipeObject.getString("strMealThumb"));
            recipe.setStrYoutube(recipeObject.getString("strYoutube"));
            recipe.setStrIngredient1(recipeObject.getString("strIngredient1"));
            recipe.setStrIngredient2(recipeObject.getString("strIngredient2"));
            recipe.setStrIngredient3(recipeObject.getString("strIngredient3"));
            recipe.setStrIngredient4(recipeObject.getString("strIngredient4"));
            recipe.setStrIngredient5(recipeObject.getString("strIngredient5"));
            recipe.setStrIngredient6(recipeObject.getString("strIngredient6"));
            recipe.setStrIngredient7(recipeObject.getString("strIngredient7"));
            recipe.setStrIngredient8(recipeObject.getString("strIngredient8"));
            recipe.setStrIngredient9(recipeObject.getString("strIngredient9"));
            recipe.setStrIngredient10(recipeObject.getString("strIngredient10"));
            recipe.setStrIngredient11(recipeObject.getString("strIngredient11"));
            recipe.setStrIngredient12(recipeObject.getString("strIngredient12"));
            recipe.setStrIngredient13(recipeObject.getString("strIngredient13"));
            recipe.setStrIngredient14(recipeObject.getString("strIngredient14"));
            recipe.setStrIngredient15(recipeObject.getString("strIngredient15"));
            recipe.setStrIngredient16(recipeObject.getString("strIngredient16"));
            recipe.setStrIngredient17(recipeObject.getString("strIngredient17"));
            recipe.setStrIngredient18(recipeObject.getString("strIngredient18"));
            recipe.setStrIngredient19(recipeObject.getString("strIngredient19"));
            recipe.setStrIngredient20(recipeObject.getString("strIngredient20"));
            recipe.setStrMeasure1(recipeObject.getString("strMeasure1"));
            recipe.setStrMeasure2(recipeObject.getString("strMeasure2"));
            recipe.setStrMeasure3(recipeObject.getString("strMeasure3"));
            recipe.setStrMeasure4(recipeObject.getString("strMeasure4"));
            recipe.setStrMeasure5(recipeObject.getString("strMeasure5"));
            recipe.setStrMeasure6(recipeObject.getString("strMeasure6"));
            recipe.setStrMeasure7(recipeObject.getString("strMeasure7"));
            recipe.setStrMeasure8(recipeObject.getString("strMeasure8"));
            recipe.setStrMeasure9(recipeObject.getString("strMeasure9"));
            recipe.setStrMeasure10(recipeObject.getString("strMeasure10"));
            recipe.setStrMeasure11(recipeObject.getString("strMeasure11"));
            recipe.setStrMeasure12(recipeObject.getString("strMeasure12"));
            recipe.setStrMeasure13(recipeObject.getString("strMeasure13"));
            recipe.setStrMeasure14(recipeObject.getString("strMeasure14"));
            recipe.setStrMeasure15(recipeObject.getString("strMeasure15"));
            recipe.setStrMeasure16(recipeObject.getString("strMeasure16"));
            recipe.setStrMeasure17(recipeObject.getString("strMeasure17"));
            recipe.setStrMeasure18(recipeObject.getString("strMeasure18"));
            recipe.setStrMeasure19(recipeObject.getString("strMeasure19"));
            recipe.setStrMeasure20(recipeObject.getString("strMeasure20"));

            results.add(recipe);
        }

        return results;
    }

    public void saveRecipe(String user, Recipe recipe) {

        // Check if the user got entries saved in the ccwSavedRecipes
        Boolean ifExists = mp.hashKeyExists(RedisKeys.ccwSavedRecipes, user);

        if (ifExists) {
            String existingSavedRecipes = mp.get(RedisKeys.ccwSavedRecipes, user);

            JsonReader jr = Json.createReader(new StringReader(existingSavedRecipes));
            JsonObject savedRecords = jr.readObject();

            JsonArray savedArray = savedRecords.getJsonArray("saved_recipes");

            // Check for duplicates
            boolean exists = savedArray.stream()
                    .map(JsonValue::asJsonObject)
                    .anyMatch(obj -> obj.getString("idMeal").equals(recipe.getIdMeal()));

            if (!exists) {
                JsonObject newSavedRecipe = Json.createObjectBuilder()
                        .add("idMeal", recipe.getIdMeal())
                        .add("strMeal", recipe.getStrMeal())
                        .add("strCategory", recipe.getStrCategory())
                        .add("strArea", recipe.getStrArea())
                        .add("strInstructions", recipe.getStrInstructions())
                        .add("strMealThumb", recipe.getStrMealThumb())
                        .add("strYoutube", recipe.getStrYoutube())
                        .add("strIngredient1", recipe.getStrIngredient1())
                        .add("strIngredient2", recipe.getStrIngredient2())
                        .add("strIngredient3", recipe.getStrIngredient3())
                        .add("strIngredient4", recipe.getStrIngredient4())
                        .add("strIngredient5", recipe.getStrIngredient5())
                        .add("strIngredient6", recipe.getStrIngredient6())
                        .add("strIngredient7", recipe.getStrIngredient7())
                        .add("strIngredient8", recipe.getStrIngredient8())
                        .add("strIngredient9", recipe.getStrIngredient9())
                        .add("strIngredient10", recipe.getStrIngredient10())
                        .add("strIngredient11", recipe.getStrIngredient11())
                        .add("strIngredient12", recipe.getStrIngredient12())
                        .add("strIngredient13", recipe.getStrIngredient13())
                        .add("strIngredient14", recipe.getStrIngredient14())
                        .add("strIngredient15", recipe.getStrIngredient15())
                        .add("strIngredient16", recipe.getStrIngredient16())
                        .add("strIngredient17", recipe.getStrIngredient17())
                        .add("strIngredient18", recipe.getStrIngredient18())
                        .add("strIngredient19", recipe.getStrIngredient19())
                        .add("strIngredient20", recipe.getStrIngredient20())
                        .add("strMeasure1", recipe.getStrMeasure1())
                        .add("strMeasure2", recipe.getStrMeasure2())
                        .add("strMeasure3", recipe.getStrMeasure3())
                        .add("strMeasure4", recipe.getStrMeasure4())
                        .add("strMeasure5", recipe.getStrMeasure5())
                        .add("strMeasure6", recipe.getStrMeasure6())
                        .add("strMeasure7", recipe.getStrMeasure7())
                        .add("strMeasure8", recipe.getStrMeasure8())
                        .add("strMeasure9", recipe.getStrMeasure9())
                        .add("strMeasure10", recipe.getStrMeasure10())
                        .add("strMeasure11", recipe.getStrMeasure11())
                        .add("strMeasure12", recipe.getStrMeasure12())
                        .add("strMeasure13", recipe.getStrMeasure13())
                        .add("strMeasure14", recipe.getStrMeasure14())
                        .add("strMeasure15", recipe.getStrMeasure15())
                        .add("strMeasure16", recipe.getStrMeasure16())
                        .add("strMeasure17", recipe.getStrMeasure17())
                        .add("strMeasure18", recipe.getStrMeasure18())
                        .add("strMeasure19", recipe.getStrMeasure19())
                        .add("strMeasure20", recipe.getStrMeasure20())
                        .build();

                JsonArray updatedReviewArray = Json.createArrayBuilder(savedArray)
                        .add(newSavedRecipe)
                        .build();

                JsonObject updatedRecords = Json.createObjectBuilder(savedRecords)
                        .add("saved_recipes", updatedReviewArray)
                        .build();

                mp.update(RedisKeys.ccwSavedRecipes, user, updatedRecords.toString());
            }
        }

    }

    public void deleteSavedRecipe(String currentUser, String recipeId) {

        String existingSavedRecipes = mp.get(RedisKeys.ccwSavedRecipes, currentUser);

        JsonReader jr = Json.createReader(new StringReader(existingSavedRecipes));
        JsonObject savedRecords = jr.readObject();

        JsonArray savedArray = savedRecords.getJsonArray("saved_recipes");

        JsonArrayBuilder updatedArray = Json.createArrayBuilder();

        for (int i = 0; i < savedArray.size(); i++) {
            JsonObject recipe = savedArray.getJsonObject(i);

            if (!recipe.getString("idMeal").equals(recipeId)) {
                updatedArray.add(recipe);
            }
        }

        JsonObject updatedRecords = Json.createObjectBuilder(savedRecords)
                .add("saved_recipes", updatedArray.build())
                .build();

        mp.update(RedisKeys.ccwSavedRecipes, currentUser, updatedRecords.toString());

    }

    public Recipe getRecipeDetails(String recipeId) {

        // Take recipe ID, then call the API get details of the recipe
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

        if (!mp.hashKeyExists(RedisKeys.ccwContainers, user)) {
            return 0;
        }

        String existingBasketJson = mp.get(RedisKeys.ccwContainers, user);

        if (existingBasketJson != null) {
            JsonObject existingBasketRecords = Json.createReader(new StringReader(existingBasketJson)).readObject();
            JsonArray currentBasketArray = existingBasketRecords.getJsonArray("baskets");
            return currentBasketArray.size();
        }

        return 0;
    }

    public void saveRecipeById(String user, String recipeId) {

        // Retrieve recipe details first
        Recipe recipe = getRecipeDetails(recipeId);

        // Check if the user got entries saved in the ccwSavedRecipes
        Boolean ifExists = mp.hashKeyExists(RedisKeys.ccwSavedRecipes, user);

        if (ifExists) {
            // Existing saved recipes
            String existingSavedRecipes = mp.get(RedisKeys.ccwSavedRecipes, user);

            JsonReader jr = Json.createReader(new StringReader(existingSavedRecipes));
            JsonObject savedRecords = jr.readObject();

            JsonArray savedArray = savedRecords.getJsonArray("saved_recipes");

            // Check for duplicates
            boolean exists = savedArray.stream()
                    .map(JsonValue::asJsonObject)
                    .anyMatch(obj -> obj.getString("idMeal").equals(recipe.getIdMeal()));

            if (!exists) {
                // Create a new saved recipe JSON object
                JsonObject newSavedRecipe = Json.createObjectBuilder()
                        .add("idMeal", recipe.getIdMeal())
                        .add("strMeal", recipe.getStrMeal())
                        .add("strCategory", recipe.getStrCategory())
                        .add("strArea", recipe.getStrArea())
                        .add("strInstructions", recipe.getStrInstructions())
                        .add("strMealThumb", recipe.getStrMealThumb())
                        .add("strYoutube", recipe.getStrYoutube())
                        .add("strIngredient1", recipe.getStrIngredient1())
                        .add("strIngredient2", recipe.getStrIngredient2())
                        .add("strIngredient3", recipe.getStrIngredient3())
                        .add("strIngredient4", recipe.getStrIngredient4())
                        .add("strIngredient5", recipe.getStrIngredient5())
                        .add("strIngredient6", recipe.getStrIngredient6())
                        .add("strIngredient7", recipe.getStrIngredient7())
                        .add("strIngredient8", recipe.getStrIngredient8())
                        .add("strIngredient9", recipe.getStrIngredient9())
                        .add("strIngredient10", recipe.getStrIngredient10())
                        .add("strIngredient11", recipe.getStrIngredient11())
                        .add("strIngredient12", recipe.getStrIngredient12())
                        .add("strIngredient13", recipe.getStrIngredient13())
                        .add("strIngredient14", recipe.getStrIngredient14())
                        .add("strIngredient15", recipe.getStrIngredient15())
                        .add("strIngredient16", recipe.getStrIngredient16())
                        .add("strIngredient17", recipe.getStrIngredient17())
                        .add("strIngredient18", recipe.getStrIngredient18())
                        .add("strIngredient19", recipe.getStrIngredient19())
                        .add("strIngredient20", recipe.getStrIngredient20())
                        .add("strMeasure1", recipe.getStrMeasure1())
                        .add("strMeasure2", recipe.getStrMeasure2())
                        .add("strMeasure3", recipe.getStrMeasure3())
                        .add("strMeasure4", recipe.getStrMeasure4())
                        .add("strMeasure5", recipe.getStrMeasure5())
                        .add("strMeasure6", recipe.getStrMeasure6())
                        .add("strMeasure7", recipe.getStrMeasure7())
                        .add("strMeasure8", recipe.getStrMeasure8())
                        .add("strMeasure9", recipe.getStrMeasure9())
                        .add("strMeasure10", recipe.getStrMeasure10())
                        .add("strMeasure11", recipe.getStrMeasure11())
                        .add("strMeasure12", recipe.getStrMeasure12())
                        .add("strMeasure13", recipe.getStrMeasure13())
                        .add("strMeasure14", recipe.getStrMeasure14())
                        .add("strMeasure15", recipe.getStrMeasure15())
                        .add("strMeasure16", recipe.getStrMeasure16())
                        .add("strMeasure17", recipe.getStrMeasure17())
                        .add("strMeasure18", recipe.getStrMeasure18())
                        .add("strMeasure19", recipe.getStrMeasure19())
                        .add("strMeasure20", recipe.getStrMeasure20())
                        .build();

                // Add the new entry
                JsonArray updatedReviewArray = Json.createArrayBuilder(savedArray)
                        .add(newSavedRecipe)
                        .build();

                // Update the JSON object
                JsonObject updatedRecords = Json.createObjectBuilder(savedRecords)
                        .add("saved_recipes", updatedReviewArray)
                        .build();

                mp.update(RedisKeys.ccwSavedRecipes, user, updatedRecords.toString());
            }
        }
    }
}
