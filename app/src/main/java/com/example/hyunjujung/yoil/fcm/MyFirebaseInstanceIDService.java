package com.example.hyunjujung.yoil.fcm;

import android.content.SharedPreferences;

import com.example.hyunjujung.yoil.ServerResponse;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 10. 17..
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{
    //  회원가입 시에 기기 토큰을 생성하여 DB에 저장한다
    @Override
    public void onTokenRefresh() {
        //  토큰을 생성하여 변수에 저장한다
        String token = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String userToken) {
        String userid = "";
        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            userid = autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = getSharedPreferences("noAuto", MODE_PRIVATE);
            userid = noAuto.getString("noAutoid", null);
        }
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<ServerResponse> serverResponseCall = apiConfig.insertToken(userid, userToken);
        serverResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }
}
