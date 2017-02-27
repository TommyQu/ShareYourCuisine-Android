package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.service.UserService;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 11/29/2016.
 */

public class ProfileActivity extends BaseActivity implements UserService.GetUserInfoByEmailListener, UserService.UpdateProfileListener {

    private static final String TAG = "ToeProfileActivity:";
    private CircleImageView mAvatarCIV;
    private MaterialEditText mEmailET;
    private MaterialEditText mFNameET;
    private MaterialEditText mLNameET;
    private MaterialEditText mDobET;
    private MaterialEditText mBioET;
    private Button mSaveChangesBtn;
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAvatarCIV = (CircleImageView)findViewById(R.id.avatar_civ);
        mEmailET = (MaterialEditText)findViewById(R.id.email_et);
        mFNameET = (MaterialEditText)findViewById(R.id.fname_et);
        mLNameET = (MaterialEditText)findViewById(R.id.lname_et);
        mDobET = (MaterialEditText)findViewById(R.id.dob_et);
        mBioET = (MaterialEditText)findViewById(R.id.bio_et);
        mSaveChangesBtn = (Button)findViewById(R.id.save_changes_btn);

        mMaterialDialog = new MaterialDialog.Builder(ProfileActivity.this)
                .title("Loading data")
                .content("Please wait")
                .progress(true, 0)
                .show();
        UserService userService = new UserService(ProfileActivity.this);
        userService.setGetUserInfoByEmailListener(ProfileActivity.this);
        userService.getUserInfoByEmailListener(mAuth.getCurrentUser().getEmail());

        mSaveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog = new MaterialDialog.Builder(ProfileActivity.this)
                        .title("Updating profile")
                        .content("Please wait")
                        .progress(true, 0)
                        .show();
                UserService userService = new UserService(ProfileActivity.this);
                userService.setUpdateProfileListener(ProfileActivity.this);
                userService.updateProfile(mBioET.getText().toString());
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
    public void getUserInfoByEmailSucceed(User user) {
        Picasso.with(ProfileActivity.this).load(user.getAvatarUrl()).fit().centerCrop().into(mAvatarCIV);
        mEmailET.setText(user.getEmail());
        mFNameET.setText(user.getfName());
        mLNameET.setText(user.getlName());
        mDobET.setText(SYCUtils.convertMillisecondsToDateTime(user.getDob()));
        mBioET.setText(user.getBio());
        mMaterialDialog.dismiss();
    }

    @Override
    public void getUserInfoByEmailFail(String errorMsg) {
        mMaterialDialog.dismiss();
        Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateProfileSucceed() {
        mMaterialDialog.dismiss();
        Toast.makeText(ProfileActivity.this, "Update profile successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void updateProfileFail(String errorMsg) {
        mMaterialDialog.dismiss();
        Toast.makeText(ProfileActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
