package com.example.recipe.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import android.text.TextUtils;

import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.recipe.R;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder>  implements Filterable {

    private List<Recipe> recipeList;
    private List<Recipe> filteredRecipeList;
    private List<Recipe> originalRecipeList;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
        this.filteredRecipeList = new ArrayList<>(recipeList);
        this.originalRecipeList = new ArrayList<>(recipeList);
    }

    @Override
    public Filter getFilter() {
        return recipeFilter;
    }

    private final Filter recipeFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Recipe> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalRecipeList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Recipe recipe : recipeList) {
                    if (recipe.getRecipeName().toLowerCase().contains(filterPattern) ||
                            recipe.getRecipeDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(recipe);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            recipeList.clear();
            recipeList.addAll((List<Recipe>) results.values);
            notifyDataSetChanged();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);
        holder.bind(recipe);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeNameTextView;
        private TextView descriptionTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);

            // Set the click listener for the item view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                        onItemClickListener.onItemClick(position);
                    }
                }
            });
        }

        public void bind(Recipe recipe) {
            recipeNameTextView.setText(recipe.getRecipeName());
            descriptionTextView.setText(recipe.getRecipeDescription ());
            // You can bind other data to UI elements here
        }
    }

    // Recipe details section
    // Define an interface for item click events
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Member variable for the listener
    private OnItemClickListener onItemClickListener;

    // Setter method for the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}
