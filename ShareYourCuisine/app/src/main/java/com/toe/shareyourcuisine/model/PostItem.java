package com.toe.shareyourcuisine.model;

import com.orm.dsl.Unique;

/**
 * Created by HQu on 12/28/2016.
 */

public class PostItem extends Post{

    private String createdUserName;
    private String createdUserAvatarUrl;

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
