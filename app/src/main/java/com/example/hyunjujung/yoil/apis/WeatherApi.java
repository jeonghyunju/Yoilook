package com.example.hyunjujung.yoil.apis;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by hyunjujung on 2017. 12. 5..
 */

public interface WeatherApi {
    static final String APPKEY = "eeaa29f3-8155-31d7-ad15-9104758122f4";

    @GET("weather/forecast/3days")
    Call<JsonObject> getForecast(@Header("appKey") String appKey,
                                @Query("version") int version,
                                @Query("lat") double lat,
                                @Query("lon") double lon);

    @GET("weather/current/hourly")
    Call<JsonObject> getHourly(@Header("appKey") String appKey,
                               @Query("version") int version,
                               @Query("lat") double lat,
                               @Query("lon") double lon);
}
