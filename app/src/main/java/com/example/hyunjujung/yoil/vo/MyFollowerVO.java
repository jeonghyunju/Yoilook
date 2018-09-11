package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 11. 30..
 */

public class MyFollowerVO {
    @SerializedName("idx")
    private int idx;
    @SerializedName("userid")
    private String userid;
    @SerializedName("followerId")
    private String followerId;
    @SerializedName("followerProfile")
    private String followerProfile;
    @SerializedName("followerName")
    private String followerName;

    public MyFollowerVO() {
    }

    public MyFollowerVO(int idx, String userid, String followerId, String followerProfile, String followerName) {
        this.idx = idx;
        this.userid = userid;
        this.followerId = followerId;
        this.followerProfile = followerProfile;
        this.followerName = followerName;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public String getFollowerProfile() {
        return followerProfile;
    }

    public void setFollowerProfile(String followerProfile) {
        this.followerProfile = followerProfile;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }
}
