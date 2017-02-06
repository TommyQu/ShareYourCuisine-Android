package com.toe.shareyourcuisine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Attendance;
import com.toe.shareyourcuisine.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/27/2016.
 */

public class AttendanceRecyclerViewAdapter extends RecyclerView.Adapter<AttendanceRecyclerViewAdapter.AttendanceViewHolder> {

    private Context mContext;
    private List<Attendance> mAttendances;

    public AttendanceRecyclerViewAdapter(Context context, List<Attendance> attendances) {
        mContext = context;
        mAttendances = attendances;
    }

    public static class AttendanceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CircleImageView mAvatarIV;
        public AttendanceViewHolder(View itemView) {
            super(itemView);
            mAvatarIV = (CircleImageView)itemView.findViewById(R.id.avatar_iv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

        }
    }

    @Override
    public AttendanceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendant, parent, false);
        AttendanceViewHolder attendanceViewHolder = new AttendanceViewHolder(view);
        return attendanceViewHolder;
    }

    @Override
    public void onBindViewHolder(AttendanceViewHolder holder, int position) {
        Picasso.with(mContext).load(mAttendances.get(position).getUserAvatarUrl()).fit().centerCrop().into(holder.mAvatarIV);
    }

    @Override
    public int getItemCount() {
        return mAttendances.size();
}


}
