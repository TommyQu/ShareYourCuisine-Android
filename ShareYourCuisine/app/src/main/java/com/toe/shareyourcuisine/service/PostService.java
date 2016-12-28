package com.toe.shareyourcuisine.service;

import android.content.Context;
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
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.model.PostItem;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import id.zelory.compressor.Compressor;

/**
 * Created by HQu on 12/27/2016.
 */

public class PostService {

    private static final String TAG = "ToePostService:";
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageRef;
    private StorageReference mImgStorageRef;
    private CreatePostListener mCreatePostListener;
    private GetAllPostItemsListener mGetAllPostItemsListener;
    private Context mContext;
    private Post mPostToCreate;

    public interface CreatePostListener {
        public void createPostSucceed();
        public void createPostFail(String errorMsg);
    }

    public interface GetAllPostItemsListener {
        public void getAllPostItemsSucceed(List<PostItem> postItems);
        public void getAllPostItemsFail(String errorMsg);
    }

    public PostService(Context context) {
        mContext = context;
    }

    public void setCreatePostListener(CreatePostListener createPostListener) {
        mCreatePostListener = createPostListener;
    }

    public void setGetAllPostItemsListener(GetAllPostItemsListener getAllPostItemsListener) {
        mGetAllPostItemsListener = getAllPostItemsListener;
    }

    public void createPost(Post post) {
        mPostToCreate = post;
        if(post.getImgUrl() != null && post.getImgUrl() != "")
            uploadImg(post.getImgUrl());
        else
            insertPostData();
    }

    public void uploadImg(String imgUrl) {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageRef = mFirebaseStorage.getReferenceFromUrl("gs://shareyourcuisine.appspot.com");
        File compressedImg = new Compressor.Builder(mContext).build().compressToFile(new File(imgUrl));
        Uri file = Uri.fromFile(compressedImg);
        mImgStorageRef = mStorageRef.child("images/post/" + mPostToCreate.getCreatedBy() + "/" + UUID.randomUUID().toString());
        UploadTask uploadTask = mImgStorageRef.putFile(file);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                mCreatePostListener.createPostFail(exception.toString());
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                mPostToCreate.setImgUrl(downloadUrl.toString());
                insertPostData();
            }
        });
    }

    public void insertPostData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference postRef = firebaseDatabase.getReference("post");
        postRef.push().setValue(mPostToCreate, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mCreatePostListener.createPostFail(databaseError.getMessage());
                else
                    mCreatePostListener.createPostSucceed();
            }
        });
    }

    public void getAllPostItems() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference postRef = firebaseDatabase.getReference("post");
        final DatabaseReference userRef = firebaseDatabase.getReference("user");
        postRef.orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<PostItem> postItems = new ArrayList<PostItem>();
                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                    final PostItem postItem = postSnapShot.getValue(PostItem.class);
                    postItem.setUid(postSnapShot.getKey());
                    userRef.child(postItem.getCreatedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            postItem.setCreatedUserName(user.getfName() + " " + user.getlName());
                            postItem.setCreatedUserAvatarUrl(user.getAvatarUrl());
                            postItems.add(postItem);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mGetAllPostItemsListener.getAllPostItemsFail(databaseError.getMessage());
                        }
                    });
                }
                mGetAllPostItemsListener.getAllPostItemsSucceed(postItems);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetAllPostItemsListener.getAllPostItemsFail(databaseError.getMessage());
            }
        });
    }
}
