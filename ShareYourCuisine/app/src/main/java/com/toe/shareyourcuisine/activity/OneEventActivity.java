package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.CommentRecyclerViewAdapter;
import com.toe.shareyourcuisine.libs.RatingDialog;
import com.toe.shareyourcuisine.model.CommentItem;
import com.toe.shareyourcuisine.model.EventItem;
import com.toe.shareyourcuisine.model.Recipe;
import com.toe.shareyourcuisine.model.User;
import com.toe.shareyourcuisine.service.CommentService;
import com.toe.shareyourcuisine.service.EventService;
import com.toe.shareyourcuisine.service.RecipeService;
import com.toe.shareyourcuisine.service.UserService;
import com.toe.shareyourcuisine.utils.Constants;

import org.parceler.Parcels;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HQu on 12/19/2016.
 */
public class OneEventActivity extends BaseActivity implements EventService.GetEventItemByIdListener {

    private static final String TAG = "ToeOneEventActivity:";
    private ImageView mDisplayImgIV;
    private TextView mTitleTV;
    private TextView mTimeTV;
    private TextView mLocationTV;
    private TextView mDescTV;
    private RecyclerView mAttendantRV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_one_event);
        String eventId = getIntent().getStringExtra("eventId");

        mDisplayImgIV = (ImageView)findViewById(R.id.display_img_iv);
        mTitleTV = (TextView)findViewById(R.id.title_tv);
        mTimeTV = (TextView)findViewById(R.id.time_tv);
        mLocationTV = (TextView)findViewById(R.id.location_tv);
        mDescTV = (TextView)findViewById(R.id.desc_tv);
        mAttendantRV = (RecyclerView)findViewById(R.id.attendants_rv);

        EventService eventService = new EventService(OneEventActivity.this);
        eventService.setGetEventItemByIdListener(OneEventActivity.this);
        eventService.getEventItemById(eventId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void getEventItemByIdSucceed(EventItem eventItem) {
        Picasso.with(this).load(eventItem.getDisplayImgUrl()).fit().centerCrop().into(mDisplayImgIV);
        mTitleTV.setText(eventItem.getTitle());
        mTimeTV.setText(eventItem.getStartTime() + " ~ " + eventItem.getEndTime());
        mLocationTV.setText(eventItem.getLocation());
        mDescTV.setText(eventItem.getDesc());
    }

    @Override
    public void getEventItemByIdFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
