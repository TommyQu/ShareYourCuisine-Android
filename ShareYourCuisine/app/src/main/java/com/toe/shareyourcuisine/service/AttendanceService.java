package com.toe.shareyourcuisine.service;

import com.toe.shareyourcuisine.model.Attendance;

import java.util.List;

/**
 * Created by HQu on 1/9/2017.
 */

public class AttendanceService {

    private RequestEventAttendanceListener mRequestEventAttendanceListener;
    private GetEventAttendancesByEventIdListener mGetEventAttendancesByEventIdListener;

    public interface RequestEventAttendanceListener {
        public void requestEventAttendanceSucceed();
        public void requestEventAttendanceFail(String errorMsg);
    }

    public interface GetEventAttendancesByEventIdListener {
        public void getEventAttendancesSucceed(List<Attendance> attendances);
        public void getEventAttendancesFail(String errorMsg);
    }

    public void setRequestEventAttendanceListener(RequestEventAttendanceListener requestEventAttendanceListener) {
        mRequestEventAttendanceListener = requestEventAttendanceListener;
    }

    public void setGetEventAttendancesByEventIdListener(GetEventAttendancesByEventIdListener getEventAttendancesByEventIdListener) {
        mGetEventAttendancesByEventIdListener = getEventAttendancesByEventIdListener;
    }
}
