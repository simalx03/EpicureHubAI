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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.bumptech.glide.Glide;



public class shareFragment extends Fragment {

    private DatabaseReference databaseReference;
    private EditText editTextRecipeName, editTextRecipeDescription, ingridientsText, instructionText, serveSizeText, cookingTimeText;
    private Spinner spinnerCategory;
    private Button btnSubmit;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ImageView imageViewRecipe;
    private ImageButton btnPickImage;
    private String userId;

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
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        imageViewRecipe = view.findViewById(R.id.imageViewRecipe);
        btnPickImage = view.findViewById(R.id.btnPickImage);
        imageViewRecipe.setVisibility (View.GONE);

        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });


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
                String category = spinnerCategory.getSelectedItem().toString();
                String cookingTime = cookingTimeText.getText().toString().trim();

                // Check if both fields are not empty before submitting to Firebase
                if (!recipeName.isEmpty() && !recipeDescription.isEmpty() && !ingridients.isEmpty() && !instruction.isEmpty() && !serveSize.isEmpty() && !cookingTime.isEmpty()) {
                    sendRecipeToFirebase(recipeName, recipeDescription, ingridients, instruction, serveSize, cookingTime, category);
                    Toast.makeText(getContext(), "Recipe submitted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Please fill all the required feilds", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            int width = 500;  // Adjust the value according to your needs
            int height = 500;
            Picasso.get().load(imageUri).resize(width, height).into(imageViewRecipe);
            imageViewRecipe.setVisibility (View.VISIBLE);
        }
    }

    private void uploadImage(final String recipeKey, String recipeName, String recipeDescription, String ingridients, String instruction, String serveSize, String cookingTime, String category) {
        // Create a reference to "recipe_images" in Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("recipe_images").child(recipeKey);

        UploadTask uploadTask = storageRef.putFile(imageUri);
        // Upload the image to Firebase Storage
        storageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image
                        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Save the image URL in the Realtime Database
                                databaseReference.child(userId).child(recipeKey).child("image_url").setValue(downloadUri.toString());

                                // Continue with any other code you want to execute after successfully uploading the image
                                // For example, you might want to save the recipe details in the Realtime Database
                                // after successfully uploading the image.
                                saveRecipeDetails(recipeKey, recipeName, recipeDescription, ingridients, instruction, serveSize, cookingTime, category, downloadUri);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle image upload failure
                        Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        // Continue with any other code you want to execute after a failure
                        // For example, you might want to display an error message or take other actions.
                    }
                });
    }

    private void saveRecipeDetails(String recipeKey, String recipeName, String recipeDescription, String ingridients, String instruction, String serveSize, String cookingTime, String category, Uri downloadUri) {
        // Save the remaining recipe details in the Realtime Database
        String imageUrl = downloadUri.toString();
        Recipe recipe = new Recipe(recipeKey, recipeName, recipeDescription, ingridients, instruction, serveSize, cookingTime, category, imageUrl);
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

    private void sendRecipeToFirebase(String recipeName, String recipeDescription,String ingridients, String instruction, String serveSize, String cookingTime,String category) {
        // Create a unique key for the recipe
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Get the current user's ID
            userId = currentUser.getUid();

            // Create a unique key for the recipe
            String recipeKey = databaseReference.push().getKey();
            if (imageUri != null) {
                // Upload the image to Firebase Storage
                uploadImage(recipeKey, recipeName, recipeDescription, ingridients, instruction, serveSize, cookingTime, category);
            } else {
                // Display an error message or take other actions if no image is selected
                Toast.makeText(getContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void clearInputFields() {
        editTextRecipeName.getText().clear();
        editTextRecipeDescription.getText().clear();
        ingridientsText.getText().clear();
        instructionText.getText().clear();
        serveSizeText.getText().clear();
        cookingTimeText.getText().clear();
        imageViewRecipe.setImageResource(R.drawable.ic_menu_camera);
    }

}