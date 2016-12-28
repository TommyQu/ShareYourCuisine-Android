package com.toe.shareyourcuisine.service;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orm.SugarRecord;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.utils.Constants;

import java.io.File;

import id.zelory.compressor.Compressor;

/**
 * Created by HQu on 11/30/2016.
 */

public class UserService {

    private static final String TAG = "ToeUserService:";
    private FirebaseAuth mAuth;
    private Context mContext;
    private SignInListener mSignInListener;
    private RegisterListener mRegisterListener;
    private GetUserInfoListener mGetUserInfoListener;
    private User mUserToRegister;

    public interface SignInListener {
        public void signInSucceed();
        public void signInFail(String errorMsg);
    }

    public interface RegisterListener {
        public void registerSucceed();
        public void registerFail(String errorMsg);
    }

    public interface GetUserInfoListener {
        public void getUserInfoSucceed(User user);
        public void getUserInfoFail(String errorMsg);
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

    public void setUserInfoListener(GetUserInfoListener getUserInfoListener) {
        mGetUserInfoListener = getUserInfoListener;
    }

    public void signIn(final String email, String pwd) {
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            mSignInListener.signInFail(task.getException().getMessage());
                        } else {
                            getUserInfo(task.getResult().getUser().getUid(), Constants.ACTION_SIGN_IN);
                        }
                    }
                });
    }

    public void getUserInfo(String uid, String action) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference("user");
        if(action.equalsIgnoreCase(Constants.ACTION_SIGN_IN)) {
            userRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot userSnapShot: dataSnapshot.getChildren()) {
                        User user = userSnapShot.getValue(User.class);
                        user.save();
                    }
                    mSignInListener.signInSucceed();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mSignInListener.signInFail(databaseError.getMessage());
                }
            });
        } else if(action.equalsIgnoreCase(Constants.ACTION_GET_USER_PROFILE)){
            userRef.orderByChild("uid").equalTo(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = new User();
                    for(DataSnapshot userSnapShot: dataSnapshot.getChildren()) {
                        user = userSnapShot.getValue(User.class);
                    }
                    mGetUserInfoListener.getUserInfoSucceed(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mGetUserInfoListener.getUserInfoFail(databaseError.getMessage());
                }
            });
        }

    }

    public void register(User user, String pwd) {
        mUserToRegister = user;
        mAuth.createUserWithEmailAndPassword(user.getEmail(), pwd).
                addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            mRegisterListener.registerFail(task.getException().getMessage());
                        } else {
                            mUserToRegister.setUid(task.getResult().getUser().getUid());
                            uploadAvatar();
                        }
                    }
                });
    }

    public void uploadAvatar() {
        File compressedImg = new Compressor.Builder(mContext).setCompressFormat(Bitmap.CompressFormat.PNG).build().compressToFile(new File(mUserToRegister.getAvatarUrl()));
        Uri file = Uri.fromFile(compressedImg);
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference avatarRef = firebaseStorage.getReferenceFromUrl("gs://shareyourcuisine.appspot.com").child("avatars/" + mUserToRegister.getUid() + "/" + file.getLastPathSegment());
        UploadTask uploadTask = avatarRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mRegisterListener.registerFail(exception.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mUserToRegister.setAvatarUrl(downloadUrl.toString());
                createUser();
            }
        });
    }

//    Add user detail information to user table
    public void createUser() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRef = firebaseDatabase.getReference("user");
        userRef.child(mUserToRegister.getUid()).setValue(mUserToRegister, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mRegisterListener.registerFail(databaseError.getMessage());
                else {
                    mAuth.signOut();
                    mRegisterListener.registerSucceed();
                }
            }
        });
    }
}
