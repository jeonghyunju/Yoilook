package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 10. 11..
 */

public class SelectVO {
    //  아이디에 해당하는 정보 VO 클래스

    @SerializedName("profile")
    private String profile;
    @SerializedName("username")
    private String username;
    @SerializedName("userid")
    private String userid;
    @SerializedName("userpass")
    private String userpass;
    @SerializedName("userphone")
    private String userphone;
    @SerializedName("useremail")
    private String useremail;
    @SerializedName("usergender")
    private String usergender;
    @SerializedName("userage")
    private String userage;
    @SerializedName("userarea")
    private String userarea;

    public SelectVO() {
    }

    public SelectVO(String profile, String username, String userid, String userpass, String userphone, String useremail, String usergender, String userage, String userarea) {
        this.profile = profile;
        this.username = username;
        this.userid = userid;
        this.userpass = userpass;
        this.userphone = userphone;
        this.useremail = useremail;
        this.usergender = usergender;
        this.userage = userage;
        this.userarea = userarea;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserpass() {
        return userpass;
    }

    public void setUserpass(String userpass) {
        this.userpass = userpass;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUsergender() {
        return usergender;
    }

    public void setUsergender(String usergender) {
        this.usergender = usergender;
    }

    public String getUserage() {
        return userage;
    }

    public void setUserage(String userage) {
        this.userage = userage;
    }

    public String getUserarea() {
        return userarea;
    }

    public void setUserarea(String userarea) {
        this.userarea = userarea;
    }
}
