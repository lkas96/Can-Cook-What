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

    // Get one random recipe
    // www.themealdb.com/api/json/v1/1/random.php
    public static final String randomOne = "https://www.themealdb.com/api/json/v2/{APIKEY}/random.php";

    //Get 10 rfandom recipes
    // www.themealdb.com/api/json/v1/1/randomselection.php
    public static final String randomTen = "https://www.themealdb.com/api/json/v2/{APIKEY}/randomselection.php";

    //Search reicpe - user input search string whatever
    // www.themealdb.com/api/json/v1/1/search.php?s=Arrabiata
    public static final String searchRecipe = "https://www.themealdb.com/api/json/v2/{APIKEY}/search.php?s={SEARCHSTRING}";

    //BLOGGER POST URL
    //www.googleapis.com/blogger/v3/blogs/8070105920543249955/posts/
    public static final String postToBlogger = "https://www.googleapis.com/blogger/v3/blogs/{BLOGID}/posts/";

    //BLOGGER REFERSH OAUTH2 TOKEN
    //https://oauth2.googleapis.com/token?
    public static final String refreshAuthCode = "https://oauth2.googleapis.com/token";
}
