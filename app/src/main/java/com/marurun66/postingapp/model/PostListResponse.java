package com.marurun66.postingapp.model;

import java.util.List;

public class PostListResponse {
    public List<Post> postsList;
    public int count;

    public PostListResponse() {
    }

    public PostListResponse(List<Post> postsList, int count) {
        this.postsList = postsList;
        this.count = count;
    }
}
