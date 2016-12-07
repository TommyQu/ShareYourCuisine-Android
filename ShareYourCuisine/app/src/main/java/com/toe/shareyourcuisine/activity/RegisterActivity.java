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
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rengwuxian.materialedittext.validation.METValidator;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.service.UserService;

import java.util.List;

/**
 * Created by HQu on 11/28/2016.
 */

public class RegisterActivity extends AppCompatActivity implements UserService.RegisterListener, Validator.ValidationListener{

    private static final String TAG = "ToeRegisterActivity:";
    @Email
    private MaterialEditText mEmailET;
    @Password(min = 8, scheme = Password.Scheme.ANY)
    private MaterialEditText mPwdET;
    @ConfirmPassword
    private MaterialEditText mRePwdET;
    private Button mSubmitBtn;
    private Validator mValidator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mValidator = new Validator(RegisterActivity.this);
        mValidator.setValidationListener(RegisterActivity.this);

        mEmailET = (MaterialEditText)findViewById(R.id.email_et);
        mPwdET = (MaterialEditText)findViewById(R.id.pwd_et);
        mRePwdET = (MaterialEditText)findViewById(R.id.re_pwd_et);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);
        mEmailET.setError("Invalid email address");
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
    public void registerSucceed() {
        Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void registerFail(String errorMsg) {
        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationSucceeded() {
        UserService userService = new UserService(RegisterActivity.this);
        userService.setRegisterListener((UserService.RegisterListener) RegisterActivity.this);
        userService.register(mEmailET.getText().toString(), mPwdET.getText().toString());
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
}
