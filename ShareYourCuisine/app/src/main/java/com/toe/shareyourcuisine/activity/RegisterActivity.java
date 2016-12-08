package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.define.Define;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.service.UserService;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 11/28/2016.
 */

public class RegisterActivity extends AppCompatActivity implements UserService.RegisterListener, Validator.ValidationListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ToeRegisterActivity:";
    @Email
    private MaterialEditText mEmailET;
    @Password(min = 8, scheme = Password.Scheme.ANY)
    private MaterialEditText mPwdET;
    @ConfirmPassword
    private MaterialEditText mRePwdET;
    @NotEmpty
    private MaterialEditText mFNameET;
    @NotEmpty
    private MaterialEditText mLNameET;
    @NotEmpty
    private MaterialEditText mDobET;
    @NotEmpty
    private MaterialEditText mBioET;
    private CircleImageView mAvatarCIV;
    private Button mSelectAvatarBtn;
    private Button mSubmitBtn;
    private Validator mValidator;
    private Long mDob;
    private User mUser;
    private MaterialDialog mMaterialDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mValidator = new Validator(RegisterActivity.this);
        mValidator.setValidationListener(RegisterActivity.this);
        mUser = new User();
        mEmailET = (MaterialEditText)findViewById(R.id.email_et);
        mPwdET = (MaterialEditText)findViewById(R.id.pwd_et);
        mRePwdET = (MaterialEditText)findViewById(R.id.re_pwd_et);
        mFNameET = (MaterialEditText)findViewById(R.id.fname_et);
        mLNameET = (MaterialEditText)findViewById(R.id.lname_et);
        mDobET = (MaterialEditText)findViewById(R.id.dob_et);
        mBioET = (MaterialEditText)findViewById(R.id.bio_et);
        mAvatarCIV = (CircleImageView) findViewById(R.id.avatar_civ);
        mSelectAvatarBtn = (Button)findViewById(R.id.select_avatar_btn);
        mSubmitBtn = (Button)findViewById(R.id.submit_btn);
        mDobET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                        RegisterActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show(getFragmentManager(), "DatePickerDialog");
            }
        });
        mSelectAvatarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FishBun.with(RegisterActivity.this)
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    mUser.setAvatarUrl(data.getStringArrayListExtra(Define.INTENT_PATH).get(0));
                    mAvatarCIV.getLayoutParams().width = (int) getResources().getDimension(R.dimen.avatar_dimen);
                    mAvatarCIV.getLayoutParams().height = (int) getResources().getDimension(R.dimen.avatar_dimen);
                    Picasso.with(RegisterActivity.this).load(new File(mUser.getAvatarUrl())).fit().centerCrop().into(mAvatarCIV);
                }
        }
    }

    @Override
    public void registerSucceed() {
        mMaterialDialog.dismiss();
        Toast.makeText(RegisterActivity.this, "Register successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void registerFail(String errorMsg) {
        mMaterialDialog.dismiss();
        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onValidationSucceeded() {
        mMaterialDialog = new MaterialDialog.Builder(RegisterActivity.this)
                .title("Register")
                .content("Please wait")
                .progress(true, 0)
                .show();
        UserService userService = new UserService(RegisterActivity.this);
        userService.setRegisterListener((UserService.RegisterListener) RegisterActivity.this);
        mUser.setEmail(mEmailET.getText().toString());
        mUser.setfName(mFNameET.getText().toString());
        mUser.setlName(mLNameET.getText().toString());
        mUser.setDob(mDob);
        mUser.setBio(mBioET.getText().toString());
        userService.register(mUser, mPwdET.getText().toString());
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        mDob = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTimeInMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
        mDobET.setText(simpleDateFormat.format(mDob));
    }
}
