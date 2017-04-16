package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.service.PostService;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.io.File;
import java.util.List;

/**
 * Created by HQu on 12/27/2016.
 */

public class CreatePostActivity extends BaseActivity implements Validator.ValidationListener, PostService.CreatePostListener {

    @NotEmpty
    private MaterialEditText mContentET;
    private ImageView mImgIV;
    private Button mSelectImgBtn;
    private Button mSubmitBtn;
    private Validator mValidator;
    private MaterialDialog mMaterialDialog;
    private String mImgUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mValidator = new Validator(CreatePostActivity.this);
        mValidator.setValidationListener(CreatePostActivity.this);

        mContentET = (MaterialEditText)findViewById(R.id.content_et);
        mImgIV = (ImageView)findViewById(R.id.img_iv);
        mSelectImgBtn = (Button)findViewById(R.id.select_img_btn);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);

        mSelectImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FishBun.with(CreatePostActivity.this)
                        .setActionBarColor(Color.rgb(211, 47, 47), Color.rgb(211, 47, 47))
                        .setPickerCount(1)
                        .setCamera(true)
                        .startAlbum();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValidator.validate();
            }
        });
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
    public void onValidationSucceeded() {
        mMaterialDialog = new MaterialDialog.Builder(CreatePostActivity.this)
                .title("Creating post")
                .content("Please wait")
                .progress(true, 0)
                .show();
        Post post = new Post();
        post.setContent(mContentET.getText().toString());
        post.setCreatedUserId(mAuth.getCurrentUser().getUid());
        post.setCreatedUserName(mAuth.getCurrentUser().getDisplayName());
        post.setCreatedUserAvatarUrl(mAuth.getCurrentUser().getPhotoUrl().toString());
        post.setCreatedAt(SYCUtils.getCurrentEST());
        post.setImgUrl(mImgUrl);
        PostService postService = new PostService(CreatePostActivity.this);
        postService.setCreatePostListener(CreatePostActivity.this);
        postService.createPost(post);
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    ViewGroup.LayoutParams params = mImgIV.getLayoutParams();
                    params.width = (int) getResources().getDimension(R.dimen.img_dimen);
                    params.height = (int) getResources().getDimension(R.dimen.img_dimen);
                    mImgIV.setLayoutParams(params);
                    mImgUrl = data.getStringArrayListExtra(Define.INTENT_PATH).get(0);
                    Picasso.with(CreatePostActivity.this).load(new File(mImgUrl)).fit().centerCrop().into(mImgIV);
                }
        }
    }

    @Override
    public void createPostSucceed() {
        mMaterialDialog.dismiss();
        Toast.makeText(this, "Create post successfully!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void createPostFail(String errorMsg) {
        mMaterialDialog.dismiss();
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
