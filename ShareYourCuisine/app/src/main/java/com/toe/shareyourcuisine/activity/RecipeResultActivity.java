package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.RecipeRecyclerViewAdapter;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.service.RecipeService;

import org.parceler.Parcels;

import java.util.List;

/**
 * Created by HQu on 2/26/2017.
 */

public class RecipeResultActivity extends BaseActivity implements RecipeService.GetRecipesByNameListener {

    private RecyclerView mRecipeRV;
    private RecipeRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecipeRV = (RecyclerView)findViewById(R.id.recipe_rv);
        mRecipeRV.setLayoutManager(new LinearLayoutManager(RecipeResultActivity.this));

        String query = getIntent().getStringExtra("query");
        setTitle(query);
        RecipeService recipeService = new RecipeService(RecipeResultActivity.this);
        recipeService.setGetRecipesByNameListener(RecipeResultActivity.this);
        recipeService.getRecipesByName(query);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getRecipesByNameSucceed(final List<Recipe> recipes) {
        mAdapter = new RecipeRecyclerViewAdapter(RecipeResultActivity.this, recipes);
        mAdapter.setRecipeItemClickListener(new RecipeRecyclerViewAdapter.RecipeItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(RecipeResultActivity.this, OneRecipeActivity.class);
                Parcelable wrapped = Parcels.wrap(recipes.get(position));
                intent.putExtra("recipe", wrapped);
                startActivity(intent);
            }
        });
        mRecipeRV.setAdapter(mAdapter);
    }

    @Override
    public void getRecipesByNameFail(String errorMsg) {
        Toast.makeText(RecipeResultActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
