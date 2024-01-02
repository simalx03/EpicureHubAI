package com.example.recipe;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.content.Intent;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.recipe.ui.home.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser; // Add this line

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recipe.databinding.ActivityHomeBinding;

public class Home extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.fragment_container, new RecipeFragment ())
//                .commit();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        setSupportActionBar (binding.appBarHome.toolbar);
        binding.appBarHome.fab.setOnClickListener (new View.OnClickListener ( ) {
            @Override
            public void onClick(View view) {
                Snackbar.make (view, "\"Welcome to RecipeHub! \uD83C\uDF1F Share your favorite recipes with the community and discover a world of delightful flavors. Let's cook, share, and savor together!\" ", Snackbar.LENGTH_LONG)
                        .setAction ("Action", null).show ( );
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder (
                R.id.nav_home, R.id.exploreRecipe2, R.id.shareFragment, R.id.myRecipeFragment4)
                .setOpenableLayout (drawer)
                .build ( );
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController (this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController (navigationView, navController);
        updateNavigationViewHeader();
    }

    // Helper method to update user's email in the NavigationView header
    private void updateNavigationViewHeader() {
        View headerView = binding.navView.getHeaderView(0); // 0 is the index of the header view
        TextView textViewUserEmail = (TextView) headerView.findViewById(R.id.textViewUserEmail);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userEmail = currentUser.getEmail();
            textViewUserEmail.setText(userEmail);
        }
    }

    public void logout(View view) {
        // Build the confirmation dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User confirmed, log out
                mAuth.signOut();
                Intent intent = new Intent(Home.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User canceled, do nothing
                dialog.dismiss();
            }
        });

        // Show the dialog
        builder.show();
    }


    public void toExplorePage(View view) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.exploreRecipe2);
    }

    public void toSharePage(View view) {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.navigate(R.id.shareFragment);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ( ).inflate (R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController (this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp (navController, mAppBarConfiguration)
                || super.onSupportNavigateUp ( );
    }


}