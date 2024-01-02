package com.example.recipe.ui.home;

import com.example.recipe.R;
import com.example.recipe.ui.home.exploreRecipe;
import android.util.Log;
import android.widget.ProgressBar;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextUtils;


import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;

import androidx.fragment.app.Fragment;

public class RecipeDetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        // Retrieve the recipe details from arguments
        Bundle args = getArguments();
        if (args != null) {
            String recipeName = args.getString("recipeName");
            String recipeDescription = args.getString("recipeDescription", "");
            String recipeIngredient = args.getString("recipeIngredient","");
            String recipeInstruction = args.getString("recipeInstruction", "");
            String recipeServeSize = args.getString("recipeServeSize", "");
            String recipeCookingTime = args.getString("recipeCookingTime", "");
            String imageUrl = args.getString("imageUrl", "");

            // Update the UI elements with the recipe details
            TextView nameTextView = view.findViewById(R.id.recipe_name);
            TextView descriptionTextView = view.findViewById(R.id.recipe_description);
            TextView ingredientTextview = view.findViewById (R.id.recipe_ingredient);
            TextView instructionTextview = view.findViewById (R.id.recipe_instruction);
            TextView serveSizeTextview = view.findViewById (R.id.recipe_serveSize);
            TextView cookingTimeTextview = view.findViewById (R.id.recipe_cookingTime);
            ImageView imageView = view.findViewById(R.id.imageView);
            ProgressBar progressBar = view.findViewById(R.id.progressBar);

            nameTextView.setText(recipeName);
            descriptionTextView.setText(recipeDescription);
            ingredientTextview.setText(recipeIngredient);
            instructionTextview.setText(recipeInstruction);
            serveSizeTextview.setText(recipeServeSize);
            cookingTimeTextview.setText(recipeCookingTime);

            if (!TextUtils.isEmpty(imageUrl)) {
                progressBar.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);

                int width = 666;  // Adjust the value according to your needs
                int height = 777;
                // Load the image dynamically using Picasso with callback
                Picasso.get()
                        .load(imageUrl).resize(width, height).centerCrop()
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {
                                // Image successfully loaded, hide progress bar
                                imageView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {
                                // Handle error, hide progress bar
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
            // Add other UI element updates as needed
        }

        return view;
    }

}
