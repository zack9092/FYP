package com.example.elly_clarkson.fyp.Retrofit;

import io.reactivex.Observable;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IMyService {
    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("userName") String userName,
                                    @Field("userPassword")String userPassword);
}
