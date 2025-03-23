package com.marurun66.postingapp.api;

import com.marurun66.postingapp.model.PostListResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PostAPI {


    @GET("/posting")
    Call<PostListResponse> getPostingList(@Header("Authorization") String token, @Query("page") int page, @Query("size") int size);
    // API 인터페이스 수정

    @Multipart
    @POST("/posting")
    Call<Void> createPost(@Header("Authorization") String token,
                          @Part("content") RequestBody content,
                          @Part MultipartBody.Part image);
}
