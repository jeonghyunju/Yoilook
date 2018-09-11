package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.hyunjujung.yoil.apdater.MyPagerAdapter;
import com.example.hyunjujung.yoil.chatting.Chatting;
import com.example.hyunjujung.yoil.chatting.ChattingRoom;
import com.example.hyunjujung.yoil.fragment.DailylookFragment;
import com.example.hyunjujung.yoil.fragment.TimelineFragment;
import com.example.hyunjujung.yoil.fragment.WeeklylookFragment;

public class YoilMain extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    MyPagerAdapter adapter;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yoil_main);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        viewPager = (ViewPager)findViewById(R.id.viewpager);

        //  viewpager 애니메이션 주기
        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                float nomalize = Math.abs(1 - Math.abs(position));
                page.setAlpha(nomalize);
                page.setScaleX(nomalize/2 + 0.5f);
                page.setScaleY(nomalize/2 + 0.5f);
                page.setRotationY(position * 80);
            }
        });

        //  프래그먼트 배열에 프래그먼트 넣기
        Fragment[] arrFragment = new Fragment[3];
        arrFragment[0] = new TimelineFragment();
        arrFragment[1] = new DailylookFragment();
        arrFragment[2] = new WeeklylookFragment();

        //  뷰페이저에 프래그먼트 붙이기
        adapter = new MyPagerAdapter(getSupportFragmentManager(), arrFragment);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.logoutmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                //  로그아웃
                SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
                SharedPreferences.Editor autoLoginEdit = autoLogin.edit();
                autoLoginEdit.clear();
                autoLoginEdit.commit();
                SharedPreferences noAuto = getSharedPreferences("noAuto", MODE_PRIVATE);
                SharedPreferences.Editor noAutoEdit = noAuto.edit();
                noAutoEdit.clear();
                noAutoEdit.commit();
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //  하단 메뉴 클릭 이벤트
    public void clickMenu(View view) {
        int id = view.getId();
        switch (id) {
            //  홈버튼
            case R.id.home:
                break;
            //  favorite 버튼
            case R.id.favorite:
                intent = new Intent(this, MyFavoriteList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
            //  타임라인에 글쓰기 버튼
            case R.id.addtimeline:
                intent = new Intent(this, WriteTimeline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                //finish();
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

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

}
