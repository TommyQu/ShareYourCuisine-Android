package com.toe.shareyourcuisine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Comment;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/27/2016.
 */

public class CommentRecyclerViewAdapter extends RecyclerView.Adapter<CommentRecyclerViewAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mComments;

    public CommentRecyclerViewAdapter(Context context, List<Comment> comments) {
        mContext = context;
        mComments = comments;
    }


    public static class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CircleImageView mAvatarIV;
        public TextView mNameTV;
        public TextView mContentTV;
        public CommentViewHolder(View itemView) {
            super(itemView);
            mAvatarIV = (CircleImageView)itemView.findViewById(R.id.avatar_iv);
            mNameTV = (TextView)itemView.findViewById(R.id.name_tv);
            mContentTV = (TextView)itemView.findViewById(R.id.content_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        CommentViewHolder commentViewHolder = new CommentViewHolder(view);
        return commentViewHolder;
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        holder.mNameTV.setText(mComments.get(position).getCreatedUserName());
        holder.mContentTV.setText(mComments.get(position).getContent());
        Picasso.with(mContext).load(mComments.get(position).getCreatedUserAvatarUrl()).fit().centerCrop().into(holder.mAvatarIV);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }


}
