package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Select;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.service.RecipeService;
import com.toe.shareyourcuisine.utils.SYCUtils;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HQu on 12/4/2016.
 */

public class CreateRecipeActivity extends AppCompatActivity implements RecipeService.CreateNewRecipeListener, Validator.ValidationListener{

    private static final String TAG = "ToeCreateRecipeActivity:";
    @NotEmpty
    private MaterialEditText mTitleET;
    private MaterialBetterSpinner mCookingTimeSpin;
    @NotEmpty
    private MaterialEditText mContentET;
    private Button mSelectImgBtn;
    private LinearLayout mContentImgLayout;
    private LayoutInflater mContentImgLayoutInflater;
    private ImageView mSelectedImageIV;
    private Button mSubmitBtn;
    private ArrayList<String> mContentImgUrls;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser mUser;
    private MaterialDialog mMaterialDialog;
    private Validator mValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();
            }
        };
        mValidator = new Validator(CreateRecipeActivity.this);
        mValidator.setValidationListener(CreateRecipeActivity.this);

        mTitleET = (MaterialEditText)findViewById(R.id.title_et);
        mCookingTimeSpin = (MaterialBetterSpinner)findViewById(R.id.cooking_time_spin);
        mContentET = (MaterialEditText)findViewById(R.id.content_et);
        mSelectImgBtn = (Button)findViewById(R.id.select_img_btn);
        mContentImgLayout = (LinearLayout)findViewById(R.id.content_img_layout);
        mSelectedImageIV = (ImageView)findViewById(R.id.selected_img);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);
        mContentImgLayoutInflater = LayoutInflater.from(CreateRecipeActivity.this);

        final String[] COUNTRIES = new String[] {
                "< 10 min", "10~30 min", "30~60 min", "> 60 min"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        mCookingTimeSpin.setAdapter(adapter);

        mSelectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FishBun.with(CreateRecipeActivity.this)
                        .setActionBarColor(Color.rgb(211, 47, 47), Color.rgb(211, 47, 47))
                        .setPickerCount(5)
                        .setCamera(true)
                        .startAlbum();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog.Builder(CreateRecipeActivity.this)
                        .title("Creating recipe")
                        .content("Please wait")
                        .progress(true, 0)
                        .show();
                mValidator.validate();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    mSelectedImageIV.getLayoutParams().width = (int) getResources().getDimension(R.dimen.img_dimen);
                    mSelectedImageIV.getLayoutParams().height = (int) getResources().getDimension(R.dimen.img_dimen);
                    mContentImgLayout.removeAllViews();
                    mContentImgUrls = data.getStringArrayListExtra(Define.INTENT_PATH);
                    //Initiate first image as default selected
                    Picasso.with(CreateRecipeActivity.this).load(new File(mContentImgUrls.get(0))).fit().centerCrop().into(mSelectedImageIV);
                    for (int i = 0; i < mContentImgUrls.size(); i++) {
                        View view = mContentImgLayoutInflater.inflate(R.layout.content_img_item, mContentImgLayout, false);
                        ImageView iv = (ImageView) view.findViewById(R.id.content_img);
                        final String imgUrl = mContentImgUrls.get(i);
                        Picasso.with(CreateRecipeActivity.this).load(new File(imgUrl)).fit().centerCrop().into(iv);
                        iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Picasso.with(CreateRecipeActivity.this).load(new File(imgUrl)).fit().centerCrop().into(mSelectedImageIV);
                            }
                        });
                        mContentImgLayout.addView(view);
                    }
                }
        }
    }

    @Override
    public void createNewRecipeSucceed() {
        mMaterialDialog.dismiss();
        Toast.makeText(CreateRecipeActivity.this, "Create recipe successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void createNewRecipeFail(String errorMsg) {
        mMaterialDialog.dismiss();
        Toast.makeText(CreateRecipeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationSucceeded() {
        Recipe recipe = new Recipe();
        recipe.setTitle(mTitleET.getText().toString());
        recipe.setCookingTime(mCookingTimeSpin.getText().toString());
        recipe.setDisplayImgUrl(mContentImgUrls.get(0));
        recipe.setContent(mContentET.getText().toString());
        recipe.setCreatedBy(mUser.getUid());
        recipe.setCreatedAt(SYCUtils.getCurrentEST());
        recipe.setLastCommentedAt(SYCUtils.getCurrentEST());
        RecipeService recipeService = new RecipeService(CreateRecipeActivity.this);
        recipeService.createRecipe(recipe, mContentImgUrls);
        recipeService.setCreateNewRecipeListener(CreateRecipeActivity.this);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        mMaterialDialog.dismiss();
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
