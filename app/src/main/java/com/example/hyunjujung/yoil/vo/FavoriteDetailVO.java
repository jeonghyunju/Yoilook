package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 10. 24..
 */

public class FavoriteDetailVO {
    @SerializedName("writeidx")
    private int writeidx;
    @SerializedName("writeid")
    private String writeid;
    @SerializedName("writeprofile")
    private String writeprofile;
    @SerializedName("writeimg")
    private String writeimg;
    @SerializedName("writedate")
    private String writedate;
    @SerializedName("favoriteCount")
    private int favoriteCount;
    @SerializedName("recommentCount")
    private int recommentCount;
    @SerializedName("writetype")
    private String writetype;
    @SerializedName("writecontent")
    private String writecontent;
    @SerializedName("commentText")
    private String commentText;

    public int getWriteidx() {
        return writeidx;
    }

    public void setWriteidx(int writeidx) {
        this.writeidx = writeidx;
    }

    public String getWriteid() {
        return writeid;
    }

    public void setWriteid(String writeid) {
        this.writeid = writeid;
    }

    public String getWriteprofile() {
        return writeprofile;
    }

    public void setWriteprofile(String writeprofile) {
        this.writeprofile = writeprofile;
    }

    public String getWriteimg() {
        return writeimg;
    }

    public void setWriteimg(String writeimg) {
        this.writeimg = writeimg;
    }

    public String getWritedate() {
        return writedate;
    }

    public void setWritedate(String writedate) {
        this.writedate = writedate;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getRecommentCount() {
        return recommentCount;
    }

    public void setRecommentCount(int recommentCount) {
        this.recommentCount = recommentCount;
    }

    public String getWritetype() {
        return writetype;
    }

    public void setWritetype(String writetype) {
        this.writetype = writetype;
    }

    public String getWritecontent() {
        return writecontent;
    }

    public void setWritecontent(String writecontent) {
        this.writecontent = writecontent;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }
}

