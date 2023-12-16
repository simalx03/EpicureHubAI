package com.example.recipe.ui.home;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.recipe.R;

public class exploreRecipe extends Fragment {

    public exploreRecipe() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getContext(), "Welcome to explore", Toast.LENGTH_SHORT).show();
    }

}
