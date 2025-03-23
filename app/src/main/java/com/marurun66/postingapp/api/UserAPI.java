package com.marurun66.postingapp.api;


import com.marurun66.postingapp.model.LoginRequest;
import com.marurun66.postingapp.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserAPI {

    @POST("/auth/user/login")
    Call<LoginResponse> login(@Body LoginRequest loginRequest);


    //회원가입
    @POST("/auth/user/signup")
    Call<Void> signUp(@Body LoginRequest signUpRequest);

}
