package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 11. 28..
 */

public class UserFollowVO {
    @SerializedName("follows")
    private String follows;

    public UserFollowVO() {
    }

    public UserFollowVO(String follows) {
        this.follows = follows;
    }

    public String getFollows() {
        return follows;
    }

    public void setFollows(String follows) {
        this.follows = follows;
    }
}
