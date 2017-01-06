package com.toe.shareyourcuisine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.CommentItem;
import com.toe.shareyourcuisine.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/27/2016.
 */

public class AttendantRecyclerViewAdapter extends RecyclerView.Adapter<AttendantRecyclerViewAdapter.AttendantViewHolder> {

    private Context mContext;
    private List<User> mAttendants;

    public AttendantRecyclerViewAdapter(Context context, List<User> attendants) {
        mContext = context;
        mAttendants = attendants;
    }

    public static class AttendantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CircleImageView mAvatarIV;
        public TextView mNameTV;
        public AttendantViewHolder(View itemView) {
            super(itemView);
            mAvatarIV = (CircleImageView)itemView.findViewById(R.id.avatar_iv);
            mNameTV = (TextView)itemView.findViewById(R.id.name_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public AttendantViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendant, parent, false);
        AttendantViewHolder attendantViewHolder = new AttendantViewHolder(view);
        return attendantViewHolder;
    }

    @Override
    public void onBindViewHolder(AttendantViewHolder holder, int position) {
        holder.mNameTV.setText(mAttendants.get(position).getfName() + " " + mAttendants.get(position).getlName());
        Picasso.with(mContext).load(mAttendants.get(position).getAvatarUrl()).fit().centerCrop().into(holder.mAvatarIV);
    }

    @Override
    public int getItemCount() {
        return mAttendants.size();
}


}
