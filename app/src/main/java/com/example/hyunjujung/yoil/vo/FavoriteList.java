package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 10. 18..
 */

public class FavoriteList {
    @SerializedName("favoriteList")
    @Expose
    private ArrayList<FavoriteVO> arrayList = new ArrayList<>();

    public ArrayList<FavoriteVO> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<FavoriteVO> arrayList) {
        this.arrayList = arrayList;
    }
}
