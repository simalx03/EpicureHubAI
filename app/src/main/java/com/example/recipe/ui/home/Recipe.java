package com.example.recipe.ui.home;

public class Recipe {
    private String recipeName;
    private String recipeDescription;
    private String ingredients;
    private String instruction;
    private String serveSize;
    private String cookingTime;

    // Required empty constructor for Firebase
    public Recipe() {
    }

    public Recipe(String recipeName, String recipeDescription, String ingridients, String instruction, String serveSize, String cookingTime) {
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.ingredients = ingridients;
        this.instruction = instruction;
        this.serveSize = serveSize;
        this.cookingTime = cookingTime;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeDescription() {
        return recipeDescription;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instruction;
    }

    public String getServeSize() {
        return serveSize;
    }

    public String getCookingTime() {
        return cookingTime;
    }
}
