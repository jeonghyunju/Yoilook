package com.example.hyunjujung.yoil.vo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hyunjujung on 2017. 10. 22..
 */

public class CommentVO {
    @SerializedName("idx")
    private int idx;
    @SerializedName("commentProfile")
    private String commentProfile;
    @SerializedName("commentId")
    private String commentId;
    @SerializedName("commentCon")
    private String commentCon;
    @SerializedName("commentDate")
    private String commentDate;
    @SerializedName("cGrpidx")
    private int cGrpidx;
    @SerializedName("cGroup")
    private int cGroup;
    @SerializedName("cSeq")
    private int cSeq;
    @SerializedName("cLevel")
    private int cLevel;

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public String getCommentProfile() {
        return commentProfile;
    }

    public void setCommentProfile(String commentProfile) {
        this.commentProfile = commentProfile;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getCommentCon() {
        return commentCon;
    }

    public void setCommentCon(String commentCon) {
        this.commentCon = commentCon;
    }

    public String getCommentDate() {
        return commentDate;
    }

    public void setCommentDate(String commentDate) {
        this.commentDate = commentDate;
    }

    public int getcGrpidx() {
        return cGrpidx;
    }

    public void setcGrpidx(int cGrpidx) {
        this.cGrpidx = cGrpidx;
    }

    public int getcGroup() {
        return cGroup;
    }

    public void setcGroup(int cGroup) {
        this.cGroup = cGroup;
    }

    public int getcSeq() {
        return cSeq;
    }

    public void setcSeq(int cSeq) {
        this.cSeq = cSeq;
    }

    public int getcLevel() {
        return cLevel;
    }

    public void setcLevel(int cLevel) {
        this.cLevel = cLevel;
    }
}
