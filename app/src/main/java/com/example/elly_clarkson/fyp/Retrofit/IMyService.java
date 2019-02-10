package com.example.elly_clarkson.fyp.Retrofit;

import org.json.JSONObject;

import io.reactivex.Observable;

import io.reactivex.Observer;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.*;

public interface IMyService {
    @POST("login")
    @FormUrlEncoded
    Observable<String> loginUser(@Field("userName") String userName,
                                    @Field("userPassword")String userPassword);

    @GET("seats")
     Observable<String> getSeats();

    @GET("seatStatus")
    Observable<String> seatStatus(@Query("place_id")String place_id);

    @GET("seatRecommended")
    Call<String> seatRecommended(@Query("floor") int floor);
}
