package com.example.hyunjujung.yoil.apis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hyunjujung on 2017. 12. 5..
 */

public class WeatherRetrofit {
    private static final String BASEURL = "http://apis.skplanetx.com/";
    public static Retrofit weatherRetroCreate() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();
        return new Retrofit.Builder()
                .baseUrl(BASEURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static WeatherApi getWeatherApi() {
        return weatherRetroCreate().create(WeatherApi.class);
    }

}
