package com.example.hyunjujung.yoil;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 10. 10..
 */

public class ServerResponse {
    @SerializedName("success")
    private int success;
    @SerializedName("message")
    private String message;

    public int getSuccess() {
        return success;
    }
    public String getMessage() {
        return message;
    }

}
