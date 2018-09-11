package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 11. 3..
 */

public class WeekCodiweekdayVO {
    @SerializedName("ymd")
    private String ymds;

    public WeekCodiweekdayVO() {
    }

    public WeekCodiweekdayVO(String ymds) {
        this.ymds = ymds;
    }

    public String getYmds() {
        return ymds;
    }

    public void setYmds(String ymds) {
        this.ymds = ymds;
    }
}
