package com.toe.shareyourcuisine.model;

import com.orm.dsl.Unique;

import java.util.ArrayList;

/**
 * Created by HQu on 12/27/2016.
 */

public class Post {

    @Unique
    private String uid;
    private String content;
    private String imgUrl;
    private String createdUserId;
    private String createdUserName;
    private String createdUserAvatarUrl;
    private Long createdAt;
    private int totalLikes;
    private ArrayList<String> likedBy;

    public Post() {
        likedBy = new ArrayList<String>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }
}
