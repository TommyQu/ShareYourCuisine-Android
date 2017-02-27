package com.toe.shareyourcuisine.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.toe.shareyourcuisine.model.Comment;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRecipeRef;
    private CreateNewRecipeListener mCreateNewRecipeListener;
    private GetAllRecipesListener mGetAllRecipesListener;
    private RateRecipeListener mRateRecipeListener;
    private GetRecipesByNameListener mGetRecipesByNameListener;
    public DeleteRecipeListener mDeleteRecipeListener;
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

    public interface RateRecipeListener {
        public void rateRecipeSucceed();
        public void rateRecipeFail(String errorMsg);
    }

    public interface GetRecipesByNameListener {
        public void getRecipesByNameSucceed(List<Recipe> recipes);
        public void getRecipesByNameFail(String errorMsg);
    }

    public interface DeleteRecipeListener {
        public void deleteRecipeSucceed();
        public void deleteRecipeFail(String errorMsg);
    }

    public void setCreateNewRecipeListener(CreateNewRecipeListener createNewRecipeListener) {
        mCreateNewRecipeListener = createNewRecipeListener;
    }

    public void setGetAllRecipesListener(GetAllRecipesListener getAllRecipesListener) {
        mGetAllRecipesListener = getAllRecipesListener;
    }

    public void setRateRecipeListener(RateRecipeListener rateRecipeListener) {
        mRateRecipeListener = rateRecipeListener;
    }

    public void setGetRecipesByNameListener(GetRecipesByNameListener getRecipesByNameListener) {
        mGetRecipesByNameListener = getRecipesByNameListener;
    }

    public void setDeleteRecipeListener(DeleteRecipeListener deleteRecipeListener) {
        mDeleteRecipeListener = deleteRecipeListener;
    }

    public RecipeService(Context context) {
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRecipeRef = mFirebaseDatabase.getReference("recipe");
    }

    public void getAllRecipes() {

        mRecipeRef.orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Recipe> recipes = new ArrayList<Recipe>();
                for(DataSnapshot recipeSnapShot: dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapShot.getValue(Recipe.class);
                    recipe.setUid(recipeSnapShot.getKey());
                    recipes.add(recipe);
                }
                Collections.reverse(recipes);
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
        uploadDisplayImg(recipe.getDisplayImgUrl(), recipe.getCreatedUserId());
        uploadContentImgs(contentImgUrls, recipe.getCreatedUserId());
    }

    public void uploadDisplayImg(String displayImgUrl, String uid) {
        File compressedImg = new Compressor.Builder(mContext).build().compressToFile(new File(displayImgUrl));
        Uri file = Uri.fromFile(compressedImg);
        mImgStorageRef = mStorageRef.child("images/recipe/" + uid + "/" + UUID.randomUUID().toString());
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
            mImgStorageRef = mStorageRef.child("images/recipe/" + uid + "/" + UUID.randomUUID().toString());
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
                        insertRecipeData();
                }
            });
        }
    }

    public void insertRecipeData() {
        mRecipeRef.push().setValue(mRecipeToCreate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mCreateNewRecipeListener.createNewRecipeFail(databaseError.getMessage());
                else
                    mCreateNewRecipeListener.createNewRecipeSucceed();
            }
        });
    }

    public void rateRecipe(final String recipeId, final String userId, final float newRate) {
//        Get current recipe rated users and rate value
        mRecipeRef.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                recipe.getRatedBy().add(userId);
                recipe.setTotalRates(recipe.getTotalRates()+newRate);
                mRecipeRef.child(recipeId).child("ratedBy").setValue(recipe.getRatedBy());
                mRecipeRef.child(recipeId).child("totalRates").setValue(recipe.getTotalRates()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mRateRecipeListener.rateRecipeSucceed();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mRateRecipeListener.rateRecipeFail(databaseError.getMessage());
            }
        });

    }

    public void getRecipesByName(String name) {
        mRecipeRef.orderByChild("title").startAt(name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Recipe> recipes = new ArrayList<Recipe>();
                for(DataSnapshot recipeSnapShot: dataSnapshot.getChildren()) {
                    Recipe recipe = recipeSnapShot.getValue(Recipe.class);
                    recipe.setUid(recipeSnapShot.getKey());
                    recipes.add(recipe);
                }
                Collections.reverse(recipes);
                mGetRecipesByNameListener.getRecipesByNameSucceed(recipes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetRecipesByNameListener.getRecipesByNameFail(databaseError.getMessage());
            }
        });
    }

    public void deleteRecipe(final String uid) {
        mRecipeRef.child(uid).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mDeleteRecipeListener.deleteRecipeFail(databaseError.getMessage());
                else
                    deleteRecipeImgs(uid);
            }
        });
    }

    //Todo: Delete all recipe images
    public void deleteRecipeImgs(final String uid) {
        mStorageRef.child("images/recipe/" + uid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                deleteRecipeComment(uid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mDeleteRecipeListener.deleteRecipeFail(e.getMessage());
            }
        });
    }

    public void deleteRecipeComment(String uid) {
        DatabaseReference commentRef = mFirebaseDatabase.getReference("comment");
        commentRef.orderByChild("parentId").equalTo(uid).removeEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
