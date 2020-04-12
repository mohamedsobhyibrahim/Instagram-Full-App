package com.instagram.network.models;

public class User {

    private String userId;
    private String userName;
    private String imageUrl;
    private String email;

    public User() {
    }

    public User(String userId, String userName, String imageUrl, String email) {
        this.userId = userId;
        this.userName = userName;
        this.imageUrl = imageUrl;
        this.email = email;
    }

    public User(String userId, String imageUrl) {
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
