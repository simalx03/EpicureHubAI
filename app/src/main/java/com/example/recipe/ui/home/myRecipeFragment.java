package com.example.recipe.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.database.ValueEventListener;

import com.example.recipe.R;
import com.example.recipe.ui.home.RecipeAdapter;
import com.example.recipe.ui.home.Recipe;

import java.util.ArrayList;
import java.util.List;


public class myRecipeFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecipeAdapter recipeAdapter; // Assuming you have a custom adapter
    private List<Recipe> recipeList = new ArrayList<>();
    private SearchView searchView;
    private ProgressBar progressBar;

    private Spinner categorySpinner;
    private ArrayAdapter<String> categoryAdapter;

    private DatabaseReference databaseReference;
    private DatabaseReference userReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_recipe, container, false);

        Toast.makeText(getContext(), "Click an item to view details, and long-press to delete.", Toast.LENGTH_SHORT).show();

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipes");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        // Initialize UI elements
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.searchView);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recipeAdapter = new RecipeAdapter(recipeList);
        recyclerView.setAdapter(recipeAdapter);

        if (currentUser != null) {
            // If the user is authenticated, get their UID
            String userId = currentUser.getUid();
            userReference = databaseReference.child(userId); // Reference to the current user's recipes
        } else {
            // Handle the case where the user is not authenticated (optional)
            Toast.makeText(getContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        progressBar.setVisibility(View.VISIBLE);

        // Initialize category Spinner
        categorySpinner = view.findViewById(R.id.categorySpinner);
        categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, getCategoryList());
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setSelection(categoryAdapter.getPosition("All"));

        // Set up category filter listener
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Read data from Firebase based on the selected category
                readDataFromFirebase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event (e.g., open a search activity or perform search)
                // For simplicity, you can just focus the SearchView to show the keyboard
                searchView.setIconified(false);
            }
        });

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

        recipeAdapter.setOnItemLongClickListener(new RecipeAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(int position) {
                showDeleteConfirmationDialog(position);
            }
        });

        return view;
    }

    private void showDeleteConfirmationDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Confirm Delete");
        builder.setMessage("Are you sure you want to delete this recipe?");
        builder.setPositiveButton("Delete", (dialog, which) -> {
            // Handle recipe deletion
            deleteRecipe(position);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // Dismiss the dialog
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void deleteRecipe(int position) {
        Recipe recipeToDelete = recipeList.get(position);

        if (recipeToDelete != null) {
            // Step 1: Retrieve the image URL
            String imageUrlToDelete = recipeToDelete.getImageUrl();

            // Step 2: Use Firebase Storage API to delete the image
            if (imageUrlToDelete != null) {
                // Create a FirebaseStorage instance
                FirebaseStorage storage = FirebaseStorage.getInstance();

                // Create a storage reference
                StorageReference storageReference = storage.getReferenceFromUrl(imageUrlToDelete);

                // Delete the image from Firebase Storage
                storageReference.delete()
                        .addOnSuccessListener(aVoid -> {
                            // Step 3: Once the image is deleted from Storage, proceed to delete data from Realtime Database
                            deleteDataFromDatabase(position, recipeToDelete);
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure to delete the image
                            Toast.makeText(getContext(), "Failed to delete image from storage", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Image Url is null", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void deleteDataFromDatabase(int position, Recipe recipeToDelete) {
        if (userReference != null) {
            String recipeIdToDelete = recipeToDelete.getRecipeId();
            if (recipeIdToDelete != null) {
                // Remove data from Firebase Realtime Database
                userReference.child(recipeIdToDelete).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            // Successfully deleted from Firebase
                            Toast.makeText(getContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();

                            // Notify the adapter about the removal
                            recipeList.remove(position);
                            recipeAdapter.notifyItemRemoved(position);
                        })
                        .addOnFailureListener(e -> {
                            // Failed to delete from Firebase
                            Toast.makeText(getContext(), "Failed to delete recipe", Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Recipe ID is null", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private List<String> getCategoryList() {
        // Return a list of categories, including the "All" option
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Breakfast");
        categories.add("Lunch");
        categories.add("Dinner");
        categories.add("Dessert");
        return categories;
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
                recipeAdapter.getFilter().filter(newText);
                return true;
            }
        });

        // Add a TextWatcher to monitor changes in the search view's text
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    // When focus is lost, check if the text is empty and reload data
                    if (searchView.getQuery().toString().isEmpty()) {
                        readDataFromFirebase();
                    }
                }
            }
        });
    }

    private void readDataFromFirebase() {
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        // Attach a listener to read the data at our recipes reference
        DatabaseReference allRecipesRef = databaseReference;

        if (userReference != null) {
            userReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recipeList.clear();
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                        if ("All".equals(selectedCategory) || (recipe != null && selectedCategory.equals(recipe.getCategory()))) {
                            recipeList.add(recipe);
                        }
                    }
                    recipeAdapter.notifyDataSetChanged();
                    // Hide ProgressBar after loading
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to read recipes from Firebase", Toast.LENGTH_SHORT).show();
                    }
                    // Hide ProgressBar after loading
                    progressBar.setVisibility(View.GONE);
                }

            });
        }
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
        args.putString("imageUrl", recipe.getImageUrl());
        // Add other details as needed
        return args;
    }


}

