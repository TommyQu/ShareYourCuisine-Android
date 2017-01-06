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
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.List;

/**
 * Created by HQu on 12/27/2016.
 */

public class EventRecyclerViewAdapter extends RecyclerView.Adapter<EventRecyclerViewAdapter.EventViewHolder> {

    private Context mContext;
    private List<Event> mEvents;
    private static EventClickListener mEventClickListener;

    public EventRecyclerViewAdapter(Context context, List<Event> events) {
        mContext = context;
        mEvents = events;
    }

    public interface EventClickListener {
        public void onItemClick(int position, View v);
    }

    public void setEventItemClickListener(EventClickListener eventClickListener) {
        mEventClickListener = eventClickListener;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView mDisplayImgIV;
        public TextView mTimeTV;
        public TextView mTitleTV;
        public TextView mLocationTV;

        public EventViewHolder(View itemView) {
            super(itemView);
            mDisplayImgIV = (ImageView)itemView.findViewById(R.id.display_img_iv);
            mTimeTV = (TextView)itemView.findViewById(R.id.time_tv);
            mTitleTV = (TextView)itemView.findViewById(R.id.title_tv);
            mLocationTV = (TextView) itemView.findViewById(R.id.location_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mEventClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        EventViewHolder eventViewHolder = new EventViewHolder(view);
        return eventViewHolder;
    }


    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {
        String displayImgUrl = mEvents.get(position).getDisplayImgUrl();
        if(displayImgUrl != null && displayImgUrl != "") {
            Picasso.with(mContext).load(displayImgUrl).fit().centerCrop().into(holder.mDisplayImgIV);
        }
        holder.mTimeTV.setText(SYCUtils.convertMillisecondsToDateTime(mEvents.get(position).getStartTime()) + " ~ " + SYCUtils.convertMillisecondsToDateTime(mEvents.get(position).getEndTime()));
        holder.mTitleTV.setText(mEvents.get(position).getTitle());
        holder.mLocationTV.setText(mEvents.get(position).getLocation());
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }


}
