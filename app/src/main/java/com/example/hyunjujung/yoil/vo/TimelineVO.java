package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 10. 15..
 */

public class TimelineVO {
    @SerializedName("idx")
    private int idx;
    @SerializedName("writeprofile")
    private String writeprofile;
    @SerializedName("writeid")
    private String writeid;
    @SerializedName("writedate")
    private String writedate;
    @SerializedName("writetype")
    private String writetype;
    @SerializedName("writecontent")
    private String writecontent;
    @SerializedName("writeImg")
    private String writeImg;
    @SerializedName("favoriteCount")
    private int favoriteCount;
    @SerializedName("commentCount")
    private int commentCount;
    private int favoriteImg;

    public TimelineVO() {
    }

    public TimelineVO(int idx, String writeprofile, String writeid, String writedate, String writetype, String writecontent, String writeImg, int favoriteCount, int commentCount, int favoriteImg) {
        this.idx = idx;
        this.writeprofile = writeprofile;
        this.writeid = writeid;
        this.writedate = writedate;
        this.writetype = writetype;
        this.writecontent = writecontent;
        this.writeImg = writeImg;
        this.favoriteCount = favoriteCount;
        this.commentCount = commentCount;
        this.favoriteImg = favoriteImg;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getWriteprofile() {
        return writeprofile;
    }

    public void setWriteprofile(String writeprofile) {
        this.writeprofile = writeprofile;
    }

    public String getWriteid() {
        return writeid;
    }

    public void setWriteid(String writeid) {
        this.writeid = writeid;
    }

    public String getWritedate() {
        return writedate;
    }

    public void setWritedate(String writedate) {
        this.writedate = writedate;
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

    public String getWriteImg() {
        return writeImg;
    }

    public void setWriteImg(String writeImg) {
        this.writeImg = writeImg;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getFavoriteImg() {
        return favoriteImg;
    }

    public void setFavoriteImg(int favoriteImg) {
        this.favoriteImg = favoriteImg;
    }
}
