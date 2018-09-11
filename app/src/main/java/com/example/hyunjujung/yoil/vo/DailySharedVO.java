package com.example.hyunjujung.yoil.vo;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 11. 2..
 */

public class DailySharedVO {
    /* Shared 에 저장하기 위한 vo 클래스 */
    private String albumPath;
    private String dateTV;
    private ArrayList<String> tagArray;
    private ArrayList<String> absoluteArray;

    public DailySharedVO() {
    }

    public DailySharedVO(String albumPath, String dateTV, ArrayList<String> tagArray, ArrayList<String> absoluteArray) {
        this.albumPath = albumPath;
        this.dateTV = dateTV;
        this.tagArray = tagArray;
        this.absoluteArray = absoluteArray;
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    public String getDateTV() {
        return dateTV;
    }

    public void setDateTV(String dateTV) {
        this.dateTV = dateTV;
    }

    public ArrayList<String> getTagArray() {
        return tagArray;
    }

    public void setTagArray(ArrayList<String> tagArray) {
        this.tagArray = tagArray;
    }

    public ArrayList<String> getAbsoluteArray() {
        return absoluteArray;
    }

    public void setAbsoluteArray(ArrayList<String> absoluteArray) {
        this.absoluteArray = absoluteArray;
    }
}
