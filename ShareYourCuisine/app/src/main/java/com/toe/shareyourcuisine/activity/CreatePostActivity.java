package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.service.PostService;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.List;

/**
 * Created by HQu on 12/27/2016.
 */

public class CreatePostActivity extends BaseActivity implements Validator.ValidationListener {

    @NotEmpty
    private MaterialEditText mContentET;
    private ImageView mImgIV;
    private Button mSubmitBtn;
    private Validator mValidator;
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mValidator = new Validator(CreatePostActivity.this);
        mValidator.setValidationListener(CreatePostActivity.this);

        mContentET = (MaterialEditText)findViewById(R.id.content_et);
        mImgIV = (ImageView)findViewById(R.id.img_iv);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog.Builder(CreatePostActivity.this)
                        .title("Creating post")
                        .content("Please wait")
                        .progress(true, 0)
                        .show();
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
        Post post = new Post();
        post.setContent(mContentET.getText().toString());
        post.setCreatedBy(mAuth.getCurrentUser().getUid());
        post.setCreatedAt(SYCUtils.getCurrentEST());
        PostService postService = new PostService();
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
