package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.CommentRecyclerViewAdapter;
import com.toe.shareyourcuisine.adapter.RecipeRecyclerViewAdapter;
import com.toe.shareyourcuisine.fragment.PostFragment;
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.service.PostService;
import com.toe.shareyourcuisine.service.RecipeService;
import com.toe.shareyourcuisine.utils.SYCUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Song on 4/19/2017.
 */

public class OnePostActivity extends BaseActivity implements PostService.GetPostByIdListener,{
    private RecyclerView mPostRV;
    private CommentRecyclerViewAdapter mAdapter;
    public CircleImageView mAvatarIV;
    public TextView mNameTV;
    public TextView mContentTV;
    public ImageView mImgIV;
    public TextView mCreatedAtTV;
    public Button mLikeBtn;
    public Button mCommentBtn;
    private String mPostId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_post);
        mPostId = getIntent().getStringExtra("postId");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mPostRV = (RecyclerView)findViewById(R.id.comment_rv);
        mPostRV.setLayoutManager(new LinearLayoutManager(OnePostActivity.this));

        mAvatarIV = (CircleImageView)findViewById(R.id.avatar_iv);
        mNameTV = (TextView)findViewById(R.id.name_tv);
        mContentTV = (TextView)findViewById(R.id.content_tv);
        mImgIV = (ImageView)findViewById(R.id.img_iv);
        mCreatedAtTV = (TextView)findViewById(R.id.createdAt_tv);
        mLikeBtn = (Button)findViewById(R.id.like_btn);
        mCommentBtn = (Button)findViewById(R.id.comment_btn);


        PostService postService = new PostService(OnePostActivity.this);
        //postService.setLikeOnePostListener(PostFragment.this);
        postService.getPostById(mPostId);
    }

    @Override
    public void getPostByIdSucceed(Post post) {
        //Todo initialize the view according to the post data 
//        Picasso.with(this).load(event.getDisplayImgUrl()).fit().centerCrop().into(mDisplayImgIV);
//        mTitleTV.setText(event.getTitle());
//        mTimeTV.setText(SYCUtils.convertMillisecondsToDateTime(event.getStartTime()) + " ~ " + SYCUtils.convertMillisecondsToDateTime(event.getEndTime()));
//        mLocationTV.setText(event.getLocation());
//        mDescTV.setText(event.getDesc());
    }

    @Override
    public void getPostByIdFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
