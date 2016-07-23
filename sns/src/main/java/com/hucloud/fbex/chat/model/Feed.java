package com.hucloud.fbex.chat.model;

import java.util.Date;
import java.util.List;

/**
 * 해당 파일은 소유권은 신휴창에게 있습니다.
 * 현재 오픈 소스로 공개중인 버전은 AGPL을 따르는 오픈 소스 프로젝트이며,
 * 소스 코드를 수정하셔서 사용하는 경우에는 반드시 동일한 라이센스로 소스 코드를 공개하여야 합니다.
 * 만약 HUCLOUD를 상업적으로 이용하실 경우에는 라이센스를 구매하여 사용하셔야 합니다.
 * email : huttchang@gmail.com
 * 프로젝트명    : fbex
 * 작성 및 소유자 : hucloud
 * 최초 생성일   : 2016. 7. 20.
 */

public class Feed {

    private String feedId;
    private String text, userId, userPhoto, userName;
    private List<String> photos;
    private List<Reply> replyList;
    private Date regdate;
    private boolean isMeLike = false;
    private int repliesCnt;

    public String getFeedId() {
        return feedId;
    }

    public void setFeedId(String feedId) {
        this.feedId = feedId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Date getRegdate() {
        return regdate;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public boolean isMeLike() {
        return isMeLike;
    }

    public void setIsMeLike(boolean isMeLike) {
        this.isMeLike = isMeLike;
    }

    public int getRepliesCnt() {
        return repliesCnt;
    }

    public void setRepliesCnt(int repliesCnt) {
        this.repliesCnt = repliesCnt;
    }

    public List<Reply> getReplyList() {
        return replyList;
    }

    public void setReplyList(List<Reply> replyList) {
        this.replyList = replyList;
    }
}
