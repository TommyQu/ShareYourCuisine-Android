package com.toe.shareyourcuisine.service;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

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
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.model.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
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
    private GetAllPostsListener mGetAllPostsListener;
    private Context mContext;
    private Post mPostToCreate;

    public interface CreatePostListener {
        public void createPostSucceed();
        public void createPostFail(String errorMsg);
    }

    public interface GetAllPostsListener {
        public void getAllPostsSucceed(List<Post> posts);
        public void getAllPostsFail(String errorMsg);
    }

    public PostService(Context context) {
        mContext = context;
    }

    public void setCreatePostListener(CreatePostListener createPostListener) {
        mCreatePostListener = createPostListener;
    }

    public void setGetAllPostsListener(GetAllPostsListener getAllPostsListener) {
        mGetAllPostsListener = getAllPostsListener;
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
        mImgStorageRef = mStorageRef.child("images/post/" + mPostToCreate.getCreatedUserId() + "/" + UUID.randomUUID().toString());
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

    public void getAllPosts() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference postRef = firebaseDatabase.getReference("post");
        postRef.orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Post> posts = new ArrayList<Post>();
                for(DataSnapshot postSnapShot: dataSnapshot.getChildren()) {
                    Post post = postSnapShot.getValue(Post.class);
                    post.setUid(postSnapShot.getKey());
                    posts.add(post);
                }
                Collections.reverse(posts);
                mGetAllPostsListener.getAllPostsSucceed(posts);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetAllPostsListener.getAllPostsFail(databaseError.getMessage());
            }
        });

    }
}
