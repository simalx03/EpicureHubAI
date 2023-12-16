package com.example.recipe.ui.home;

public class Recipe {
    private String recipeName;
    private String recipeDescription;

    // Required empty constructor for Firebase
    public Recipe() {
    }

    public Recipe(String recipeName, String recipeDescription) {
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }
}
