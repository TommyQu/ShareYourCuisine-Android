package com.toe.shareyourcuisine.service;

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

import java.io.File;

/**
 * Created by HQu on 12/5/2016.
 */

public class MenuService {

    private static final String TAG = "ToeMenuService:";
    private StorageReference mImgStorageRef;
    private CreateNewMenuListener mCreateNewMenuListener;

    public interface CreateNewMenuListener {
        public void createNewMenuSucceed();
        public void createNewMenuFail(String errorMsg);
    }

    public void setCreateNewMenuListener(CreateNewMenuListener createNewMenuListener) {
        mCreateNewMenuListener = createNewMenuListener;
    }

    public MenuService() {
    }

    public void createMenu(Menu menu, final String url) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference menuRef = firebaseDatabase.getReference("menu");
        menuRef.push().setValue(menu, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mCreateNewMenuListener.createNewMenuFail(databaseError.getMessage());
                else
                    mCreateNewMenuListener.createNewMenuSucceed();
            }
        });
//        final StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://shareyourcuisine.appspot.com");
//        Uri file = Uri.fromFile(new File(url));
//        mImgStorageRef = storageRef.child("images/" + menu.getCreatedBy() + "/" + file.getLastPathSegment());
//        UploadTask uploadTask = mImgStorageRef.putFile(file);
//        uploadTask.addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                mCreateNewMenuListener.createNewMenuFail(exception.toString());
//            }
//        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
//                Uri downloadUrl = taskSnapshot.getDownloadUrl();
//                Log.i(TAG, downloadUrl.toString());
//            }
//        });

    }
}
