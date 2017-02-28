package com.toe.shareyourcuisine.model;

import com.orm.dsl.Unique;

/**
 * Created by HQu on 2/28/2017.
 */

public class Favorite {

    @Unique
    private String uid;
    private String userId;
    private Recipe recipe;
    private Long createdAt;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
