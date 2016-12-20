package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Recipe;

import org.parceler.Parcels;

import java.io.File;

/**
 * Created by HQu on 12/19/2016.
 */

public class OneRecipeActivity extends AppCompatActivity {

    private ImageView mDisplayImgIV;

    private static final String TAG = "ToeOneRecipeActivity:";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_one_recipe);
        Recipe recipe = Parcels.unwrap(getIntent().getParcelableExtra("recipe"));

        mDisplayImgIV = (ImageView)findViewById(R.id.display_img_iv);
        Picasso.with(OneRecipeActivity.this).load(recipe.getDisplayImgUrl()).fit().centerCrop().into(mDisplayImgIV);
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
}
