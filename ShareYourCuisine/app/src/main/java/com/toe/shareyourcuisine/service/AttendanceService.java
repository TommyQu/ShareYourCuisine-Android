package com.toe.shareyourcuisine.service;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.toe.shareyourcuisine.activity.OneEventActivity;
import com.toe.shareyourcuisine.model.Attendance;
import com.toe.shareyourcuisine.model.AttendanceMsg;
import com.toe.shareyourcuisine.model.Event;
import com.toe.shareyourcuisine.utils.SYCUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by HQu on 1/9/2017.
 */

public class AttendanceService {

    private static final String TAG = "ToeAttendService:";
    private Context mContext;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mAttendanceRef;
    private RequestEventAttendanceListener mRequestEventAttendanceListener;
    private CancelEventAttendanceListener mCancelEventAttendanceListener;
    private GetEventAttendancesByEventIdListener mGetEventAttendancesByEventIdListener;
    private GetAttendanceMsgsListener mGetAttendanceMsgsListener;
    private ApproveAttendanceRequestListener mApproveAttendanceRequestListener;
    private RejectAttendanceRequestListener mRejectAttendanceRequestListener;

    private int mAttendanceMsgCount;

    public AttendanceService(Context context) {
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mAttendanceRef = mFirebaseDatabase.getReference("attendance");
    }

    public interface RequestEventAttendanceListener {
        public void requestEventAttendanceSucceed();
        public void requestEventAttendanceFail(String errorMsg);
    }

    public interface CancelEventAttendanceListener {
        public void cancelEventAttendanceSucceed();
        public void cancelEventAttendanceFail(String errorMsg);
    }

    public interface GetEventAttendancesByEventIdListener {
        public void getEventAttendancesSucceed(List<Attendance> attendances);
        public void getEventAttendancesFail(String errorMsg);
    }

    public interface GetAttendanceMsgsListener {
        public void getAttendanceMsgsSucceed(List<AttendanceMsg> attendanceMsgs);
        public void getAttendanceMsgsFail(String errorMsg);
    }

    public interface ApproveAttendanceRequestListener {
        public void approveAttendanceRequestSucceed(int position);
        public void approveAttendanceRequestFail(String errorMsg);
    }

    public interface RejectAttendanceRequestListener {
        public void rejectAttendanceRequestSucceed(int position);
        public void rejectAttendanceRequestFail(String errorMsg);
    }

    public void setRequestEventAttendanceListener(RequestEventAttendanceListener requestEventAttendanceListener) {
        mRequestEventAttendanceListener = requestEventAttendanceListener;
    }

    public void setCancelEventAttendanceListener(CancelEventAttendanceListener cancelEventAttendanceListener) {
        mCancelEventAttendanceListener = cancelEventAttendanceListener;
    }

    public void setGetEventAttendancesByEventIdListener(GetEventAttendancesByEventIdListener getEventAttendancesByEventIdListener) {
        mGetEventAttendancesByEventIdListener = getEventAttendancesByEventIdListener;
    }

    public void setGetAttendanceMsgsListener(GetAttendanceMsgsListener getAttendanceMsgsListener) {
        mGetAttendanceMsgsListener = getAttendanceMsgsListener;
    }

    public void setApproveAttendanceRequestListener(ApproveAttendanceRequestListener approveAttendanceRequestListener) {
        mApproveAttendanceRequestListener = approveAttendanceRequestListener;
    }

    public void setRejectAttendanceRequestListener(RejectAttendanceRequestListener rejectAttendanceRequestListener) {
        mRejectAttendanceRequestListener = rejectAttendanceRequestListener;
    }

    public void requestEventAttendance(String eventId, FirebaseUser currentUser) {
        Attendance attendance = new Attendance();
        attendance.setEventId(eventId);
        attendance.setUserId(currentUser.getUid());
        attendance.setEventIdUserId(eventId+"_"+currentUser.getUid());
        attendance.setUserName(currentUser.getDisplayName());
        attendance.setUserAvatarUrl(currentUser.getPhotoUrl().toString());
        attendance.setRequestedAt(SYCUtils.getCurrentEST());
        attendance.setStatus("Pending");

        mAttendanceRef.push().setValue(attendance, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if(databaseError != null)
                    mRequestEventAttendanceListener.requestEventAttendanceFail(databaseError.getMessage());
                else
                    mRequestEventAttendanceListener.requestEventAttendanceSucceed();
            }
        });
    }

    public void cancelEventAttendance(String eventId, String userId) {
        mAttendanceRef.orderByChild("eventIdUserId").equalTo(eventId+"_"+userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.getRef().removeValue(new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if(databaseError != null)
                            mCancelEventAttendanceListener.cancelEventAttendanceFail(databaseError.getMessage());
                        else
                            mCancelEventAttendanceListener.cancelEventAttendanceSucceed();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mCancelEventAttendanceListener.cancelEventAttendanceFail(databaseError.getMessage());
            }
        });
    }

    public void getEventAttendancesByEventId(String eventId, final String status) {
        mAttendanceRef.orderByChild("eventId").equalTo(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Attendance> attendances = new ArrayList<Attendance>();
                for(DataSnapshot attendanceSnapShot: dataSnapshot.getChildren()) {
                    Attendance attendance = attendanceSnapShot.getValue(Attendance.class);
                    attendance.setUid(attendanceSnapShot.getKey());
                    if(attendance.getStatus().equalsIgnoreCase(status))
                        attendances.add(attendance);
                }
                Collections.reverse(attendances);
                mGetEventAttendancesByEventIdListener.getEventAttendancesSucceed(attendances);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetEventAttendancesByEventIdListener.getEventAttendancesFail(databaseError.getMessage());
            }
        });
    }

    public void getPendingRequests(String userId) {
        DatabaseReference eventRef = mFirebaseDatabase.getReference("event");
        eventRef.orderByChild("createdUserId").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Event> events = new ArrayList<Event>();
                for(DataSnapshot eventSnapShot: dataSnapshot.getChildren()) {
                    Event event = eventSnapShot.getValue(Event.class);
                    event.setUid(eventSnapShot.getKey());
                    events.add(event);
                }
                getAttendanceMsgsByEvents(events);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mGetAttendanceMsgsListener.getAttendanceMsgsFail(databaseError.getMessage());
            }
        });
    }

    private void getAttendanceMsgsByEvents(final List<Event> events) {
        mAttendanceMsgCount = 0;
        final List<AttendanceMsg> attendanceMsgs = new ArrayList<AttendanceMsg>();
        for(int i = 0; i < events.size(); i++) {
            mAttendanceRef.orderByChild("eventId").equalTo(events.get(i).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot attendanceSnapShot: dataSnapshot.getChildren()) {
                        AttendanceMsg attendanceMsg = attendanceSnapShot.getValue(AttendanceMsg.class);
                        if(attendanceMsg.getStatus().equalsIgnoreCase("Pending")) {
                            attendanceMsg.setUid(attendanceSnapShot.getKey());
                            attendanceMsg.setMsg(attendanceMsg.getUserName()+ " requested to join "+events.get(mAttendanceMsgCount).getTitle() + " start from "+SYCUtils.convertMillisecondsToDateTime(events.get(mAttendanceMsgCount).getStartTime()));
//                            attendanceMsg.setEventTitle();
//                            attendanceMsg.setEventStartTime(events.get(mAttendanceMsgCount).getStartTime());
//                            attendanceMsg.setEventLocation(events.get(mAttendanceMsgCount).getLocation());
                            attendanceMsgs.add(attendanceMsg);
                        }
                    }
                    mAttendanceMsgCount++;
                    if(mAttendanceMsgCount == events.size())
                        mGetAttendanceMsgsListener.getAttendanceMsgsSucceed(attendanceMsgs);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    mGetAttendanceMsgsListener.getAttendanceMsgsFail(databaseError.getMessage());
                }
            });
        }
    }

    public void approveAttendanceRequest(String attendanceId, final int position) {
        mAttendanceRef.child(attendanceId).child("status").setValue("Approved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.getException() != null)
                    mApproveAttendanceRequestListener.approveAttendanceRequestFail(task.getException().getMessage());
                else
                    mApproveAttendanceRequestListener.approveAttendanceRequestSucceed(position);
            }
        });
    }

    public void rejectAttendanceRequest(String attendanceId, final int position) {
        mAttendanceRef.child(attendanceId).child("status").setValue("Rejected").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.getException() != null)
                    mRejectAttendanceRequestListener.rejectAttendanceRequestFail(task.getException().getMessage());
                else
                    mRejectAttendanceRequestListener.rejectAttendanceRequestSucceed(position);
            }
        });
    }
}
