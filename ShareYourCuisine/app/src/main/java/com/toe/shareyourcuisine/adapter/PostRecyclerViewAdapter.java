package com.toe.shareyourcuisine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Post;
import com.toe.shareyourcuisine.model.Recipe;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/27/2016.
 */

public class PostRecyclerViewAdapter extends RecyclerView.Adapter<PostRecyclerViewAdapter.PostViewHolder> {

    private Context mContext;
    private List<Post> mPosts;
    private static PostItemClickListener mPostItemClickListener;

    public PostRecyclerViewAdapter(Context context, List<Post> posts) {
        mContext = context;
        mPosts = posts;
    }

    public interface PostItemClickListener {
        public void onItemClick(int position, View v);
    }

    public void setPostItemClickListener(PostItemClickListener postItemClickListener) {
        mPostItemClickListener = postItemClickListener;
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CircleImageView mAvatarIV;
        public TextView mNameTV;
        public TextView mContentTV;
        public ImageView mImgIV;
        public PostViewHolder(View itemView) {
            super(itemView);
            mAvatarIV = (CircleImageView)itemView.findViewById(R.id.avatar_iv);
            mNameTV = (TextView)itemView.findViewById(R.id.name_tv);
            mContentTV = (TextView)itemView.findViewById(R.id.content_tv);
            mImgIV = (ImageView)itemView.findViewById(R.id.img_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mPostItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        PostViewHolder postViewHolder = new PostViewHolder(view);
        return postViewHolder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {
        Picasso.with(mContext).load(mPosts.get(position).getImgUrl()).fit().centerCrop().into(holder.mImgIV);
        holder.mContentTV.setText(mPosts.get(position).getContent());
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }


}
