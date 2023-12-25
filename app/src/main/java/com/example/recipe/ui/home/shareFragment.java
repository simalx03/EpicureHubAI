package com.example.recipe.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.recipe.R;
import com.example.recipe.ui.home.Recipe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;



public class shareFragment extends Fragment {

    private DatabaseReference databaseReference;
    private EditText editTextRecipeName, editTextRecipeDescription, ingridientsText, instructionText, serveSizeText, cookingTimeText;
    private Button btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_share, container, false);

        // Initialize Firebase
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("recipes");

        // Initialize UI elements
        editTextRecipeName = view.findViewById(R.id.editTextRecipeName);
        editTextRecipeDescription = view.findViewById(R.id.editTextRecipeDescription);
        ingridientsText = view.findViewById (R.id.ingridientText);
        instructionText = view.findViewById (R.id.instructionText);
        serveSizeText = view.findViewById(R.id.serveText);
        cookingTimeText = view.findViewById(R.id.cooktimeText);
        btnSubmit = view.findViewById(R.id.btnSubmit);


        // Set up click listener for the submit button
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the recipe name and description from EditText fields
                String recipeName = editTextRecipeName.getText().toString().trim();
                String recipeDescription = editTextRecipeDescription.getText().toString().trim();
                String ingridients = ingridientsText.getText().toString().trim();
                String instruction = instructionText.getText().toString().trim();
                String serveSize = serveSizeText.getText().toString().trim();
                String cookingTime = cookingTimeText.getText().toString().trim();

                // Check if both fields are not empty before submitting to Firebase
                if (!recipeName.isEmpty() && !recipeDescription.isEmpty() && !ingridients.isEmpty() && !instruction.isEmpty() && !serveSize.isEmpty() && !cookingTime.isEmpty()) {
                    sendRecipeToFirebase(recipeName, recipeDescription, ingridients, instruction, serveSize, cookingTime);
                    Toast.makeText(getContext(), "Recipe submitted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please enter both recipe name and description", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void sendRecipeToFirebase(String recipeName, String recipeDescription,String ingridients, String instruction, String serveSize, String cookingTime) {
        // Create a unique key for the recipe
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get the current user's ID
            String userId = currentUser.getUid();

            // Create a unique key for the recipe
            String recipeKey = databaseReference.push().getKey();

            // Create a Recipe object with the provided information
            Recipe recipe = new Recipe(recipeName, recipeDescription, ingridients, instruction, serveSize, cookingTime);

            // Use the unique key to set the recipe data in the database under the user's ID
            databaseReference.child(userId).child(recipeKey).setValue(recipe)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                // Data was successfully submitted to Firebase
                                Toast.makeText(getContext(), "Recipe submitted successfully", Toast.LENGTH_SHORT).show();
                                clearInputFields();
                            } else {
                                // Failed to submit data to Firebase
                                Toast.makeText(getContext(), "Failed to submit recipe", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void clearInputFields() {
        editTextRecipeName.getText().clear();
        editTextRecipeDescription.getText().clear();
        ingridientsText.getText().clear();
        instructionText.getText().clear();
        serveSizeText.getText().clear();
        cookingTimeText.getText().clear();
    }

}