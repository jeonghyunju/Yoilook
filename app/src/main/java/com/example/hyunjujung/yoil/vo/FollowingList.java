package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 11. 30..
 */

public class FollowingList {
    @SerializedName("followingList")
    private String followingList;

    public FollowingList(String followingList) {
        this.followingList = followingList;
    }

    public String getFollowingList() {
        return followingList;
    }

    public void setFollowingList(String followingList) {
        this.followingList = followingList;
    }
}
