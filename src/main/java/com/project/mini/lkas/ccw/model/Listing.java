package com.project.mini.lkas.ccw.model;

public class Listing {
    private String strMeal;
    private String strMealThumb;
    private String idMeal;
    public String getStrMeal() {
        return strMeal;
    }
    public void setStrMeal(String strMeal) {
        this.strMeal = strMeal;
    }
    public String getStrMealThumb() {
        return strMealThumb;
    }
    public void setStrMealThumb(String strMealThumb) {
        this.strMealThumb = strMealThumb;
    }
    public String getIdMeal() {
        return idMeal;
    }
    public void setIdMeal(String idMeal) {
        this.idMeal = idMeal;
    }
    public Listing(String strMeal, String strMealThumb, String idMeal) {
        this.strMeal = strMeal;
        this.strMealThumb = strMealThumb;
        this.idMeal = idMeal;
    }
    public Listing(){
        
    }
}
