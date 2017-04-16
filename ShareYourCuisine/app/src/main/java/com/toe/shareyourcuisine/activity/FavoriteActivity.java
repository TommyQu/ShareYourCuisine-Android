package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.RecipeRecyclerViewAdapter;
import com.toe.shareyourcuisine.model.Favorite;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.service.FavoriteService;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HQu on 2/28/2017.
 */

public class FavoriteActivity extends BaseActivity implements FavoriteService.GetFavoritesByUserIdListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "ToeFavoriteActivity:";
    private TextView mNoFavTV;
    private RecyclerView mRecipeRV;
    private RecipeRecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mFavoriteSRL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNoFavTV = (TextView)findViewById(R.id.no_fav_tv);
        mRecipeRV = (RecyclerView) findViewById(R.id.recipe_rv);
        mRecipeRV.setLayoutManager(new LinearLayoutManager(FavoriteActivity.this));
        mFavoriteSRL = (SwipeRefreshLayout)findViewById(R.id.favorite_srl);
        mFavoriteSRL.setOnRefreshListener(this);
        mFavoriteSRL.setColorSchemeColors(ContextCompat.getColor(FavoriteActivity.this, R.color.colorRed), ContextCompat.getColor(FavoriteActivity.this, R.color.colorAccent), ContextCompat.getColor(FavoriteActivity.this, R.color.colorOrange));
        getAllFavorites();
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

    public void getAllFavorites() {
        mFavoriteSRL.setRefreshing(true);
        FavoriteService favoriteService = new FavoriteService();
        favoriteService.setGetFavoritesByUserIdListener(FavoriteActivity.this);
        favoriteService.getFavoritesByUserId(mAuth.getCurrentUser().getUid());
    }

    @Override
    public void getFavoritesByUserIdSucceed(List<Favorite> favorites) {
        final List<Recipe> recipes = new ArrayList<>();
        if(favorites.size() <= 0)
            mNoFavTV.setVisibility(View.VISIBLE);
        else
            mNoFavTV.setVisibility(View.GONE);
        for(Favorite favorite: favorites) {
            recipes.add(favorite.getRecipe());
        }
        mAdapter = new RecipeRecyclerViewAdapter(FavoriteActivity.this, recipes);
        mAdapter.setRecipeItemClickListener(new RecipeRecyclerViewAdapter.RecipeItemClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Intent intent = new Intent(FavoriteActivity.this, OneRecipeActivity.class);
                Parcelable wrapped = Parcels.wrap(recipes.get(position));
                intent.putExtra("recipe", wrapped);
                startActivity(intent);
            }
        });
        mRecipeRV.setAdapter(mAdapter);
        mFavoriteSRL.setRefreshing(false);
    }

    @Override
    public void getFavoritesByUserIdFail(String errorMsg) {
        Toast.makeText(FavoriteActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        mFavoriteSRL.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        getAllFavorites();
    }
}
