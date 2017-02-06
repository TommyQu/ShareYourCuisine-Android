package com.toe.shareyourcuisine.model;

import com.orm.dsl.Unique;

/**
 * Created by HQu on 1/5/2017.
 */

public class Attendance{

    @Unique
    private String uid;
    private String eventId;
    private String userId;
    private String eventIdUserId;
    private String userName;
    private String userAvatarUrl;
    private Long requestedAt;
    private String status;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEventIdUserId() {
        return eventIdUserId;
    }

    public void setEventIdUserId(String eventIdUserId) {
        this.eventIdUserId = eventIdUserId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatarUrl() {
        return userAvatarUrl;
    }

    public void setUserAvatarUrl(String userAvatarUrl) {
        this.userAvatarUrl = userAvatarUrl;
    }

    public Long getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
