package com.example.hyunjujung.yoil.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.apdater.InfoListAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.FavoriteList;
import com.example.hyunjujung.yoil.vo.FavoriteVO;
import com.example.hyunjujung.yoil.vo.TimelineList;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 10. 25..
 */

public class InfoListFragment extends Fragment {
    RecyclerView infoListRecycle;
    RecyclerView.LayoutManager infoListlayout;
    InfoListAdapter infoListAdapter;
    ArrayList<TimelineVO> myTimelineList = new ArrayList<>();
    ArrayList<FavoriteVO> myFavoriteList = new ArrayList<>();

    Intent intent;

    static String userid = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.info_list_fragment, container, false);
        infoListRecycle = (RecyclerView)view.findViewById(R.id.infoListRecycle);

        intent = getActivity().getIntent();

        /* 좋아요 리스트 모두 가져오기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<FavoriteList> callF = apiConfig.selectFavorite(getUserid());
        callF.enqueue(new Callback<FavoriteList>() {
            @Override
            public void onResponse(Call<FavoriteList> call, Response<FavoriteList> response) {
                myFavoriteList = response.body().getArrayList();
            }

            @Override
            public void onFailure(Call<FavoriteList> call, Throwable t) {

            }
        });

        infoListlayout = new LinearLayoutManager(getContext());
        infoListRecycle.setLayoutManager(infoListlayout);

        //  데이터와 view 연결
        infoListAdapter = new InfoListAdapter(getActivity().getApplicationContext(), myTimelineList, myFavoriteList);
        infoListRecycle.setAdapter(infoListAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(intent.getStringExtra("writeId") != null && !intent.getStringExtra("writeId").equals(getUserid())) {
            getTimelineList(intent.getStringExtra("writeId"));
        }else {
            //  내가 쓴 타임라인만 가져오기
            getTimelineList(getUserid());
        }

    }

    public void getTimelineList(String userId) {
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<TimelineList> callt = apiConfig.selectWrite(userId);
        callt.enqueue(new Callback<TimelineList>() {
            @Override
            public void onResponse(Call<TimelineList> call, Response<TimelineList> response) {
                myTimelineList = response.body().getArrayList();
                infoListAdapter.updatelist(myTimelineList);
            }

            @Override
            public void onFailure(Call<TimelineList> call, Throwable t) {

            }
        });
    }

    public String getUserid() {
        SharedPreferences autoLogin = getActivity().getSharedPreferences("auto", Context.MODE_PRIVATE);
        if (autoLogin.getString("autoId", null) != null) {
            //  자동 로그인일때
            userid = autoLogin.getString("autoId", null);
            return userid;
        } else {
            SharedPreferences noAuto = getActivity().getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            userid = noAuto.getString("noAutoid", null);
            return userid;
        }
    }
}
