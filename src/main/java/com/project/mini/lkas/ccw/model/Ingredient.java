package com.project.mini.lkas.ccw.model;

public class Ingredient {

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Ingredient(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Ingredient() {
    }

    @Override
    public String toString() {
        return "Ingredients [id=" + id + ", name=" + name + "]";
    }

}
