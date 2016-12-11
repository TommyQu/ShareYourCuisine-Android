package com.toe.shareyourcuisine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.activity.CreateRecipeActivity;
import com.toe.shareyourcuisine.activity.SignInActivity;
import com.toe.shareyourcuisine.adapter.RecipeRecyclerViewAdapter;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.service.RecipeService;

import java.util.List;

/**
 * Created by HQu on 12/3/2016.
 */

public class RecipeFragment extends Fragment implements RecipeService.GetAllRecipesListener{

    private static final String TAG = "ToeRecipeFragment:";
    private RecyclerView mRecipeRV;
    private RecyclerView.Adapter mAdapter;
    private FloatingActionButton mCreateRecipeFAB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        mRecipeRV = (RecyclerView)rootView.findViewById(R.id.recipe_rv);
        mRecipeRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCreateRecipeFAB = (FloatingActionButton) rootView.findViewById(R.id.create_recipe_fab);
        mCreateRecipeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateRecipeActivity.class);
                startActivity(intent);
            }
        });
        RecipeService recipeService = new RecipeService(getActivity());
        recipeService.setGetAllRecipesListener(this);
        recipeService.getAllRecipes();
        return rootView;
    }

    @Override
    public void getAllRecipesSucceed(List<Recipe> recipes) {
        mAdapter = new RecipeRecyclerViewAdapter(getActivity(), recipes);
        mRecipeRV.setAdapter(mAdapter);
    }

    @Override
    public void getAllRecipesFail(String errorMsg) {
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }
}
