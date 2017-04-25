package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.CommentRecyclerViewAdapter;
import com.toe.shareyourcuisine.adapter.PostRecyclerViewAdapter;
import com.toe.shareyourcuisine.adapter.RecipeRecyclerViewAdapter;
import com.toe.shareyourcuisine.fragment.PostFragment;
import com.toe.shareyourcuisine.model.Comment;
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.service.CommentService;
import com.toe.shareyourcuisine.service.PostService;
import com.toe.shareyourcuisine.service.RecipeService;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Song on 4/19/2017.
 */

public class OnePostActivity extends BaseActivity implements PostService.GetPostByIdListener, PostService.LikeOnePostListener, PostRecyclerViewAdapter.PostLikeClickListener, CommentService.CreateCommentListener, CommentService.GetCommentsByParentIdListener{
    private RecyclerView mCommentsRV;
    private CommentRecyclerViewAdapter mCommentAdapter;
    public CircleImageView mAvatarIV;
    public TextView mNameTV;
    public TextView mContentTV;
    public ImageView mImgIV;
    public TextView mCreatedAtTV;
    public Button mLikeBtn;
    public Button mCommentBtn;
    private MaterialDialog mProgressDialog;
    private String mPostId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_one_post);
        mPostId = getIntent().getStringExtra("postId");
        mCommentsRV = (RecyclerView)findViewById(R.id.comment_rv);
        mCommentsRV.setLayoutManager(new LinearLayoutManager(OnePostActivity.this));

        mAvatarIV = (CircleImageView)findViewById(R.id.avatar_iv);
        mNameTV = (TextView)findViewById(R.id.name_tv);
        mContentTV = (TextView)findViewById(R.id.content_tv);
        mImgIV = (ImageView)findViewById(R.id.img_iv);
        mCreatedAtTV = (TextView)findViewById(R.id.createdAt_tv);
        mLikeBtn = (Button)findViewById(R.id.like_btn);
        mCommentBtn = (Button)findViewById(R.id.comment_btn);


        //TODO Set the button click listeners
        mLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLikeBtnClick(mPostId);
            }
        });
        mCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (mAuth.getCurrentUser() != null) {
                 MaterialDialog dialog = new MaterialDialog.Builder(OnePostActivity.this)
                         .title("Comment")
                         .customView(R.layout.dialog_comment, true)
                         .positiveText("Confirm")
                         .negativeText(android.R.string.cancel)
                         .onPositive(new MaterialDialog.SingleButtonCallback() {
                             @Override
                             public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                 MaterialEditText contentET = (MaterialEditText) dialog.getCustomView().findViewById(R.id.content_et);
                                 if (contentET.getText() != null && contentET.getText().toString().length() >= 10 && contentET.getText().toString().length() <= 400) {
                                     mProgressDialog = new MaterialDialog.Builder(OnePostActivity.this)
                                             .title("Submitting")
                                             .content("Please wait")
                                             .progress(true, 0)
                                             .show();
                                     CommentService commentService = new CommentService(OnePostActivity.this);
                                     commentService.setCreateCommentListener(OnePostActivity.this);
                                     commentService.createComment(mPostId, contentET.getText().toString(), mAuth.getCurrentUser());
                                 } else Toast.makeText(OnePostActivity.this, "Comment length should betweent 10 and 400!", Toast.LENGTH_LONG).show();
                             }
                         }).build();
                 dialog.show();
             } else Toast.makeText(OnePostActivity.this, "Please log in!", Toast.LENGTH_SHORT).show();
            }
        });

        //Get the comments by postId
        CommentService commentService = new CommentService(OnePostActivity.this);
        commentService.setGetCommentsByParentIdListener(OnePostActivity.this);
        commentService.getCommentsByParentId(mPostId);

        //Get the post by id
        PostService postService = new PostService(OnePostActivity.this);
        postService.setGetPostByIdListener(OnePostActivity.this);
        postService.getPostById(mPostId);
    }

    @Override
    public void getPostByIdSucceed(Post post) {
        //Initialize the view according to the post data
        String postImgUrl = post.getImgUrl();
        if (postImgUrl != null && postImgUrl != "") {
            mImgIV.getLayoutParams().width = (int) OnePostActivity.this.getResources().getDimension(R.dimen.img_dimen);
            mImgIV.getLayoutParams().height = (int) OnePostActivity.this.getResources().getDimension(R.dimen.img_dimen);
            Picasso.with(OnePostActivity.this).load(postImgUrl).fit().centerCrop().into(mImgIV);
        }
        //Avatar ImageView
        Picasso.with(OnePostActivity.this).load(post.getCreatedUserAvatarUrl()).fit().centerCrop().into(mAvatarIV);
        //Name TextView
        mNameTV.setText(post.getCreatedUserName());
        //Content TextView
        mContentTV.setText(post.getContent());
        //CreatedAt TextView
        //Convert time stamp to local date time
        mCreatedAtTV.setText(SYCUtils.convertMillisecondsToDateTime(post.getCreatedAt()));

        //Get comments according to the post id
    }

    @Override
    public void getPostByIdFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void likeOnePostSucceed() {
        Toast.makeText(this, "Like succeed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void likeOnePostFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLikeBtnClick(String postId) {
        PostService postService = new PostService(OnePostActivity.this);
        postService.setLikeOnePostListener(OnePostActivity.this);
        postService.likeOnePost(mPostId, mAuth.getCurrentUser().getUid());
    }

    @Override
    public void createCommentSucceed() {
        mProgressDialog.dismiss();
        Toast.makeText(this, "Create comment successfully!", Toast.LENGTH_LONG).show();
        CommentService commentService = new CommentService(OnePostActivity.this);
        commentService.setGetCommentsByParentIdListener(OnePostActivity.this);
        commentService.getCommentsByParentId(mPostId);
    }

    @Override
    public void createCommentFail(String errorMsg) {
        mProgressDialog.dismiss();
        Toast.makeText(this,  errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getCommentsByParentIdSucceed(List<Comment> comments) {
        mCommentAdapter = new CommentRecyclerViewAdapter(OnePostActivity.this, comments);
        mCommentsRV.setAdapter(mCommentAdapter);
    }

    @Override
    public void getCommentsByParentIdFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
