package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.libs.RatingDialog;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.service.RecipeService;
import com.toe.shareyourcuisine.service.UserService;
import com.toe.shareyourcuisine.utils.Constants;

import org.parceler.Parcels;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/19/2016.
 */

public class OneRecipeActivity extends BaseActivity implements UserService.GetUserInfoListener, RecipeService.RateRecipeListener {

    private ImageView mDisplayImgIV;
    private CircleImageView mCreatedUserAvatarIV;
    private TextView mCreatedUserNameTV;
    private TextView mFlavorTV;
    private TextView mTitleTV;
    private SimpleRatingBar mRatingSRB;
    private TextView mCookingTimeTV;
    private TextView mContentTV;
    private Button mRateBtn;
    private Button mCommentBtn;

    private static final String TAG = "ToeOneRecipeActivity:";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_one_recipe);
        final Recipe recipe = Parcels.unwrap(getIntent().getParcelableExtra("recipe"));

        mDisplayImgIV = (ImageView)findViewById(R.id.display_img_iv);
        mCreatedUserAvatarIV = (CircleImageView) findViewById(R.id.created_user_avatar_iv);
        mCreatedUserNameTV = (TextView)findViewById(R.id.created_user_name_tv);
        mFlavorTV = (TextView)findViewById(R.id.flavor_tv);
        mTitleTV = (TextView)findViewById(R.id.title_tv);
        mRatingSRB = (SimpleRatingBar)findViewById(R.id.rating_srb);
        mCookingTimeTV = (TextView)findViewById(R.id.cooking_time_tv);
        mContentTV = (TextView)findViewById(R.id.content_tv);
        mRateBtn = (Button)findViewById(R.id.rate_btn);
        mCommentBtn = (Button)findViewById(R.id.comment_btn);

        Picasso.with(OneRecipeActivity.this).load(recipe.getDisplayImgUrl()).fit().centerCrop().into(mDisplayImgIV);
        mFlavorTV.setText(recipe.getFlavorTypes());
        mTitleTV.setText(recipe.getTitle());
        if(recipe.getRatedBy().size() == 0)
            mRatingSRB.setRating(0);
        else
            mRatingSRB.setRating(recipe.getTotalRates()/recipe.getRatedBy().size());
        mCookingTimeTV.setText(recipe.getCookingTime());
        mContentTV.setText(recipe.getContent());

        mRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    RatingDialog ratingDialog = new RatingDialog.Builder(OneRecipeActivity.this)
//                            .icon(drawable)
                            .threshold(3)
                            .title("How do you like this?")
                            .titleTextColor(R.color.textGrey)
                            .positiveButtonText("Confirm")
                            .negativeButtonText("Cancel")
                            .positiveButtonTextColor(R.color.colorRed)
                            .negativeButtonTextColor(R.color.colorRed)
                            .ratingBarColor(R.color.golden_stars)
//                            .positiveButtonBackgroundColor(R.color.colorRed)
//                            .negativeButtonBackgroundColor(R.color.white)
                            .onRatingSubmit(new RatingDialog.RatingSubmitListener() {
                                @Override
                                public void onRatingSubmit(float rating, boolean thresholdCleared) {
                                    if(recipe.getRatedBy().contains(mAuth.getCurrentUser().getUid()))
                                        Toast.makeText(OneRecipeActivity.this, "You have already rated this recipe", Toast.LENGTH_LONG).show();
                                    else {
                                        recipe.getRatedBy().add(mAuth.getCurrentUser().getUid());
                                        RecipeService recipeService = new RecipeService(OneRecipeActivity.this);
                                        recipeService.setRateRecipeListener(OneRecipeActivity.this);
                                        recipeService.rateRecipe(recipe.getUid(), mAuth.getCurrentUser().getUid(), rating);
                                    }
                                }
                            }).build();

                    ratingDialog.show();
                } else {
                    Toast.makeText(OneRecipeActivity.this, "Please log in!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {

                } else {
                    Toast.makeText(OneRecipeActivity.this, "Please log in!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        UserService userService = new UserService(OneRecipeActivity.this);
        userService.setUserInfoListener(OneRecipeActivity.this);
        userService.getUserInfo(recipe.getCreatedBy(), Constants.ACTION_GET_USER_PROFILE);
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
    public void getUserInfoSucceed(User user) {
        Picasso.with(OneRecipeActivity.this).load(user.getAvatarUrl()).fit().centerCrop().into(mCreatedUserAvatarIV);
        mCreatedUserNameTV.setText(user.getfName() + " " + user.getlName());
    }

    @Override
    public void getUserInfoFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void rateRecipeSucceed() {
        Toast.makeText(this, "Rate successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void rateRecipeFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
