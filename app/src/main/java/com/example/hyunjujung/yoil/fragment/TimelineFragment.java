package com.example.hyunjujung.yoil.fragment;

import android.content.Context;
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
import com.example.hyunjujung.yoil.apdater.TimelineAdapter;
import com.example.hyunjujung.yoil.paging.EndlessRecyclerViewScrollListener;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.FavoriteList;
import com.example.hyunjujung.yoil.vo.FavoriteVO;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 10. 12..
 */

public class TimelineFragment extends Fragment{
    RecyclerView timelineRecycle;
    TimelineAdapter adapter;
    LinearLayoutManager layoutManager;
    ArrayList<TimelineVO> timelineList = new ArrayList<>();
    ArrayList<FavoriteVO> favoriteList = new ArrayList<>();

    static int pages = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.timeline_fragment, container, false);
        timelineRecycle = (RecyclerView)view.findViewById(R.id.timelineRecycle);

        if(timelineList.size() > 0) {
            timelineList.clear();
        }

        //  DB에 있는 좋아요 리스트 모두 가져오기
        ApiConfig apicon = RetrofitCreator.getapiConfig();
        Call<FavoriteList> callF = apicon.selectFavorite(getUserid());
        callF.enqueue(new Callback<FavoriteList>() {
            @Override
            public void onResponse(Call<FavoriteList> call, Response<FavoriteList> response) {
                favoriteList = response.body().getArrayList();
            }

            @Override
            public void onFailure(Call<FavoriteList> call, Throwable t) {

            }
        });

        pages = 1;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //  list 형태
        layoutManager = new LinearLayoutManager(getContext());
        timelineRecycle.setLayoutManager(layoutManager);

        //  데이터와 view를 연결
        adapter = new TimelineAdapter(getActivity().getApplicationContext(), timelineList, favoriteList);
        timelineRecycle.setAdapter(adapter);

        /* 리사이클러뷰 페이징 처리 */
        timelineRecycle.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                pages++;
                getPaging(pages);
            }
        });

        getPaging(pages);
    }

    /* recyclerview 페이징 */
    private void getPaging(int page) {
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<List<TimelineVO>> calls = apiConfig.selectTimeline(page);
        calls.enqueue(new Callback<List<TimelineVO>>() {
            @Override
            public void onResponse(Call<List<TimelineVO>> call, Response<List<TimelineVO>> response) {
                //  arraylist에 넣었음
                timelineList.addAll(response.body());
                //adapter.updateList(timelineList);
                adapter.notifyItemRangeInserted(adapter.getItemCount(), timelineList.size()-1);
            }

            @Override
            public void onFailure(Call<List<TimelineVO>> call, Throwable t) {

            }
        });
    }

    public String getUserid() {
        SharedPreferences autoLogin = getActivity().getSharedPreferences("auto", Context.MODE_PRIVATE);
        if (autoLogin.getString("autoId", null) != null) {
            //  자동 로그인일때
            return autoLogin.getString("autoId", null);
        } else {
            SharedPreferences noAuto = getActivity().getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            return noAuto.getString("noAutoid", null);
        }
    }
}
