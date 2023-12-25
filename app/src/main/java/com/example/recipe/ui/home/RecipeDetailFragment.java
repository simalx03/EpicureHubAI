package com.example.recipe.ui.home;

import com.example.recipe.R;
import com.example.recipe.ui.home.exploreRecipe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class RecipeDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Retrieve the recipe details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String recipeName = args.getString("recipeName", "");
            String recipeDescription = args.getString("recipeDescription", "");
            String recipeIngredient = args.getString("recipeIngredient","");
            String recipeInstruction = args.getString("recipeInstruction", "");
            String recipeServeSize = args.getString("recipeServeSize", "");
            String recipeCookingTime = args.getString("recipeCookingTime", "");

            // Update the UI elements with the recipe details
            TextView nameTextView = view.findViewById(R.id.recipe_name);
            TextView descriptionTextView = view.findViewById(R.id.recipe_description);
            TextView ingredientTextview = view.findViewById (R.id.recipe_ingredient);
            TextView instructionTextview = view.findViewById (R.id.recipe_instruction);
            TextView serveSizeTextview = view.findViewById (R.id.recipe_serveSize);
            TextView cookingTimeTextview = view.findViewById (R.id.recipe_cookingTime);

            nameTextView.setText(recipeName);
            descriptionTextView.setText(recipeDescription);
            ingredientTextview.setText(recipeIngredient);
            instructionTextview.setText(recipeInstruction);
            serveSizeTextview.setText(recipeServeSize);
            cookingTimeTextview.setText(recipeCookingTime);

            // Add other UI element updates as needed
        }

        return view;
    }
}
