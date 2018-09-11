package com.example.hyunjujung.yoil.vo;

import android.net.Uri;

/**
 * Created by hyunjujung on 2017. 10. 30..
 */

public class DailyCategoryVO {
    private Uri categoryImg;

    public DailyCategoryVO() {
    }

    public DailyCategoryVO(Uri categoryImg) {
        this.categoryImg = categoryImg;
    }

    public Uri getCategoryImg() {
        return categoryImg;
    }

    public void setCategoryImg(Uri categoryImg) {
        this.categoryImg = categoryImg;
    }
}
