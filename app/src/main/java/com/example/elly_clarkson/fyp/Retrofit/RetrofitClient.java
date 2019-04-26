package com.example.elly_clarkson.fyp.Retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RetrofitClient {

    private static Retrofit instance;
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS)
            .build();

    public static Retrofit getInstance(){
        if(instance==null)
           // instance=new Retrofit.Builder().baseUrl("http://10.0.2.2:3000/").client(okHttpClient).addConverterFactory(ScalarsConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
            instance=new Retrofit.Builder().baseUrl("http://10.0.2.2:3000/").client(okHttpClient).addConverterFactory(ScalarsConverterFactory.create()).addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build();
            return instance;
    }
}
