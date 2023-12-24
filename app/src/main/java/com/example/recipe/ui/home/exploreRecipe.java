package com.example.recipe.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        private DatabaseReference databaseReference;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_explore_recipe, container, false);

            // Initialize Firebase
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("recipes");

            // Initialize UI elements
            recyclerView = view.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recipeAdapter = new RecipeAdapter(recipeList);
            recyclerView.setAdapter(recipeAdapter);

            // Read data from Firebase
            readDataFromFirebase();

            return view;
        }

        private void readDataFromFirebase() {
            // Attach a listener to read the data at our recipes reference
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    recipeList.clear();
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        Recipe recipe = recipeSnapshot.getValue(Recipe.class);
                        if (recipe != null) {
                            recipeList.add(recipe);
                        }
                    }
                    recipeAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Toast.makeText(getContext(), "Failed to read data from Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

