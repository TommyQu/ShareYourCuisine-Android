package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.UserProfile;
import com.toe.shareyourcuisine.service.UserService;

/**
 * Created by HQu on 11/23/2016.
 */

public class SignInActivity extends AppCompatActivity implements UserService.SignInListener{

    private static final String TAG = "ToeSignInActivity:";
    private MaterialEditText mEmailET;
    private MaterialEditText mPwdET;
    private Button mRegisterBtn;
    private Button mSubmitBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmailET = (MaterialEditText)findViewById(R.id.email_et);
        mPwdET = (MaterialEditText)findViewById(R.id.pwd_et);
        mRegisterBtn = (Button)findViewById(R.id.register_btn);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = new UserService(SignInActivity.this);
                userService.setSignInListener((UserService.SignInListener) SignInActivity.this);
                userService.signIn(mEmailET.getText().toString(), mPwdET.getText().toString());
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
    public void signInSucceed() {
        Toast.makeText(SignInActivity.this, "Sign in successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void signInFail(String errorMsg) {
        Toast.makeText(SignInActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
