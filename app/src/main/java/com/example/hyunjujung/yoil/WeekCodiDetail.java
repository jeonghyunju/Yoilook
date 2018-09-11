package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.apdater.DailySubimgAdapter;
import com.example.hyunjujung.yoil.apdater.DailyTagAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.DailyCodiVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeekCodiDetail extends AppCompatActivity {
    ImageView dailyImg, favoriteOn;
    //TextView dayofweekTv;
    ImageButton dailyFavorite;

    /* 서브 이미지 Recyclerview */
    RecyclerView dailyRecycle;
    RecyclerView.LayoutManager dailyLayout;
    DailySubimgAdapter subimgAdapter;
    ArrayList<String> subArrayList = new ArrayList<>();

    /* tag Recyclerview */
    RecyclerView dailyTagRecycle;
    RecyclerView.LayoutManager tagLayout;
    DailyTagAdapter tagAdapter;
    ArrayList<String> tagArray = new ArrayList<>();

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_codi_detail);

        dailyImg = (ImageView)findViewById(R.id.dailyImg);
        favoriteOn = (ImageView)findViewById(R.id.favoriteOn);
        //dayofweekTv = (TextView)findViewById(R.id.dayofweekTv);
        dailyFavorite = (ImageButton)findViewById(R.id.dailyFavorite);

        dailyRecycle = (RecyclerView)findViewById(R.id.dailyRecycle);
        dailyTagRecycle = (RecyclerView)findViewById(R.id.dailyTagRecycle);

        dailyLayout = new GridLayoutManager(this, 2);
        dailyRecycle.setLayoutManager(dailyLayout);

        tagLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dailyTagRecycle.setLayoutManager(tagLayout);

        intent = getIntent();

        /* 인텐트로 넘겨 받은 daily codi 인덱스를 이용해서 daily codi 한 건 불러오기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<DailyCodiVO> codiCall = apiConfig.selectDailycodi(intent.getStringExtra("weekYmd"));
        codiCall.enqueue(new Callback<DailyCodiVO>() {
            @Override
            public void onResponse(Call<DailyCodiVO> call, Response<DailyCodiVO> response) {
                DailyCodiVO dailyCodiVO = response.body();
                String[] subImages = dailyCodiVO.getSubimage().split("-");
                String[] tags = dailyCodiVO.getTag().split("-");

                /* 서브 이미지 split */
                for(int i=0 ; i<subImages.length ; i++) {
                    subArrayList.add(subImages[i]);
                }
                subimgAdapter = new DailySubimgAdapter(getApplicationContext(), subArrayList);
                dailyRecycle.setAdapter(subimgAdapter);

                /* tag split */
                for(int i=0 ; i<tags.length ; i++) {
                    tagArray.add(tags[i]);
                }
                tagAdapter = new DailyTagAdapter(getApplicationContext(), tagArray, false);
                dailyTagRecycle.setAdapter(tagAdapter);

                Glide.with(getApplicationContext()).load("http://13.124.12.50" + dailyCodiVO.getMainimage()).into(dailyImg);
                /*switch (dailyCodiVO.getWeekday()) {
                    case "월":
                        dayofweekTv.setText("MON");
                        break;
                    case "화":
                        dayofweekTv.setText("TUE");
                        break;
                    case "수":
                        dayofweekTv.setText("WED");
                        break;
                    case "목":
                        dayofweekTv.setText("THU");
                        break;
                    case "금":
                        dayofweekTv.setText("FRI");
                        break;
                    case "토":
                        dayofweekTv.setText("SAT");
                        break;
                    case "일":
                        dayofweekTv.setText("SUN");
                        break;
                }*/

            }

            @Override
            public void onFailure(Call<DailyCodiVO> call, Throwable t) {

            }
        });
    }
}
