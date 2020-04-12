package com.instagram.network.models;

public class Story {
    private String imageUrl;

    public Story() {
    }

    public Story(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
