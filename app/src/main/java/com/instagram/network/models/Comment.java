package com.instagram.network.models;

public class Comment {

    private String userImage;
    private String userName;
    private String commment;

    public Comment() {
    }

    public Comment(String userImage, String userName, String commment) {
        this.userImage = userImage;
        this.userName = userName;
        this.commment = commment;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCommment() {
        return commment;
    }

    public void setCommment(String commment) {
        this.commment = commment;
    }
}
