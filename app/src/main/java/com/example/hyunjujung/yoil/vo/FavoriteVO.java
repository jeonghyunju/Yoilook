package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 10. 18..
 */

public class FavoriteVO {
    @SerializedName("userid")
    private String userid;
    @SerializedName("favoriteIdx")
    private int favoriteIdx;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public int getFavoriteIdx() {
        return favoriteIdx;
    }

    public void setFavoriteIdx(int favoriteIdx) {
        this.favoriteIdx = favoriteIdx;
    }
}
