package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.chatting.Chatting;
import com.example.hyunjujung.yoil.fragment.InfoGridFragment;
import com.example.hyunjujung.yoil.fragment.InfoListFragment;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.*;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyInfo extends AppCompatActivity {
    CircleImageView infoProfile;
    TextView infoIdTv, totalCount;
    TabLayout tablayout;
    TableLayout tablelayout;
    TextView followingCount, followerCount, followingTv, followerTv;
    ImageButton todayWeather;

    Intent intent;

    static String userid = "";
    static String userpw = "";
    static String userimg = "";

    /* Fragment */
    private InfoGridFragment infoGridFragment;
    private InfoListFragment infoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_info);
        infoProfile = (CircleImageView) findViewById(R.id.infoProfile);
        infoIdTv = (TextView)findViewById(R.id.infoIdTv);
        tablayout = (TabLayout)findViewById(R.id.tablayout);
        tablelayout = (TableLayout)findViewById(R.id.tablelayout);
        followingCount = (TextView)findViewById(R.id.followingCount);
        followerCount = (TextView)findViewById(R.id.followerCount);
        followingTv = (TextView)findViewById(R.id.followingTv);
        followerTv = (TextView)findViewById(R.id.followerTv);
        todayWeather = (ImageButton)findViewById(R.id.todayWeather);

        bindWidgetWithEvent();
        setupTabLayout();

        //  로그인 시에 저장된 아이디 꺼내기
        SharedPreferences noautoLogin = getSharedPreferences("noAuto", MODE_PRIVATE);
        if(noautoLogin.getString("noAutoid", null) == null) {
            //  자동로그인일때
            SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
            userid = autoLogin.getString("autoId", null);
            userpw = autoLogin.getString("autoPw", null);
            userimg = autoLogin.getString("autoImg", null);
       }else {
            //  자동 로그인 아닐때
            userid = noautoLogin.getString("noAutoid", null);
            userpw = noautoLogin.getString("noAutopw", null);
            userimg = noautoLogin.getString("noAutoImg", null);
        }

        //  꺼내온 아이디로 회원 정보 가져오기
        ApiConfig apicon = RetrofitCreator.getapiConfig();
        Call<SelectVO> call = apicon.selectmembers(userid);
        call.enqueue(new Callback<SelectVO>() {
            @Override
            public void onResponse(Call<SelectVO> call, Response<SelectVO> response) {
                SelectVO selectVO = response.body();
                Glide.with(getApplicationContext()).load("http://13.124.12.50" + selectVO.getProfile()).into(infoProfile);
                infoIdTv.setText(userid);
            }

            @Override
            public void onFailure(Call<SelectVO> call, Throwable t) {

            }
        });

        todayWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), WeatherClothes.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        /* 팔로잉, 팔로워 수 가져오기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<FollowCountVO> fCall = apiConfig.selectcountFollow(userid);
        fCall.enqueue(new Callback<FollowCountVO>() {
            @Override
            public void onResponse(Call<FollowCountVO> call, Response<FollowCountVO> response) {
                followingCount.setText(String.valueOf(response.body().getFollowingCount()));
                followerCount.setText(String.valueOf(response.body().getFollowerCount()));
            }

            @Override
            public void onFailure(Call<FollowCountVO> call, Throwable t) {

            }
        });
    }

    //  로그아웃 메뉴
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
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* 팔로잉, 팔로워 리스트로 이동 */
    public void goFollowingList(View view) {
        //  팔로잉 리스트로 이동한다
        intent = new Intent(this, MyFollowingList.class);
        startActivity(intent);
    }
    public void goFollowerList(View view) {
        //  팔로워 리스트로 이동한다
        intent = new Intent(this, MyFollowerList.class);
        startActivity(intent);
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

            //  정보 수정하기
            case R.id.updateUser:
                intent = new Intent(this, UpdateUser.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;
        }
    }

    /* tabLayout에 프래그먼트 붙이기 */
    private void setupTabLayout() {
        infoGridFragment = new InfoGridFragment();
        infoListFragment = new InfoListFragment();
        tablayout.addTab(tablayout.newTab().setIcon(R.drawable.menu), true);
        tablayout.addTab(tablayout.newTab().setIcon(R.drawable.list));
    }

    /* 탭 클릭 이벤트 (프래그먼트 체인지) */
    private void setCurrentFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(infoGridFragment);
                break;
            case 1:
                replaceFragment(infoListFragment);
                break;
        }
    }

    /* 프래그먼트를 replace 한다 */
    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.framelayout, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    private void bindWidgetWithEvent() {
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
