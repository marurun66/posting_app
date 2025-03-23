package com.marurun66.postingapp.model;

import java.io.File;

import okhttp3.MultipartBody;


public class PostRequest {
    String content;
    MultipartBody.Part image;



    public PostRequest(String content, MultipartBody.Part image) {
        this.content = content;
        this.image = image;
    }

    public String getContent() {
        return content;
    }
}
