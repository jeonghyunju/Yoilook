package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 11. 30..
 */

public class MyFollowingVO {
    @SerializedName("idx")
    private int idx;
    @SerializedName("userid")
    private String userid;
    @SerializedName("followingId")
    private String followingId;
    @SerializedName("followingProfile")
    private String followingProfile;
    @SerializedName("followingName")
    private String followingName;

    public MyFollowingVO(int idx, String userid, String followingId, String followingProfile, String followingName) {
        this.idx = idx;
        this.userid = userid;
        this.followingId = followingId;
        this.followingProfile = followingProfile;
        this.followingName = followingName;
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

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public String getFollowingProfile() {
        return followingProfile;
    }

    public void setFollowingProfile(String followingProfile) {
        this.followingProfile = followingProfile;
    }

    public String getFollowingName() {
        return followingName;
    }

    public void setFollowingName(String followingName) {
        this.followingName = followingName;
    }
}
