package com.toe.shareyourcuisine.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

/**
 * Created by HQu on 12/5/2016.
 */

@IgnoreExtraProperties
public class Recipe {

    private String id;
    private String title;
    private String cookingTime;
    private String displayImgUrl;
    private String content;
    private ArrayList<String> contentImgUrls;
    private Long createdAt;
    private String createdBy;
    private Long lastCommentedAt;

    public Recipe() {
        contentImgUrls = new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public String getDisplayImgUrl() {
        return displayImgUrl;
    }

    public void setDisplayImgUrl(String displayImgUrl) {
        this.displayImgUrl = displayImgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getContentImgUrls() {
        return contentImgUrls;
    }

    public void setContentImgUrls(ArrayList<String> contentImgUrls) {
        this.contentImgUrls = contentImgUrls;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Long getLastCommentedAt() {
        return lastCommentedAt;
    }

    public void setLastCommentedAt(Long lastCommentedAt) {
        this.lastCommentedAt = lastCommentedAt;
    }
}
