package com.toe.shareyourcuisine.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.toe.shareyourcuisine.R;
import com.toe.shareyourcuisine.adapter.AttendanceMsgRecyclerViewAdapter;
import com.toe.shareyourcuisine.model.Attendance;
import com.toe.shareyourcuisine.model.AttendanceMsg;
import com.toe.shareyourcuisine.service.AttendanceService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HQu on 1/9/2017.
 */

public class MessageActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, AttendanceService.GetAttendanceMsgsListener, AttendanceMsgRecyclerViewAdapter.ApproveListener, AttendanceService.ApproveAttendanceRequestListener, AttendanceMsgRecyclerViewAdapter.RejectListener, AttendanceService.RejectAttendanceRequestListener {

    private static final String TAG = "ToeMsgActivity:";
    private SwipeRefreshLayout mMsgSRL;
    private RecyclerView mAttendanceMsgRV;
    private AttendanceMsgRecyclerViewAdapter mAttendanceMsgRecyclerViewAdapter;
    private List<AttendanceMsg> mAttendanceMsgs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMsgSRL = (SwipeRefreshLayout)findViewById(R.id.msg_srl);
        mAttendanceMsgRV = (RecyclerView)findViewById(R.id.attendance_msg_rv);
        mAttendanceMsgRV.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        mMsgSRL.setOnRefreshListener(MessageActivity.this);
        mMsgSRL.setColorSchemeColors(ContextCompat.getColor(MessageActivity.this, R.color.colorRed), ContextCompat.getColor(MessageActivity.this, R.color.colorAccent), ContextCompat.getColor(MessageActivity.this, R.color.colorOrange));
        getAllMessages();
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
    public void onRefresh() {
        getAllMessages();
    }

    public void getAllMessages() {
        mMsgSRL.setRefreshing(true);
        AttendanceService attendanceService = new AttendanceService(MessageActivity.this);
        attendanceService.setGetAttendanceMsgsListener(MessageActivity.this);
        attendanceService.getPendingRequests(mAuth.getCurrentUser().getUid());
    }


    @Override
    public void getAttendanceMsgsSucceed(List<AttendanceMsg> attendanceMsgs) {
        mMsgSRL.setRefreshing(false);
        mAttendanceMsgs = new ArrayList<>();
        mAttendanceMsgs = attendanceMsgs;
        mAttendanceMsgRecyclerViewAdapter = new AttendanceMsgRecyclerViewAdapter(MessageActivity.this, mAttendanceMsgs);
        mAttendanceMsgRecyclerViewAdapter.setApproveListener(MessageActivity.this);
        mAttendanceMsgRecyclerViewAdapter.setRejectListener(MessageActivity.this);
        mAttendanceMsgRV.setAdapter(mAttendanceMsgRecyclerViewAdapter);
    }

    @Override
    public void getAttendanceMsgsFail(String errorMsg) {
        mMsgSRL.setRefreshing(false);
        Toast.makeText(MessageActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void executeApproveAction(String attendanceId, int position) {
        AttendanceService attendanceService = new AttendanceService(MessageActivity.this);
        attendanceService.setApproveAttendanceRequestListener(MessageActivity.this);
        attendanceService.approveAttendanceRequest(attendanceId, position);
    }

    @Override
    public void approveAttendanceRequestSucceed(int position) {
        mAttendanceMsgs.remove(position);
        mAttendanceMsgRecyclerViewAdapter.notifyItemRemoved(position);
        mAttendanceMsgRecyclerViewAdapter.notifyItemRangeChanged(position, mAttendanceMsgs.size());
        Toast.makeText(MessageActivity.this, "Request is approved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void approveAttendanceRequestFail(String errorMsg) {
        Toast.makeText(MessageActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void executeRejectAction(String attendanceId, int position) {
        AttendanceService attendanceService = new AttendanceService(MessageActivity.this);
        attendanceService.setRejectAttendanceRequestListener(MessageActivity.this);
        attendanceService.rejectAttendanceRequest(attendanceId, position);
    }

    @Override
    public void rejectAttendanceRequestSucceed(int position) {
        mAttendanceMsgs.remove(position);
        mAttendanceMsgRecyclerViewAdapter.notifyItemRemoved(position);
        mAttendanceMsgRecyclerViewAdapter.notifyItemRangeChanged(position, mAttendanceMsgs.size());
        Toast.makeText(MessageActivity.this, "Request is rejected!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void rejectAttendanceRequestFail(String errorMsg) {
        Toast.makeText(MessageActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}
