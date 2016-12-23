package com.toe.shareyourcuisine.activity;

import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
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
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.service.UserService;
import com.toe.shareyourcuisine.utils.Constants;

import org.parceler.Parcels;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/19/2016.
 */

public class OneRecipeActivity extends BaseActivity implements UserService.GetUserInfoListener{

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
        Recipe recipe = Parcels.unwrap(getIntent().getParcelableExtra("recipe"));

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
        if(recipe.getRatedUserNumber() == 0)
            mRatingSRB.setRating(0);
        else
            mRatingSRB.setRating(recipe.getTotalRates()/recipe.getRatedUserNumber());
        mCookingTimeTV.setText(recipe.getCookingTime());
        mContentTV.setText(recipe.getContent());

        mRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {

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
}
