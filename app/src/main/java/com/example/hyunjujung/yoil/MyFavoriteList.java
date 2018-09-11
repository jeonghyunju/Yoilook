package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.hyunjujung.yoil.apdater.MyfavoriteAdapter;
import com.example.hyunjujung.yoil.chatting.Chatting;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.TimelineList;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavoriteList extends AppCompatActivity {
    Intent intent;
    RecyclerView favoriteRecycle;
    MyfavoriteAdapter favoriteAdapter;
    RecyclerView.LayoutManager favoriteLayout;
    ArrayList<TimelineVO> favoriteList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_favorite_list);
        favoriteRecycle = (RecyclerView)findViewById(R.id.favoriteRecycle);

        //  리사이클러뷰에 좋아요 누른 게시물 모두 가져오기
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<TimelineList> call = apiConfig.selectMyfavorite(getLoginid());
        call.enqueue(new Callback<TimelineList>() {
            @Override
            public void onResponse(Call<TimelineList> call, Response<TimelineList> response) {
                //  넘어온 데이터 arraylist에 넣는다
                favoriteList = response.body().getArrayList();

                //  recyclerview 형태 지정
                favoriteLayout = new GridLayoutManager(getApplicationContext(), 3);
                favoriteRecycle.setLayoutManager(favoriteLayout);

                //  데이터와 뷰를 연결한다
                favoriteAdapter = new MyfavoriteAdapter(getApplicationContext(), favoriteList);
                favoriteRecycle.setAdapter(favoriteAdapter);
            }

            @Override
            public void onFailure(Call<TimelineList> call, Throwable t) {

            }
        });

    }

    //  하단 메뉴 버튼 클릭 이벤트
    public void clickMenu(View view) {
        int id = view.getId();
        switch (id) {
            //  홈버튼
            case R.id.home:
                intent = new Intent(this, YoilMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            //  타임라인에 글쓰기 버튼
            case R.id.addtimeline:
                intent = new Intent(this, WriteTimeline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            //  채팅하기 버튼
            case R.id.chatting:
                intent = new Intent(this, Chatting.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            //  내 정보 버튼
            case R.id.myinfo:
                intent = new Intent(this, MyInfo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

        }
    }

    //  로그인 아이디 가져오기
    public String getLoginid() {
        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동로그인 일때
            return autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = getSharedPreferences("noAuto", MODE_PRIVATE);
            return noAuto.getString("noAutoid", null);
        }
    }
}
