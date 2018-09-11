package com.example.hyunjujung.yoil.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hyunjujung on 2017. 10. 10..
 */

public class RetrofitCreator {
    private static final String SERVER = "http://13.124.12.50/androidproject/";
    public static Retrofit createRetrofit() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.MINUTES)
                .connectTimeout(3, TimeUnit.MINUTES)
                .build();
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(SERVER)
                .addConverterFactory(GsonConverterFactory.create(gson))
                //.client(okHttpClient)
                .build();
    }

    public static ApiConfig getapiConfig() {
        return createRetrofit().create(ApiConfig.class);
    }
}
