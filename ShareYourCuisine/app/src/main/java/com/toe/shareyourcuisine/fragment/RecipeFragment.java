package com.toe.shareyourcuisine.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class RecipeFragment extends Fragment implements RecipeService.GetAllRecipesListener, SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = "ToeRecipeFragment:";
    private RecyclerView mRecipeRV;
    private RecyclerView.Adapter mAdapter;
    private FloatingActionButton mCreateRecipeFAB;
    private SwipeRefreshLayout mRecipeSRL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipe, container, false);
        mRecipeRV = (RecyclerView)rootView.findViewById(R.id.recipe_rv);
        mCreateRecipeFAB = (FloatingActionButton) rootView.findViewById(R.id.create_recipe_fab);
        mRecipeSRL = (SwipeRefreshLayout)rootView.findViewById(R.id.recipe_srl);
        mRecipeRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCreateRecipeFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CreateRecipeActivity.class);
                startActivity(intent);
            }
        });
        mRecipeSRL.setOnRefreshListener(this);
        mRecipeSRL.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorRed), ContextCompat.getColor(getActivity(), R.color.colorAccent), ContextCompat.getColor(getActivity(), R.color.colorOrange));
        getAllRecipes();
        return rootView;
    }

    @Override
    public void getAllRecipesSucceed(List<Recipe> recipes) {
        mAdapter = new RecipeRecyclerViewAdapter(getActivity(), recipes);
        mRecipeRV.setAdapter(mAdapter);
        mRecipeSRL.setRefreshing(false);
    }

    @Override
    public void getAllRecipesFail(String errorMsg) {
        mRecipeSRL.setRefreshing(false);
        Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRefresh() {
        getAllRecipes();
    }

    public void getAllRecipes() {
        mRecipeSRL.setRefreshing(true);
        RecipeService recipeService = new RecipeService(getActivity());
        recipeService.setGetAllRecipesListener(this);
        recipeService.getAllRecipes();
    }
}
