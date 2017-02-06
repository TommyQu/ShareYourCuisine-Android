package com.toe.shareyourcuisine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.activity.MessageActivity;
import com.toe.shareyourcuisine.model.Attendance;
import com.toe.shareyourcuisine.model.AttendanceMsg;
import com.toe.shareyourcuisine.service.AttendanceService;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/27/2016.
 */

public class AttendanceMsgRecyclerViewAdapter extends RecyclerView.Adapter<AttendanceMsgRecyclerViewAdapter.AttendanceMsgViewHolder> {

    private static final String TAG = "ToeAttMsgRecAdapter:";
    private Context mContext;
    private List<AttendanceMsg> mAttendanceMsgs;
    private ApproveListener mApproveListener;
    private RejectListener mRejectListener;

    public AttendanceMsgRecyclerViewAdapter(Context context, List<AttendanceMsg> attendanceMsgs) {
        mContext = context;
        mAttendanceMsgs = attendanceMsgs;
    }

    public interface ApproveListener {
        public void executeApproveAction(String attendanceId, int position);
    }

    public interface RejectListener {
        public void executeRejectAction(String attendanceId, int position);
    }

    public void setApproveListener(ApproveListener approveListener) {
        mApproveListener = approveListener;
    }

    public void setRejectListener(RejectListener rejectListener) {
        mRejectListener = rejectListener;
    }

    public static class AttendanceMsgViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public CircleImageView mAvatarIV;
        public TextView mMsgTV;
        public ImageView mApproveIV;
        public ImageView mRejectIV;
        public SwipeLayout mSwipeLayout;

        public AttendanceMsgViewHolder(final View itemView) {
            super(itemView);
            mSwipeLayout = (SwipeLayout)itemView.findViewById(R.id.swipe_layout);
            mSwipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
            mSwipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.bottom_wrapper));

            mAvatarIV = (CircleImageView)itemView.findViewById(R.id.avatar_iv);
            mMsgTV = (TextView)itemView.findViewById(R.id.msg_tv);
            mApproveIV = (ImageView) itemView.findViewById(R.id.approve_iv);
            mRejectIV = (ImageView) itemView.findViewById(R.id.reject_iv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
        }
    }

    @Override
    public AttendanceMsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attendance_msg, parent, false);
        AttendanceMsgViewHolder attendanceMsgViewHolder = new AttendanceMsgViewHolder(view);
        return attendanceMsgViewHolder;
    }

    @Override
    public void onBindViewHolder(AttendanceMsgViewHolder holder, final int position) {
        Picasso.with(mContext).load(mAttendanceMsgs.get(position).getUserAvatarUrl()).fit().centerCrop().into(holder.mAvatarIV);
        holder.mMsgTV.setText(mAttendanceMsgs.get(position).getMsg());
        holder.mApproveIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mApproveListener.executeApproveAction(mAttendanceMsgs.get(position).getUid(), position);
            }
        });
        holder.mRejectIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRejectListener.executeRejectAction(mAttendanceMsgs.get(position).getUid(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAttendanceMsgs.size();
    }


}
