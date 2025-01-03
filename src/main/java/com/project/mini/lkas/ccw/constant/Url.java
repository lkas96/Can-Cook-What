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
    public static final String getAllIngredients = "https://www.themealdb.com/api/json/v2/1/list.php?i=list";

    // Get one random recipe
    // www.themealdb.com/api/json/v1/1/random.php
    public static final String randomOne = "https://www.themealdb.com/api/json/v2/{APIKEY}/random.php";

    // Get 10 rfandom recipes
    // www.themealdb.com/api/json/v1/1/randomselection.php
    public static final String randomTen = "https://www.themealdb.com/api/json/v2/{APIKEY}/randomselection.php";

    // Search reicpe - user input search string whatever
    // www.themealdb.com/api/json/v1/1/search.php?s=Arrabiata
    public static final String searchRecipe = "https://www.themealdb.com/api/json/v2/{APIKEY}/search.php?s={SEARCHSTRING}";

    // newly added recipes to mealdb api
    // www.themealdb.com/api/json/v1/1/latest.php
    public static final String latestRecipes = "https://www.themealdb.com/api/json/v2/{APIKEY}/latest.php";

    // ingredient lookup recipe
    // www.themealdb.com/api/json/v2/9973533/filter.php?i=water
    public static final String ingredientLookup = "https://www.themealdb.com/api/json/v2/{APIKEY}/filter.php?i={INGREDIENT}";

    // search recipes by letter
    // https://www.themealdb.com/api/json/v2/1/filter.php?i=a
    public static final String searchByLetter = "https://www.themealdb.com/api/json/v2/{APIKEY}/search.php?f={LETTER}";

    // BLOGGER POST URL
    // www.googleapis.com/blogger/v3/blogs/8070105920543249955/posts/
    public static final String postToBlogger = "https://www.googleapis.com/blogger/v3/blogs/{BLOGID}/posts/";

    // BLOGGER REFERSH OAUTH2 TOKEN
    // https://oauth2.googleapis.com/token?
    public static final String refreshAuthCode = "https://oauth2.googleapis.com/token";

    // GET ALL BLOGGER POSTS
    // https://www.googleapis.com/blogger/v3/blogs/blogid/posts?key=APIKEY&maxResults=ANYNUMBER
    public static final String getAllPosts = "https://www.googleapis.com/blogger/v3/blogs/{BLOGID}/posts?key={APIKEY}&maxResults={HOWMANYPOSTS}";

    // Blogger delete a post
    // https://www.googleapis.com/blogger/v3/blogs/blogId/posts/postId
    public static final String deletePost = "https://www.googleapis.com/blogger/v3/blogs/{BLOGID}/posts/{POSTID}";

    // blogger patch to update a post just content body
    ///www.googleapis.com/blogger/v3/blogs/8070105920543249955/posts/3445355871727114160
    public static final String updatePost = "https://www.googleapis.com/blogger/v3/blogs/{BLOGID}/posts/{POSTID}?key={APIKEY}";

    // blogger self link - blogger id
    // www.googleapis.com/blogger/v3/blogs/8070105920543249955/posts/3445355871727114160"
    public static final String selfLink = "https://www.googleapis.com/blogger/v3/blogs/{BLOGID}}/posts/{POSTID}}";
}
