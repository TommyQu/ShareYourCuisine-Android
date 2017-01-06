package com.toe.shareyourcuisine.model;

/**
 * Created by HQu on 12/28/2016.
 */

public class EventItem extends Event {

    private int attendedUserNum;
    private String createdUserName;
    private String createdUserAvatarUrl;

    public int getAttendedUserNum() {
        return attendedUserNum;
    }

    public void setAttendedUserNum(int attendedUserNum) {
        this.attendedUserNum = attendedUserNum;
    }

    public String getCreatedUserName() {
        return createdUserName;
    }

    public void setCreatedUserName(String createdUserName) {
        this.createdUserName = createdUserName;
    }

    public String getCreatedUserAvatarUrl() {
        return createdUserAvatarUrl;
    }

    public void setCreatedUserAvatarUrl(String createdUserAvatarUrl) {
        this.createdUserAvatarUrl = createdUserAvatarUrl;
    }
}
