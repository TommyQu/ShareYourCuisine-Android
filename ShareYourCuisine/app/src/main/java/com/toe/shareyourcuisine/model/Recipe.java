package com.toe.shareyourcuisine.model;

import android.content.Intent;

import com.google.firebase.database.IgnoreExtraProperties;
import com.orm.dsl.Unique;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by HQu on 12/5/2016.
 */

@Parcel(Parcel.Serialization.BEAN)
@IgnoreExtraProperties
public class Recipe {

    @Unique
    private String uid;
    private String title;
    private String cookingTime;
    private String displayImgUrl;
    private String content;
    private ArrayList<String> contentImgUrls;
    private Long createdAt;
    private String createdBy;
    private Long lastCommentedAt;
    private String flavorTypes;
    private float totalRates;
    private ArrayList<String> ratedBy;

    public Recipe() {
        contentImgUrls = new ArrayList<String>();
        ratedBy = new ArrayList<String>();
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
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

    public String getFlavorTypes() {
        return flavorTypes;
    }

    public void setFlavorTypes(String flavorTypes) {
        this.flavorTypes = flavorTypes;
    }

    public float getTotalRates() {
        return totalRates;
    }

    public void setTotalRates(float totalRates) {
        this.totalRates = totalRates;
    }

    public ArrayList<String> getRatedBy() {
        return ratedBy;
    }

    public void setRatedBy(ArrayList<String> ratedBy) {
        this.ratedBy = ratedBy;
    }

}
