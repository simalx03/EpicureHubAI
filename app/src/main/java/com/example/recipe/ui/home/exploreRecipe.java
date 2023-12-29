package com.example.recipe.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import androidx.appcompat.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.recipe.R;
import java.util.ArrayList;
import java.util.List;


public class exploreRecipe extends Fragment {

        private RecyclerView recyclerView;
        private RecipeAdapter recipeAdapter; // Assuming you have a custom adapter
        private List<Recipe> recipeList = new ArrayList<>();
        private SearchView searchView;


        private DatabaseReference databaseReference;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_explore_recipe, container, false);

            // Initialize Firebase
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("recipes");

            // Initialize UI elements
            searchView = view.findViewById(R.id.searchView);
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recipeAdapter = new RecipeAdapter(recipeList);
            recyclerView.setAdapter(recipeAdapter);

            // Read data from Firebase
            readDataFromFirebase();

            // Set up search functionality
            setupSearch();

            // In ExploreRecipeFragment, set up click listener for RecyclerView items
            recipeAdapter.setOnItemClickListener(new RecipeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    // Get the selected recipe
                    Recipe selectedRecipe = recipeList.get(position);

                    // Open RecipeDetailFragment and pass the recipe details
                    openRecipeDetailFragment(selectedRecipe);
                }
            });

            return view;
        }

        private void setupSearch() {
            // Set a query listener for the search view
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Handle search query submission if needed
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Filter the list based on the search query
                    recipeAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

        private void readDataFromFirebase() {
            // Attach a listener to read the data at our recipes reference
            DatabaseReference allRecipesRef = databaseReference;

            // Attach a listener to read the data at the recipes reference
            allRecipesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recipeList.clear();
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        for (DataSnapshot recipeSnapshot : userSnapshot.getChildren()) {
                            Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                            if (recipe != null) {
                                recipeList.add(recipe);
                            }
                        }
                    }
                    recipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Toast.makeText(getContext(), "Failed to read recipes from Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }


    // Function to open RecipeDetailFragment and pass the recipe details
    // Inside your onItemClick method in exploreRecipe.java
    private void openRecipeDetailFragment(Recipe recipe) {
        // Get the NavController
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Navigate to RecipeDetailFragment and pass the recipe details as arguments
        navController.navigate(R.id.action_exploreFragment_to_recipeDetailFragment, getBundle(recipe));
    }

    private Bundle getBundle(Recipe recipe) {
        Bundle args = new Bundle();
        args.putString("recipeName", recipe.getRecipeName());
        args.putString("recipeDescription", recipe.getRecipeDescription());
        args.putString("recipeIngredient", recipe.getIngredients ());
        args.putString("recipeInstruction", recipe.getInstructions());
        args.putString("recipeServeSize", recipe.getServeSize ());
        args.putString("recipeCookingTime", recipe.getCookingTime ());
        // Add other details as needed
        return args;
    }


}

