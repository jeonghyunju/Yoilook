package com.example.hyunjujung.yoil.fragment;

import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.apdater.WeekCodiYoilAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.WeekCodiVO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 10. 12..
 */

public class WeeklylookFragment extends Fragment {
    RecyclerView weekRecycle;
    RecyclerView.LayoutManager weekLayout;
    WeekCodiYoilAdapter weekAdapter;

    ArrayList<Integer> backArray = new ArrayList<>();
    ArrayList<String> yoilArray = new ArrayList<>();

    ArrayList<WeekCodiVO> dayArray = new ArrayList<>();

    ArrayList<String> weekdayArray = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weeklylook_fragment, container, false);

        weekRecycle = (RecyclerView)view.findViewById(R.id.weekRecycle);

        int[] backs = {R.drawable.back_1, R.drawable.back_2, R.drawable.back_3, R.drawable.back_4, R.drawable.back_5, R.drawable.back_6, R.drawable.back_7};
        String[] yoil = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};

        if(backArray.size() > 0) {
            backArray.clear();
            yoilArray.clear();
        }

        for(int back : backs) {
            backArray.add(back);
        }
        for(String str : yoil) {
            yoilArray.add(str);
        }

        if(weekdayArray.size() > 0) {
            weekdayArray.clear();
        }

        if(dayArray.size() > 0) {
            dayArray.clear();
        }

        weekLayout = new LinearLayoutManager(getContext());
        weekRecycle.setLayoutManager(weekLayout);

        weekAdapter = new WeekCodiYoilAdapter(getContext(), backArray, yoilArray, weekdayArray);
        weekRecycle.setAdapter(weekAdapter);

        /* 오늘 날짜에서 그 주 월요일 구하기 */
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        Log.d("이번주 월요일", sdf.format(calendar.getTime()));

        /* DB에서 그 주 월요일에 해당하는 인덱스부터 7개까지 날짜 모두 가져오기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<List<WeekCodiVO>> callweek = apiConfig.selectWeekcodi(sdf.format(calendar.getTime()));
        callweek.enqueue(new Callback<List<WeekCodiVO>>() {
            @Override
            public void onResponse(Call<List<WeekCodiVO>> call, Response<List<WeekCodiVO>> response) {
                if(weekdayArray.size() > 0) {
                    weekdayArray.clear();
                }
                dayArray.addAll(response.body());
                for(int i=0 ; i<dayArray.size() ; i++) {
                    weekdayArray.add(dayArray.get(i).getYmd());
                }
                Log.d("데이 어레이", weekdayArray.toString());
            }

            @Override
            public void onFailure(Call<List<WeekCodiVO>> call, Throwable t) {
                Log.d("실패", t.getMessage());
            }
        });
        return view;
    }

}
