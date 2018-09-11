package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 11. 30..
 */

public class FollowCountVO {
    @SerializedName("followerCount")
    private int followerCount;
    @SerializedName("followingCount")
    private int followingCount;

    public FollowCountVO(int followerCount, int followingCount) {
        this.followerCount = followerCount;
        this.followingCount = followingCount;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }
}
