package com.toe.shareyourcuisine.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;

/**
 * Created by HQu on 12/5/2016.
 */

public class RecipeService {

    private static final String TAG = "ToeRecipeService:";
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;
    private StorageReference mImgStorageRef;
    private CreateNewRecipeListener mCreateNewRecipeListener;
    private GetAllRecipesListener mGetAllRecipesListener;
    private Recipe mRecipeToCreate;
    private Context mContext;

    public interface CreateNewRecipeListener {
        public void createNewRecipeSucceed();
        public void createNewRecipeFail(String errorMsg);
    }

    public interface GetAllRecipesListener {
        public void getAllRecipesSucceed(List<Recipe> recipes);
        public void getAllRecipesFail(String errorMsg);
    }

    public void setCreateNewRecipeListener(CreateNewRecipeListener createNewRecipeListener) {
        mCreateNewRecipeListener = createNewRecipeListener;
    }

    public void setGetAllRecipesListener(GetAllRecipesListener getAllRecipesListener) {
        mGetAllRecipesListener = getAllRecipesListener;
    }

    public RecipeService(Context context) {
        mContext = context;
    }

    public void getAllRecipes() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = firebaseDatabase.getReference("recipe");
        recipeRef.orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Recipe> recipes = new ArrayList<Recipe>();
                for(DataSnapshot recipeSnapShot: dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapShot.getValue(Recipe.class);
                    recipe.setUid(recipeSnapShot.getKey());
                    recipes.add(recipe);
                }
                mGetAllRecipesListener.getAllRecipesSucceed(recipes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetAllRecipesListener.getAllRecipesFail(databaseError.getMessage());
            }
        });
    }

    public void createRecipe(Recipe recipe, final ArrayList<String> contentImgUrls) {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReferenceFromUrl("gs://shareyourcuisine.appspot.com");
        mRecipeToCreate = recipe;
        uploadDisplayImg(recipe.getDisplayImgUrl(), recipe.getCreatedBy());
        uploadContentImgs(contentImgUrls, recipe.getCreatedBy());
    }

    public void uploadDisplayImg(String displayImgUrl, String uid) {
        File compressedImg = new Compressor.Builder(mContext).build().compressToFile(new File(displayImgUrl));
        Uri file = Uri.fromFile(compressedImg);
        mImgStorageRef = mStorageRef.child("images/" + uid + "/" + UUID.randomUUID().toString());
        UploadTask uploadTask = mImgStorageRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mCreateNewRecipeListener.createNewRecipeFail(exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mRecipeToCreate.setDisplayImgUrl(downloadUrl.toString());
            }
        });
    }

    public void uploadContentImgs(final ArrayList<String> contentImgUrls, String uid) {
        final int imgCount = contentImgUrls.size();
        for(int i = 0; i < contentImgUrls.size(); i++) {
            final int finalIndex = i;
            File compressedImg = new Compressor.Builder(mContext).build().compressToFile(new File(contentImgUrls.get(i)));
            Uri file = Uri.fromFile(compressedImg);
            mImgStorageRef = mStorageRef.child("images/" + uid + "/" + UUID.randomUUID().toString());
            UploadTask uploadTask = mImgStorageRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    mCreateNewRecipeListener.createNewRecipeFail(exception.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    mRecipeToCreate.getContentImgUrls().add(downloadUrl.toString());
                    if(finalIndex == imgCount - 1)
                        insertMenuData();
                }
            });
        }
    }

    public void insertMenuData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference recipeRef = firebaseDatabase.getReference("recipe");
        recipeRef.push().setValue(mRecipeToCreate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mCreateNewRecipeListener.createNewRecipeFail(databaseError.getMessage());
                else
                    mCreateNewRecipeListener.createNewRecipeSucceed();
            }
        });
    }
}