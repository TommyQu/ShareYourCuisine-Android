package com.toe.shareyourcuisine.service;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.orm.SugarRecord;
import com.toe.shareyourcuisine.model.UserProfile;

/**
 * Created by HQu on 11/30/2016.
 */

public class UserService {

    private FirebaseAuth mAuth;
    private Context mContext;
    private SignInListener mSignInListener;
    private RegisterListener mRegisterListener;

    public interface SignInListener {
        public void signInSucceed();
        public void signInFail(String errorMsg);
    }

    public interface RegisterListener {
        public void registerSucceed();
        public void registerFail(String errorMsg);
    }

    public UserService(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
    }

    public void setSignInListener(SignInListener signInListener) {
        mSignInListener = signInListener;
    }

    public void setRegisterListener(RegisterListener registerListener) {
        mRegisterListener = registerListener;
    }

    public void signIn(String email, String pwd) {
        mAuth.signInWithEmailAndPassword(email, pwd.toString())
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserProfile userProfile = new UserProfile();
                            userProfile.setEmail(task.getResult().getUser().getEmail());
                            userProfile.setUid(task.getResult().getUser().getUid());
                            userProfile.save();
                            mSignInListener.signInSucceed();
                        } else {
                            mSignInListener.signInFail(task.getException().getMessage());
                        }
                    }
                });
    }

    public void register(String email, String pwd) {
        mAuth.createUserWithEmailAndPassword(email, pwd).
                addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mRegisterListener.registerSucceed();
                        } else {
                            mRegisterListener.registerFail(task.getException().getMessage());
                        }
                    }
                });
    }
}
