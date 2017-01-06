package com.toe.shareyourcuisine.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Event;
import com.toe.shareyourcuisine.model.EventItem;
import com.toe.shareyourcuisine.model.PostItem;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/27/2016.
 */

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventItemViewHolder> {

    private Context mContext;
    private List<EventItem> mEventItems;
    private static EventItemClickListener mEventItemClickListener;

    public EventRecyclerViewAdapter(Context context, List<EventItem> eventItems) {
        mContext = context;
        mEventItems = eventItems;
    }

    public interface EventItemClickListener {
        public void onItemClick(int position, View v);
    }

    public void setEventItemClickListener(EventItemClickListener eventItemClickListener) {
        mEventItemClickListener = eventItemClickListener;
    }

    public static class EventItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mDisplayImgIV;
        public TextView mTimeTV;
        public TextView mTitleTV;
        public TextView mLocationTV;

        public EventItemViewHolder(View itemView) {
            super(itemView);
            mDisplayImgIV = (ImageView)itemView.findViewById(R.id.display_img_iv);
            mTimeTV = (TextView)itemView.findViewById(R.id.time_tv);
            mTitleTV = (TextView)itemView.findViewById(R.id.title_tv);
            mLocationTV = (TextView) itemView.findViewById(R.id.location_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mEventItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    @Override
    public EventItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        EventItemViewHolder eventItemViewHolder = new EventItemViewHolder(view);
        return eventItemViewHolder;
    }


    @Override
    public void onBindViewHolder(EventItemViewHolder holder, int position) {
        String displayImgUrl = mEventItems.get(position).getDisplayImgUrl();
        if(displayImgUrl != null && displayImgUrl != "") {
            Picasso.with(mContext).load(displayImgUrl).fit().centerCrop().into(holder.mDisplayImgIV);
        }
        holder.mTimeTV.setText(SYCUtils.convertMillisecondsToDateTime(mEventItems.get(position).getStartTime()) + " ~ " + SYCUtils.convertMillisecondsToDateTime(mEventItems.get(position).getEndTime()));
        holder.mTitleTV.setText(mEventItems.get(position).getTitle());
        holder.mLocationTV.setText(mEventItems.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        return mEventItems.size();
    }


}
