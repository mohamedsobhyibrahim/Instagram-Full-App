package com.instagram.network.models;

public class Post {

    private String id;
    private String userImageUrl;
    private String userName;
    private String postTime;
    private String postImageUrl;
    private int likeCount;

    public Post(){

    }

    public Post(String userImageUrl, String userName, String postTime, String postImageUrl, int likeCount) {
        this.userImageUrl = userImageUrl;
        this.userName = userName;
        this.postTime = postTime;
        this.postImageUrl = postImageUrl;
        this.likeCount = likeCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostTime() {
        return postTime;
    }

    public void setPostTime(String postTime) {
        this.postTime = postTime;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
}
