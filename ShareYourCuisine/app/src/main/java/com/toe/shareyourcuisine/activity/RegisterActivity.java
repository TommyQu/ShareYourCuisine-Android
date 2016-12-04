package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.service.UserService;

/**
 * Created by HQu on 11/28/2016.
 */

public class RegisterActivity extends AppCompatActivity implements UserService.RegisterListener{

    private static final String TAG = "ToeRegisterActivity:";
    private MaterialEditText mEmailET;
    private MaterialEditText mPwdET;
    private MaterialEditText mRePwdET;
    private Button mSubmitBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmailET = (MaterialEditText)findViewById(R.id.email_et);
        mPwdET = (MaterialEditText)findViewById(R.id.pwd_et);
        mRePwdET = (MaterialEditText)findViewById(R.id.re_pwd_et);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);
        mEmailET.setError("Invalid email address");
        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = new UserService(RegisterActivity.this);
                userService.setRegisterListener((UserService.RegisterListener) RegisterActivity.this);
                userService.register(mEmailET.getText().toString(), mPwdET.getText().toString());
            }
        });
        validatePwd();
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

    public void validatePwd() {
        mPwdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equalsIgnoreCase(mRePwdET.getText().toString()))
                    mRePwdET.setError(null);
                else
                    mRePwdET.setError("Password does not match!");
            }
        });
        mRePwdET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().equalsIgnoreCase(mPwdET.getText().toString()))
                    mRePwdET.setError(null);
                else
                    mRePwdET.setError("Password does not match!");
            }
        });
    }

    @Override
    public void registerSucceed() {
        Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void registerFail(String errorMsg) {
        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
