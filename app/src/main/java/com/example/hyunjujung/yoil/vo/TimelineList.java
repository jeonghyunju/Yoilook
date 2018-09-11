package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 10. 16..
 */

public class TimelineList {
    @SerializedName("timeline")
    @Expose
    private ArrayList<TimelineVO> arrayList = new ArrayList<>();

    public ArrayList<TimelineVO> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<TimelineVO> arrayList) {
        this.arrayList = arrayList;
    }
}
