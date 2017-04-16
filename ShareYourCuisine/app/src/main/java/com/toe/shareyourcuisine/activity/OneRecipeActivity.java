package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.CommentRecyclerViewAdapter;
import com.toe.shareyourcuisine.libs.RatingDialog;
import com.toe.shareyourcuisine.model.Comment;
import com.toe.shareyourcuisine.model.Favorite;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.service.CommentService;
import com.toe.shareyourcuisine.service.FavoriteService;
import com.toe.shareyourcuisine.service.RecipeService;

import org.parceler.Parcels;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/19/2016.
 */

public class OneRecipeActivity extends BaseActivity implements RecipeService.RateRecipeListener, CommentService.CreateCommentListener, CommentService.GetCommentsByParentIdListener, RecipeService.DeleteRecipeListener, FavoriteService.CreateFavoriteListener, FavoriteService.GetFavoritesByUserIdListener, FavoriteService.DeleteFavoriteListener {

    private ImageView mDisplayImgIV;
    private CircleImageView mCreatedUserAvatarIV;
    private TextView mCreatedUserNameTV;
    private TextView mFlavorTV;
    private TextView mTitleTV;
    private SimpleRatingBar mRatingSRB;
    private TextView mRatedUsersNumTV;
    private TextView mCookingTimeTV;
    private TextView mContentTV;
    private ImageView mSelectedImageIV;
    private LinearLayout mContentImgLayout;
    private LayoutInflater mContentImgLayoutInflater;
    private Button mRateBtn;
    private Button mCommentBtn;
    private Button mFavoriteBtn;
    private CommentRecyclerViewAdapter mCommentAdapter;
    private RecyclerView mCommentsRV;
    private MaterialDialog mProgressDialog;
    private MaterialDialog mConfirmDialog;
    private Recipe mRecipe;
    private RecipeService mRecipeService;
    private String mFavoriteId;
    private boolean mIsRated;

    private static final String TAG = "ToeOneRecipeActivity:";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_one_recipe);
        mRecipe = Parcels.unwrap(getIntent().getParcelableExtra("recipe"));
        mRecipeService = new RecipeService(OneRecipeActivity.this);
        mIsRated = false;

        mDisplayImgIV = (ImageView)findViewById(R.id.display_img_iv);
        mCreatedUserAvatarIV = (CircleImageView) findViewById(R.id.created_user_avatar_iv);
        mCreatedUserNameTV = (TextView)findViewById(R.id.created_user_name_tv);
        mFlavorTV = (TextView)findViewById(R.id.flavor_tv);
        mTitleTV = (TextView)findViewById(R.id.title_tv);
        mRatingSRB = (SimpleRatingBar)findViewById(R.id.rating_srb);
        mRatedUsersNumTV = (TextView)findViewById(R.id.rated_users_num_tv);
        mCookingTimeTV = (TextView)findViewById(R.id.cooking_time_tv);
        mContentTV = (TextView)findViewById(R.id.content_tv);
        mSelectedImageIV = (ImageView)findViewById(R.id.selected_img_iv);
        mContentImgLayout = (LinearLayout)findViewById(R.id.content_img_layout);
        mContentImgLayoutInflater = LayoutInflater.from(OneRecipeActivity.this);
        mRateBtn = (Button)findViewById(R.id.rate_btn);
        mCommentBtn = (Button)findViewById(R.id.comment_btn);
        mFavoriteBtn = (Button)findViewById(R.id.favorite_btn);
        mCommentsRV = (RecyclerView)findViewById(R.id.comments_rv);
        mCommentsRV.setLayoutManager(new LinearLayoutManager(OneRecipeActivity.this));

//        Display recipe data
        Picasso.with(OneRecipeActivity.this).load(mRecipe.getDisplayImgUrl()).fit().centerCrop().into(mDisplayImgIV);
        Picasso.with(OneRecipeActivity.this).load(mRecipe.getCreatedUserAvatarUrl()).fit().centerCrop().into(mCreatedUserAvatarIV);
        mCreatedUserNameTV.setText(mRecipe.getCreatedUserName());
        mFlavorTV.setText(mRecipe.getFlavorTypes());
        mTitleTV.setText(mRecipe.getTitle());
        if(mRecipe.getRatedBy().size() == 0)
            mRatingSRB.setRating(0);
        else
            mRatingSRB.setRating(mRecipe.getTotalRates()/mRecipe.getRatedBy().size());
        mRatedUsersNumTV.setText(mRecipe.getRatedBy().size() + " rated this recipe");
        mCookingTimeTV.setText(mRecipe.getCookingTime());
        mContentTV.setText(mRecipe.getContent());
        mSelectedImageIV.getLayoutParams().width = (int) getResources().getDimension(R.dimen.img_dimen);
        mSelectedImageIV.getLayoutParams().height = (int) getResources().getDimension(R.dimen.img_dimen);
        Picasso.with(OneRecipeActivity.this).load(mRecipe.getContentImgUrls().get(0)).fit().centerCrop().into(mSelectedImageIV);
        for(int i = 0; i < mRecipe.getContentImgUrls().size(); i++) {
            View view = mContentImgLayoutInflater.inflate(R.layout.content_img_item, mContentImgLayout, false);
            ImageView iv = (ImageView) view.findViewById(R.id.content_img);
            final String imgUrl = mRecipe.getContentImgUrls().get(i);
            Picasso.with(OneRecipeActivity.this).load(imgUrl).fit().centerCrop().into(iv);
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Picasso.with(OneRecipeActivity.this).load(imgUrl).fit().centerCrop().into(mSelectedImageIV);
                }
            });
            mContentImgLayout.addView(view);
        }


        mRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsRated == true) {
                    Toast.makeText(OneRecipeActivity.this, "You have already rated this recipe", Toast.LENGTH_SHORT).show();
                } else {
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
                                .onRatingSubmit(new RatingDialog.RatingSubmitListener() {
                                    @Override
                                    public void onRatingSubmit(float rating, boolean thresholdCleared) {
                                        mRecipe.getRatedBy().add(mAuth.getCurrentUser().getUid());
                                        mRecipeService.setRateRecipeListener(OneRecipeActivity.this);
                                        mRecipeService.rateRecipe(mRecipe.getUid(), mAuth.getCurrentUser().getUid(), rating);
                                    }
                                }).build();

                        ratingDialog.show();
                    } else {
                        Toast.makeText(OneRecipeActivity.this, "Please log in!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    MaterialDialog dialog = new MaterialDialog.Builder(OneRecipeActivity.this)
                            .title("Comment")
                            .customView(R.layout.dialog_comment, true)
                            .positiveText("Confirm")
                            .negativeText(android.R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    MaterialEditText contentET = (MaterialEditText) dialog.getCustomView().findViewById(R.id.content_et);
                                    if(contentET.getText() != null && contentET.getText().toString().length() >= 10 && contentET.getText().toString().length() <= 400) {
                                        mProgressDialog= new MaterialDialog.Builder(OneRecipeActivity.this)
                                                .title("Submitting")
                                                .content("Please wait")
                                                .progress(true, 0)
                                                .show();
                                        CommentService commentService = new CommentService(OneRecipeActivity.this);
                                        commentService.setCreateCommentListener(OneRecipeActivity.this);
                                        commentService.createComment(mRecipe.getUid(), contentET.getText().toString(), mAuth.getCurrentUser());
                                    } else
                                        Toast.makeText(OneRecipeActivity.this, "Comment length should between 10 and 400!", Toast.LENGTH_LONG).show();

                                }
                            }).build();
                    dialog.show();
                } else {
                    Toast.makeText(OneRecipeActivity.this, "Please log in!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFavoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    FavoriteService favoriteService = new FavoriteService();
                    if(mFavoriteId != null && mFavoriteId != "") {
                        favoriteService.setDeleteFavoriteListener(OneRecipeActivity.this);
                        favoriteService.deleteFavorite(mFavoriteId);
                    } else {
                        favoriteService.setCreateFavoriteListener(OneRecipeActivity.this);
                        favoriteService.createFavorite(mAuth.getCurrentUser().getUid(), mRecipe);
                    }

                } else {
                    Toast.makeText(OneRecipeActivity.this, "Please log in!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        CommentService commentService = new CommentService(OneRecipeActivity.this);
        commentService.setGetCommentsByParentIdListener(OneRecipeActivity.this);
        commentService.getCommentsByParentId(mRecipe.getUid());

        FavoriteService favoriteService = new FavoriteService();
        favoriteService.setGetFavoritesByUserIdListener(OneRecipeActivity.this);

//        Check whether the user has favorited or rated this recipe
        if(mAuth.getCurrentUser() != null) {
            favoriteService.getFavoritesByUserId(mAuth.getCurrentUser().getUid());
            if(mRecipe.getRatedBy().contains(mAuth.getCurrentUser().getUid())) {
                mIsRated = true;
                mRateBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bottom_starred, 0,0,0);
                mRateBtn.setText("Rated");
            } else {
                mIsRated = false;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(mAuth.getCurrentUser() != null && mAuth.getCurrentUser().getUid().equals(mRecipe.getCreatedUserId()))
            getMenuInflater().inflate(R.menu.delete_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_delete:
                mConfirmDialog = new MaterialDialog.Builder(this)
                        .title("Delete")
                        .content("Are you sure to delete this recipe?")
                        .positiveText("Yes")
                        .negativeText("No")
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                mProgressDialog= new MaterialDialog.Builder(OneRecipeActivity.this)
                                        .title("Deleting recipe")
                                        .content("Please wait")
                                        .progress(true, 0)
                                        .show();
                                mRecipeService.setDeleteRecipeListener(OneRecipeActivity.this);
                                mRecipeService.deleteRecipe(mRecipe.getUid(), mRecipe.getDisplayImgUrl(), mRecipe.getContentImgUrls());
                            }
                        })
                        .show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void rateRecipeSucceed() {
        Toast.makeText(this, "Rate successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void rateRecipeFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void createCommentSucceed() {
        mProgressDialog.dismiss();
        Toast.makeText(this, "Create comment successfully!", Toast.LENGTH_LONG).show();
        CommentService commentService = new CommentService(OneRecipeActivity.this);
        commentService.setGetCommentsByParentIdListener(OneRecipeActivity.this);
        commentService.getCommentsByParentId(mRecipe.getUid());
    }

    @Override
    public void createCommentFail(String errorMsg) {
        mProgressDialog.dismiss();
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getCommentsByParentIdSucceed(List<Comment> comments) {
        mCommentAdapter = new CommentRecyclerViewAdapter(OneRecipeActivity.this, comments);
        mCommentsRV.setAdapter(mCommentAdapter);
    }

    @Override
    public void getCommentsByParentIdFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void deleteRecipeSucceed() {
        mProgressDialog.dismiss();
        Toast.makeText(this, "Delete recipe successfully", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void deleteRecipeFail(String errorMsg) {
        mProgressDialog.dismiss();
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void createFavoriteSucceed(String favoriteId) {
        Toast.makeText(this, "Add to favorite successfully!", Toast.LENGTH_LONG).show();
        mFavoriteBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bottom_favorited, 0,0,0);
        mFavoriteBtn.setText("Favorited");
        mFavoriteId = favoriteId;
    }

    @Override
    public void createFavoriteFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getFavoritesByUserIdSucceed(List<Favorite> favorites) {
        for(Favorite favorite: favorites) {
            if(favorite.getRecipe().getUid().equalsIgnoreCase(mRecipe.getUid())) {
                mFavoriteBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bottom_favorited, 0,0,0);
                mFavoriteBtn.setText("Favorited");
                mFavoriteId = favorite.getUid();
                break;
            }
        }
    }

    @Override
    public void getFavoritesByUserIdFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void deleteFavoriteSucceed() {
        mFavoriteBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_bottom_favorite, 0,0,0);
        mFavoriteBtn.setText("Favorite");
        mFavoriteId = null;
        Toast.makeText(this, "Remove favorite successfully", Toast.LENGTH_LONG).show();
    }

    @Override
    public void deleteFavoriteFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
