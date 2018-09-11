package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 10. 22..
 */

public class CommentList {
    @SerializedName("commentList")
    @Expose
    private ArrayList<CommentVO> commentList = new ArrayList<>();

    public ArrayList<CommentVO> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<CommentVO> commentList) {
        this.commentList = commentList;
    }
}
