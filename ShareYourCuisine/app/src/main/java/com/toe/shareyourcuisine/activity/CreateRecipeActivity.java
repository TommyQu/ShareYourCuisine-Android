package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

public class CreateRecipeActivity extends BaseActivity implements RecipeService.CreateNewRecipeListener, Validator.ValidationListener{

    private static final String TAG = "ToeCRecipeActivity:";
    @NotEmpty
    private MaterialEditText mTitleET;
    private MaterialBetterSpinner mCookingTimeSpin;
    @NotEmpty
    private MaterialEditText mContentET;
    @NotEmpty
    private MaterialEditText mFlavorTypesET;
    private Button mSelectImgBtn;
    private LinearLayout mContentImgLayout;
    private LayoutInflater mContentImgLayoutInflater;
    private ImageView mSelectedImageIV;
    private ImageView mDisplayImgIV;
    private Button mSelectDisplayImgBtn;
    private Button mSubmitBtn;
    private ArrayList<String> mContentImgUrls;
    private String mDisplayImgUrl;
    private MaterialDialog mMaterialDialog;
    private Validator mValidator;
    private String mSelectImgAction;
    private String mFlavorTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mValidator = new Validator(CreateRecipeActivity.this);
        mValidator.setValidationListener(CreateRecipeActivity.this);

        mTitleET = (MaterialEditText)findViewById(R.id.title_et);
        mCookingTimeSpin = (MaterialBetterSpinner)findViewById(R.id.cooking_time_spin);
        mContentET = (MaterialEditText)findViewById(R.id.content_et);
        mFlavorTypesET = (MaterialEditText)findViewById(R.id.flavor_types_et);
        mSelectImgBtn = (Button)findViewById(R.id.select_img_btn);
        mContentImgLayout = (LinearLayout)findViewById(R.id.content_img_layout);
        mSelectedImageIV = (ImageView)findViewById(R.id.selected_img);
        mDisplayImgIV = (ImageView)findViewById(R.id.display_img);
        mSelectDisplayImgBtn = (Button)findViewById(R.id.select_display_img_btn);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);
        mContentImgLayoutInflater = LayoutInflater.from(CreateRecipeActivity.this);

        final String[] COUNTRIES = new String[] {
                "< 10 min", "10~30 min", "30~60 min", "> 60 min"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        mCookingTimeSpin.setAdapter(adapter);

        mFlavorTypesET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(CreateRecipeActivity.this)
                        .title("Select flavor types")
                        .items(R.array.flavor_types)
                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
                                /**
                                 * If you use alwaysCallMultiChoiceCallback(), which is discussed below,
                                 * returning false here won't allow the newly selected check box to actually be selected.
                                 * See the limited multi choice dialog example in the sample project for details.
                                 **/
                                mFlavorTypes = "";
                                for(int i = 0; i < text.length; i++) {
                                    mFlavorTypes += text[i] + ", ";
                                }
                                mFlavorTypes = mFlavorTypes.substring(0, mFlavorTypes.length()-2);
                                mFlavorTypesET.setText(mFlavorTypes);
                                return true;
                            }
                        })
                        .positiveText("Choose")
                        .show();
            }
        });

        mSelectDisplayImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectImgAction = "display";
                FishBun.with(CreateRecipeActivity.this)
                        .setActionBarColor(Color.rgb(211, 47, 47), Color.rgb(211, 47, 47))
                        .setPickerCount(1)
                        .setCamera(true)
                        .startAlbum();
            }
        });
//        Select content images
        mSelectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectImgAction = "content";
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
                    if(mSelectImgAction.equalsIgnoreCase("content")) {
                        ViewGroup.LayoutParams params = mSelectedImageIV.getLayoutParams();
                        params.width = (int) getResources().getDimension(R.dimen.img_dimen);
                        params.height = (int) getResources().getDimension(R.dimen.img_dimen);
                        mSelectedImageIV.setLayoutParams(params);
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
                    } else {
                        ViewGroup.LayoutParams params = mDisplayImgIV.getLayoutParams();
                        params.width = (int) getResources().getDimension(R.dimen.img_dimen);
                        params.height = (int) getResources().getDimension(R.dimen.img_dimen);
                        mDisplayImgIV.setLayoutParams(params);
                        mDisplayImgUrl = data.getStringArrayListExtra(Define.INTENT_PATH).get(0);
                        Picasso.with(CreateRecipeActivity.this).load(new File(mDisplayImgUrl)).fit().centerCrop().into(mDisplayImgIV);
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
        recipe.setFlavorTypes(mFlavorTypes);
        recipe.setTitle(mTitleET.getText().toString());
        recipe.setCookingTime(mCookingTimeSpin.getText().toString());
        recipe.setDisplayImgUrl(mDisplayImgUrl);
        recipe.setContent(mContentET.getText().toString());
        recipe.setCreatedBy(mFirebaseUser.getUid());
        recipe.setCreatedAt(SYCUtils.getCurrentEST());
        recipe.setLastCommentedAt(SYCUtils.getCurrentEST());
        recipe.setTotalRates(0);
        RecipeService recipeService = new RecipeService(CreateRecipeActivity.this);
        recipeService.setCreateNewRecipeListener(CreateRecipeActivity.this);
        recipeService.createRecipe(recipe, mContentImgUrls);
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
