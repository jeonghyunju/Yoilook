package com.example.hyunjujung.yoil.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.apdater.InfoAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.TimelineList;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 10. 25..
 */

public class InfoGridFragment extends Fragment {
    RecyclerView infoGridRecycle;
    RecyclerView.LayoutManager infoLayout;
    InfoAdapter infoAdapter;
    ArrayList<TimelineVO> infoList = new ArrayList<>();

    Intent intent;

    static String userid = "";
    static String userpw = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_grid_fragment, container, false);
        infoGridRecycle = (RecyclerView)view.findViewById(R.id.infoGridRecycle);

        intent = getActivity().getIntent();

        SharedPreferences autoLogin = getActivity().getSharedPreferences("auto", Context.MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동 로그인 일때
            userid = autoLogin.getString("autoId", null);
            userpw = autoLogin.getString("autoPw", null);
        }else {
            SharedPreferences noAuto = getActivity().getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            userid = noAuto.getString("noAutoid", null);
            userpw = noAuto.getString("noAutopw", null);
        }

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        if(intent.getStringExtra("writeId") != null && !intent.getStringExtra("writeId").equals(userid)) {
            /* 타임라인에서 다른 사용자 게시물 클릭해서 들어왔을때 */
            getTimelineList(intent.getStringExtra("writeId"));
        }else {
            /* 내 아이디 클릭했을때 */
            getTimelineList(userid);
        }
    }

    public void getTimelineList(String userids) {
        ApiConfig apiconfig = RetrofitCreator.getapiConfig();
        Call<TimelineList> infocall = apiconfig.selectWrite(userids);
        infocall.enqueue(new Callback<TimelineList>() {
            @Override
            public void onResponse(Call<TimelineList> call, Response<TimelineList> response) {
                infoList = response.body().getArrayList();
                infoLayout = new GridLayoutManager(getContext(), 3);
                infoGridRecycle.setLayoutManager(infoLayout);

                infoAdapter = new InfoAdapter(getActivity().getApplicationContext(), infoList);
                infoGridRecycle.setAdapter(infoAdapter);
            }

            @Override
            public void onFailure(Call<TimelineList> call, Throwable t) {

            }
        });
    }
}
