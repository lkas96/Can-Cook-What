# Can-Cook-What

# Powered by TheMealDB API & Blogger API
https://www.themealdb.com/api.php
https://developers.google.com/blogger

# Limitations
Ingredients are limited to mealdb database. Fortunately there are over 570 ingredients and 300 recipes available.
Basket Creation and Search might be limited to the ingredient pool. 
There are validation for accepted ingredients during basket creation.
Recipe calls for 8 ingredients, if you add the same 8 ingredients into a basket, API have issues searching for that same exact recipe too. Known API limitation. Try to limit the ingredients in basket to 2-4 for best results. 

    For Example
    Potato is not accepted but potatoes are accepted. 
    Carrot is not accepted but carrots are accepted.
    Bay Leaf and Bay Leaves are both accepted.
    Please check out the ingredients db or play around with the ingredients.

# Relative Small Ingredients and Recipe Database
Total Recipes: 303 
Total Ingredients: 575
As of 29 December 2024

# Features
1. View & Manage Basket
2. Save & Manage Recipes
3. Browse & Search Recipes (By dish name or recipe ID)
4. Generate 1/10 random recipes for surprise or nspiration.
5. Review and View other's reviews both on CanCookWhat/External Blog.
     Reviews are also posted to an external blog page. 

# API Calls
    /api/users -> Returns JSON of user name+email from redis db
    /api/reviews -> Returns all blog reviews from blogger API
    /api/ingredients -> Returns all ingredients from mealdb API
