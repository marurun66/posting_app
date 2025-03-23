package com.marurun66.postingapp.api;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface LikesAPI {

    @POST("/posting/{postingId}/like")
    Call<Void> likePost(@Header("Authorization") String token, @Path("postingId") Long postingId);

    @DELETE("/posting/{postingId}/unlike")
    Call<Void> unlikePost(@Header("Authorization") String token, @Path("postingId") Long postingId);
}

