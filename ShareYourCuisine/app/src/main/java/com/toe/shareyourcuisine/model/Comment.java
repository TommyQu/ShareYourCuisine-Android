package com.toe.shareyourcuisine.model;

import com.orm.dsl.Unique;

/**
 * Created by HQu on 12/28/2016.
 */

public class Comment {

    @Unique
    private String uid;
    private String parentId;
    private String content;
    private Long createdAt;
    private String createdUserId;
    private String createdUserName;
    private String createdUserAvatarUrl;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
}
