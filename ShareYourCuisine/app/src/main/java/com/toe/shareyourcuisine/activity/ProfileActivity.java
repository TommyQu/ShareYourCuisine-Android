package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.rengwuxian.materialedittext.MaterialEditText;
import com.toe.shareyourcuisine.R;

/**
 * Created by HQu on 11/29/2016.
 */

public class ProfileActivity extends BaseActivity {

    private MaterialEditText mEmailET;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mEmailET = (MaterialEditText)findViewById(R.id.email_et);
//        UserProfile userProfile = UserProfile.findById(UserProfile.class, "tommyqu1992@gmail.com");
    }
}
