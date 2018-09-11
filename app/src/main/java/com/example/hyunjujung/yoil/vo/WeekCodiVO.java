package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 11. 3..
 */

public class WeekCodiVO {
    @SerializedName("ymd")
    private String ymd;

    public WeekCodiVO() {
    }

    public WeekCodiVO(String ymd) {
        this.ymd = ymd;
    }

    public String getYmd() {
        return ymd;
    }

    public void setYmd(String ymd) {
        this.ymd = ymd;
    }
}
