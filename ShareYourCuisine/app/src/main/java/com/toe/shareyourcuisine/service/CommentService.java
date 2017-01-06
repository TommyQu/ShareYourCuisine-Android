package com.toe.shareyourcuisine.service;

import android.content.Context;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toe.shareyourcuisine.model.Comment;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HQu on 12/28/2016.
 */

public class CommentService {

    private static final String TAG = "ToeCommentService:";
    private Context mContext;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mCommentRef;
    private CreateCommentListener mCreateCommentListener;
    private GetCommentsByParentIdListener mGetCommentsByParentIdListener;

    public CommentService(Context context) {
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCommentRef = mFirebaseDatabase.getReference("comment");
    }

    public interface CreateCommentListener {
        public void createCommentSucceed();
        public void createCommentFail(String errorMsg);
    }

    public interface GetCommentsByParentIdListener {
        public void getCommentsByParentIdSucceed(List<Comment> comments);
        public void getCommentsByParentIdFail(String errorMsg);
    }

    public void setCreateCommentListener(CreateCommentListener createCommentListener) {
        mCreateCommentListener = createCommentListener;
    }

    public void setGetCommentsByParentIdListener(GetCommentsByParentIdListener getCommentsByParentIdListener) {
        mGetCommentsByParentIdListener = getCommentsByParentIdListener;
    }

    public void createComment(String parentId, String content, FirebaseUser currentUser) {
        Comment comment = new Comment();
        comment.setParentId(parentId);
        comment.setContent(content);
        comment.setCreatedUserId(currentUser.getUid());
        comment.setCreatedUserName(currentUser.getDisplayName());
        comment.setCreatedUserAvatarUrl(currentUser.getPhotoUrl().toString());
        comment.setCreatedAt(SYCUtils.getCurrentEST());
        mCommentRef.push().setValue(comment, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mCreateCommentListener.createCommentFail(databaseError.getMessage());
                else
                    mCreateCommentListener.createCommentSucceed();
            }
        });
    }

    public void getCommentsByParentId(String parentId) {
        mCommentRef.orderByChild("parentId").equalTo(parentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Comment> comments = new ArrayList<Comment>();
                for(DataSnapshot commentSnapShot: dataSnapshot.getChildren()) {
                    Comment comment = commentSnapShot.getValue(Comment.class);
                    comment.setUid(commentSnapShot.getKey());
                    comments.add(comment);
                }
                Collections.reverse(comments);
                mGetCommentsByParentIdListener.getCommentsByParentIdSucceed(comments);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetCommentsByParentIdListener.getCommentsByParentIdFail(databaseError.getMessage());
            }
        });

    }
}
