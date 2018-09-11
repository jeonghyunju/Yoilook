package com.example.hyunjujung.yoil.vo;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyunjujung on 2017. 11. 1..
 */

public class DailyCodiVO {
    @SerializedName("idx")
    private int idx;
    @SerializedName("year")
    private String year;
    @SerializedName("month")
    private String month;
    @SerializedName("day")
    private String day;
    @SerializedName("weekday")
    private String weekday;
    @SerializedName("mainimage")
    private String mainimage;
    @SerializedName("subimage")
    private String subimage;
    @SerializedName("tag")
    private String tag;
    @SerializedName("favorite")
    private int favorite;
    @SerializedName("ymd")
    private String ymd;

    public DailyCodiVO() {
    }

    public DailyCodiVO(int idx, String year, String month, String day, String weekday, String mainimage, String subimage, String tag, int favorite, String ymd) {
        this.idx = idx;
        this.year = year;
        this.month = month;
        this.day = day;
        this.weekday = weekday;
        this.mainimage = mainimage;
        this.subimage = subimage;
        this.tag = tag;
        this.favorite = favorite;
        this.ymd = ymd;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getWeekday() {
        return weekday;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public String getMainimage() {
        return mainimage;
    }

    public void setMainimage(String mainimage) {
        this.mainimage = mainimage;
    }

    public String getSubimage() {
        return subimage;
    }

    public void setSubimage(String subimage) {
        this.subimage = subimage;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public String getYmd() {
        return ymd;
    }

    public void setYmd(String ymd) {
        this.ymd = ymd;
    }
}
