package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.model.Event;
import com.toe.shareyourcuisine.service.EventService;
import com.toe.shareyourcuisine.service.UserService;

/**
 * Created by HQu on 12/19/2016.
 */
public class OneEventActivity extends BaseActivity implements EventService.GetEventByIdListener, UserService.RequestEventAttendanceListener {

    private static final String TAG = "ToeOneEventActivity:";
    private ImageView mDisplayImgIV;
    private TextView mTitleTV;
    private TextView mTimeTV;
    private TextView mLocationTV;
    private TextView mDescTV;
    private Button mAttendBtn;
    private RecyclerView mAttendantRV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_one_event);
        final String eventId = getIntent().getStringExtra("eventId");

        mDisplayImgIV = (ImageView)findViewById(R.id.display_img_iv);
        mTitleTV = (TextView)findViewById(R.id.title_tv);
        mTimeTV = (TextView)findViewById(R.id.time_tv);
        mLocationTV = (TextView)findViewById(R.id.location_tv);
        mDescTV = (TextView)findViewById(R.id.desc_tv);
        mAttendBtn = (Button)findViewById(R.id.attend_btn);
        mAttendantRV = (RecyclerView)findViewById(R.id.attendants_rv);

        mAttendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserService userService = new UserService(OneEventActivity.this);
                userService.setRequestEventAttendanceListener(OneEventActivity.this);
                userService.requestEventAttendance(eventId, mAuth.getCurrentUser().getUid());
            }
        });

        EventService eventService = new EventService(OneEventActivity.this);
        eventService.setGetEventByIdListener(OneEventActivity.this);
        eventService.getEventById(eventId);
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
    public void getEventByIdSucceed(Event event) {
        Picasso.with(this).load(event.getDisplayImgUrl()).fit().centerCrop().into(mDisplayImgIV);
        mTitleTV.setText(event.getTitle());
        mTimeTV.setText(event.getStartTime() + " ~ " + event.getEndTime());
        mLocationTV.setText(event.getLocation());
        mDescTV.setText(event.getDesc());
    }

    @Override
    public void getEventByIdFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestEventAttendanceSucceed() {
        Toast.makeText(this, "Send request successfully!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void requestEventAttendanceFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
