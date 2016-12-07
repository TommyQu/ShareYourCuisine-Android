package com.toe.shareyourcuisine.service;

import android.content.Context;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.toe.shareyourcuisine.model.Menu;
import com.toe.shareyourcuisine.model.UserProfile;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;

/**
 * Created by HQu on 12/5/2016.
 */

public class MenuService {

    private static final String TAG = "ToeMenuService:";
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;
    private StorageReference mImgStorageRef;
    private CreateNewMenuListener mCreateNewMenuListener;
    private Menu mMenuToCreate;
    private Context mContext;

    public interface CreateNewMenuListener {
        public void createNewMenuSucceed();
        public void createNewMenuFail(String errorMsg);
    }

    public void setCreateNewMenuListener(CreateNewMenuListener createNewMenuListener) {
        mCreateNewMenuListener = createNewMenuListener;
    }

    public MenuService(Context context) {
        mContext = context;
    }

    public void createMenu(Menu menu, final ArrayList<String> contentImgUrls) {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReferenceFromUrl("gs://shareyourcuisine.appspot.com");
        mMenuToCreate = menu;
        uploadDisplayImg(menu.getDisplayImgUrl(), menu.getCreatedBy());
        uploadContentImgs(contentImgUrls, menu.getCreatedBy());
    }

    public void uploadDisplayImg(String displayImgUrl, String uid) {
        Uri file = Uri.fromFile(new File(displayImgUrl));
        mImgStorageRef = mStorageRef.child("images/" + uid + "/" + file.getLastPathSegment());
        UploadTask uploadTask = mImgStorageRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mCreateNewMenuListener.createNewMenuFail(exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mMenuToCreate.setDisplayImgUrl(downloadUrl.toString());
            }
        });
    }

    public void uploadContentImgs(final ArrayList<String> contentImgUrls, String uid) {
        final int imgCount = contentImgUrls.size();
        for(int i = 0; i < contentImgUrls.size(); i++) {
            final int finalIndex = i;
            Uri file = Uri.fromFile(new File(contentImgUrls.get(i)));
            mImgStorageRef = mStorageRef.child("images/" + uid + "/" + file.getLastPathSegment());
            UploadTask uploadTask = mImgStorageRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mCreateNewMenuListener.createNewMenuFail(exception.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mMenuToCreate.getContentImgUrls().add(downloadUrl.toString());
                    if(finalIndex == imgCount - 1)
                        insertMenuData();
                }
            });
        }
    }

    public void insertMenuData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference menuRef = firebaseDatabase.getReference("menu");
        menuRef.push().setValue(mMenuToCreate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mCreateNewMenuListener.createNewMenuFail(databaseError.getMessage());
                else
                    mCreateNewMenuListener.createNewMenuSucceed();
            }
        });
    }
}
