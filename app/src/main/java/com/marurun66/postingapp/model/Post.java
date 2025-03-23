package com.marurun66.postingapp.model;

public class Post {

    public Long id;
    public Long userId;
    public String imageUrl;
    public String content;
    public String createdAt;
    public String email;
    public Boolean isLiked;

    public Post(Long id, Long userId, String imageUrl, String content, String createdAt, String email, Boolean isLiked) {
        this.id = id;
        this.userId = userId;
        this.imageUrl = imageUrl;
        this.content = content;
        this.createdAt = createdAt;
        this.email = email;
        this.isLiked = isLiked;
    }

    public Post() {
    }
}
