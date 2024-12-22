package com.project.mini.lkas.ccw.model;

import java.util.List;
import java.util.UUID;

public class Basket {

    private String name;

    private List<String> ingredients;

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Basket(String name, List<String> ingredients) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.ingredients = ingredients;
    }

    public Basket() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public String toString() {
        return name + "," + ingredients;
    }
}
