package com.project.mini.lkas.ccw.constant;

public class Url {

    // Lookup full meal details by id
    // www.themealdb.com/api/json/v1/1/lookup.php?i=52772
    public static final String searchByMealId = "https://www.themealdb.com/api/json/v2/{APIKEY}/lookup.php?i={MEALID}";

    // Filter by multi-ingredient *Premium API Only
    // www.themealdb.com/api/json/v1/1/filter.php?i=chicken_breast,garlic,salt
    public static final String searchByIngredients = "https://www.themealdb.com/api/json/v2/{APIKEY}/filter.php?i={INGREDIENTS}";

    // List all supported ingredients on the mealdb api db
    // www.themealdb.com/api/json/v1/1/list.php?i=list
    public static final String showAllIngredients = "https://www.themealdb.com/api/json/v2/{APIKEY}/list.php?i=list";
}
