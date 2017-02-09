package com.toe.shareyourcuisine.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.AttendanceRecyclerViewAdapter;
import com.toe.shareyourcuisine.model.Attendance;
import com.toe.shareyourcuisine.model.Event;
import com.toe.shareyourcuisine.service.AttendanceService;
import com.toe.shareyourcuisine.service.EventService;
import com.toe.shareyourcuisine.service.UserService;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HQu on 12/19/2016.
 */
public class OneEventActivity extends BaseActivity implements EventService.GetEventByIdListener, AttendanceService.RequestEventAttendanceListener, AttendanceService.GetEventAttendancesByEventIdListener, AttendanceService.CancelEventAttendanceListener {

    private static final String TAG = "ToeOneEventActivity:";
    private ImageView mDisplayImgIV;
    private TextView mTitleTV;
    private TextView mTimeTV;
    private TextView mLocationTV;
    private TextView mDescTV;
    private Button mAttendBtn;
    private Button mCancelAttendanceBtn;
    private TextView mAttendantTitleTV;
    private RecyclerView mAttendanceRV;
    private AttendanceRecyclerViewAdapter mAdapter;
    private String mEventId;
    private List<Attendance> mAttendances;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_one_event);
        mEventId = getIntent().getStringExtra("eventId");

        mDisplayImgIV = (ImageView)findViewById(R.id.display_img_iv);
        mTitleTV = (TextView)findViewById(R.id.title_tv);
        mTimeTV = (TextView)findViewById(R.id.time_tv);
        mLocationTV = (TextView)findViewById(R.id.location_tv);
        mDescTV = (TextView)findViewById(R.id.desc_tv);
        mAttendBtn = (Button)findViewById(R.id.attend_btn);
        mCancelAttendanceBtn = (Button)findViewById(R.id.cancel_attendance_btn);
        mAttendantTitleTV = (TextView)findViewById(R.id.attendant_title_tv);
        mAttendanceRV = (RecyclerView)findViewById(R.id.attendances_rv);
        mAttendanceRV.setLayoutManager(new GridLayoutManager(OneEventActivity.this, 6));
        mAttendances = new ArrayList<Attendance>();

        mAttendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    AttendanceService attendanceService = new AttendanceService(OneEventActivity.this);
                    attendanceService.setRequestEventAttendanceListener(OneEventActivity.this);
                    attendanceService.requestEventAttendance(mEventId, mAuth.getCurrentUser());
                } else {
                    Toast.makeText(OneEventActivity.this, "Please log in!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mCancelAttendanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    AttendanceService attendanceService = new AttendanceService(OneEventActivity.this);
                    attendanceService.setCancelEventAttendanceListener(OneEventActivity.this);
                    attendanceService.cancelEventAttendance(mEventId, mAuth.getCurrentUser().getUid());
                } else {
                    Toast.makeText(OneEventActivity.this, "Please log in!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        EventService eventService = new EventService(OneEventActivity.this);
        eventService.setGetEventByIdListener(OneEventActivity.this);
        eventService.getEventById(mEventId);

        AttendanceService attendanceService = new AttendanceService(OneEventActivity.this);
        attendanceService.setGetEventAttendancesByEventIdListener(OneEventActivity.this);
        attendanceService.getEventAttendancesByEventId(mEventId);

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
        mTimeTV.setText(SYCUtils.convertMillisecondsToDateTime(event.getStartTime()) + " ~ " + SYCUtils.convertMillisecondsToDateTime(event.getEndTime()));
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
        mAttendBtn.setVisibility(View.GONE);
        mCancelAttendanceBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void requestEventAttendanceFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void getEventAttendancesSucceed(List<Attendance> attendances) {
        mAttendances.clear();
        for(Attendance attendance : attendances) {
            if(attendance.getStatus().equalsIgnoreCase("Approved"))
                mAttendances.add(attendance);
        }
        mAttendantTitleTV.setText(mAttendances.size() + " attendants");
        mAdapter = new AttendanceRecyclerViewAdapter(OneEventActivity.this, mAttendances);
        mAttendanceRV.setAdapter(mAdapter);

        for(int i = 0; i < attendances.size(); i++) {
            if(attendances.get(i).getUserId().equalsIgnoreCase(mAuth.getCurrentUser().getUid())
                    && !attendances.get(i).getStatus().equalsIgnoreCase("Rejected")) {
                mAttendBtn.setVisibility(View.GONE);
                mCancelAttendanceBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void getEventAttendancesFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void cancelEventAttendanceSucceed() {
        Toast.makeText(this, "Cancel Attendance Successfully!", Toast.LENGTH_LONG).show();
        mAttendBtn.setVisibility(View.VISIBLE);
        mCancelAttendanceBtn.setVisibility(View.GONE);
        AttendanceService attendanceService = new AttendanceService(OneEventActivity.this);
        attendanceService.setGetEventAttendancesByEventIdListener(OneEventActivity.this);
        attendanceService.getEventAttendancesByEventId(mEventId);
    }

    @Override
    public void cancelEventAttendanceFail(String errorMsg) {
        Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
    }
}
