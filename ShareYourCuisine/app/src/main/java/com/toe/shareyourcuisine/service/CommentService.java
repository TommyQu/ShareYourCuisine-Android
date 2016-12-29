package com.toe.shareyourcuisine.service;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toe.shareyourcuisine.model.Comment;
import com.toe.shareyourcuisine.model.CommentItem;
import com.toe.shareyourcuisine.model.Recipe;
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
    private GetCommentItemsByParentIdListener mGetCommentItemsByParentIdListener;
    private int mCommentItemCount;

    public CommentService(Context context) {
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mCommentRef = mFirebaseDatabase.getReference("comment");
    }

    public interface CreateCommentListener {
        public void createCommentSucceed();
        public void createCommentFail(String errorMsg);
    }

    public interface GetCommentItemsByParentIdListener {
        public void getCommentItemsByParentIdSucceed(List<CommentItem> commentItems);
        public void getCommentItemsByParentIdFail(String errorMsg);
    }

    public void setCreateCommentListener(CreateCommentListener createCommentListener) {
        mCreateCommentListener = createCommentListener;
    }

    public void setGetCommentItemsByParentIdListener(GetCommentItemsByParentIdListener getCommentItemsByParentIdListener) {
        mGetCommentItemsByParentIdListener = getCommentItemsByParentIdListener;
    }

    public void createComment(String parentId, String content, String userId) {
        Comment comment = new Comment();
        comment.setParentId(parentId);
        comment.setContent(content);
        comment.setCreatedBy(userId);
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

    public void getCommentItemsByParentId(String parentId) {
        mCommentItemCount = 0;
        final DatabaseReference userRef = mFirebaseDatabase.getReference("user");
        mCommentRef.orderByChild("parentId").equalTo(parentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final List<CommentItem> commentItems = new ArrayList<CommentItem>();
                final long totalCommentCount = dataSnapshot.getChildrenCount();
                for(final DataSnapshot commentItemSnapShot: dataSnapshot.getChildren()) {
                    final CommentItem commentItem = commentItemSnapShot.getValue(CommentItem.class);
                    commentItem.setUid(commentItemSnapShot.getKey());
                    userRef.child(commentItem.getCreatedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            commentItem.setCreatedUserName(user.getfName() + " " + user.getlName());
                            commentItem.setCreatedUserAvatarUrl(user.getAvatarUrl());
                            commentItems.add(commentItem);
                            mCommentItemCount++;
                            if(mCommentItemCount == totalCommentCount) {
                                Collections.reverse(commentItems);
                                mGetCommentItemsByParentIdListener.getCommentItemsByParentIdSucceed(commentItems);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            mGetCommentItemsByParentIdListener.getCommentItemsByParentIdFail(databaseError.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetCommentItemsByParentIdListener.getCommentItemsByParentIdFail(databaseError.getMessage());
            }
        });
    }
}
