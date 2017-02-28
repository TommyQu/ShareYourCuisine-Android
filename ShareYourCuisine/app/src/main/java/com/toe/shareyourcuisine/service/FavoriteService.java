package com.toe.shareyourcuisine.service;

import android.content.Context;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toe.shareyourcuisine.model.Favorite;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HQu on 2/28/2017.
 */

public class FavoriteService {

    private static final String TAG = "ToeFavoriteService:";
    private FirebaseDatabase mFirebaseDatabase;
    private CreateFavoriteListener mCreateFavoriteListener;
    private GetFavoritesByUserIdListener mGetFavoritesByUserIdListener;
    private DeleteFavoriteListener mDeleteFavoriteListener;

    public FavoriteService() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    public interface CreateFavoriteListener {
        public void createFavoriteSucceed(String favoriteId);
        public void createFavoriteFail(String errorMsg);
    }

    public interface DeleteFavoriteListener {
        public void deleteFavoriteSucceed();
        public void deleteFavoriteFail(String errorMsg);
    }

    public interface GetFavoritesByUserIdListener {
        public void getFavoritesByUserIdSucceed(List<Favorite> favorites);
        public void getFavoritesByUserIdFail(String errorMsg);
    }

    public void setCreateFavoriteListener(CreateFavoriteListener createFavoriteListener) {
        mCreateFavoriteListener = createFavoriteListener;
    }

    public void setGetFavoritesByUserIdListener(GetFavoritesByUserIdListener getFavoritesByUserIdListener) {
        mGetFavoritesByUserIdListener = getFavoritesByUserIdListener;
    }

    public void setDeleteFavoriteListener(DeleteFavoriteListener deleteFavoriteListener) {
        mDeleteFavoriteListener = deleteFavoriteListener;
    }

    public void createFavorite(String userId, Recipe recipe) {
        DatabaseReference favoriteRef = mFirebaseDatabase.getReference("favorite");
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setRecipe(recipe);
        favorite.setCreatedAt(SYCUtils.getCurrentEST());
        favoriteRef.push().setValue(favorite, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mCreateFavoriteListener.createFavoriteFail(databaseError.getMessage());
                else
                    mCreateFavoriteListener.createFavoriteSucceed(databaseReference.getKey());
            }
        });
    }

    public void getFavoritesByUserId(String userId) {
        DatabaseReference favoriteRef = mFirebaseDatabase.getReference("favorite");
        favoriteRef.orderByChild("userId").equalTo(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Favorite> favorites = new ArrayList<Favorite>();
                for(DataSnapshot favoriteSnapShot: dataSnapshot.getChildren()) {
                    Favorite favorite = favoriteSnapShot.getValue(Favorite.class);
                    favorite.setUid(favoriteSnapShot.getKey());
                    favorites.add(favorite);
                }
//                Collections.reverse(favorites);
                mGetFavoritesByUserIdListener.getFavoritesByUserIdSucceed(favorites);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetFavoritesByUserIdListener.getFavoritesByUserIdFail(databaseError.getMessage());
            }
        });
    }

    public void deleteFavorite(String favoriteId) {
        DatabaseReference favoriteRef = mFirebaseDatabase.getReference("favorite");
        favoriteRef.child(favoriteId).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mDeleteFavoriteListener.deleteFavoriteFail(databaseError.getMessage());
                else
                    mDeleteFavoriteListener.deleteFavoriteSucceed();
            }
        });
    }
}
