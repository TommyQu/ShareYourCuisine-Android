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
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mPostRef;
    private CreatePostListener mCreatePostListener;
    private GetAllPostsListener mGetAllPostsListener;
    private GetPostByIdListener mGetPostByIdListener;
    private LikeOnePostListener mLikeOnePostListener;
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

    public interface GetPostByIdListener {
        public void getPostByIdSucceed(Post post);
        public void getPostByIdFail(String errorMsg);
    }

    public interface LikeOnePostListener {
        public void likeOnePostSucceed();
        public void likeOnePostFail(String errorMsg);
    }

    public PostService(Context context) {
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mPostRef = mFirebaseDatabase.getReference("post");
    }

    public void setCreatePostListener(CreatePostListener createPostListener) {
        mCreatePostListener = createPostListener;
    }

    public void setGetAllPostsListener(GetAllPostsListener getAllPostsListener) {
        mGetAllPostsListener = getAllPostsListener;
    }

    public void setGetPostByIdListener(GetPostByIdListener getPostByIdListener) {
        mGetPostByIdListener = getPostByIdListener;
    }

    public void setLikeOnePostListener(LikeOnePostListener likeOnePostListener) {
        mLikeOnePostListener = likeOnePostListener;
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
        mPostRef.push().setValue(mPostToCreate, new DatabaseReference.CompletionListener() {
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
        mPostRef.orderByChild("createdAt").addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void getPostById(String uid) {
        final DatabaseReference postRef = mFirebaseDatabase.getReference("post");
        postRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                mGetPostByIdListener.getPostByIdSucceed(post);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetPostByIdListener.getPostByIdFail(databaseError.getMessage());
            }
        });
    }

    public void likeOnePost(final String postId, final String userId) {
        mPostRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                if(!post.getLikedBy().contains(userId)) {
                    post.getLikedBy().add(userId);
                    mPostRef.child(postId).child("likedBy").setValue(post.getLikedBy(), new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError != null)
                                mLikeOnePostListener.likeOnePostFail(databaseError.getMessage());
                            else
                                mLikeOnePostListener.likeOnePostSucceed();
                        }
                    });
                } else
                    mLikeOnePostListener.likeOnePostFail("You have already liked this post!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mLikeOnePostListener.likeOnePostFail(databaseError.getMessage());
            }
        });

    }
}
