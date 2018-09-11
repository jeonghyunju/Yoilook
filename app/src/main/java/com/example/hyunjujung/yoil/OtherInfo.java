package com.example.hyunjujung.yoil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.chatting.Chatting;
import com.example.hyunjujung.yoil.fragment.InfoGridFragment;
import com.example.hyunjujung.yoil.fragment.InfoListFragment;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.FollowingList;
import com.example.hyunjujung.yoil.vo.SelectVO;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtherInfo extends AppCompatActivity {
    CircleImageView infoProfile;
    TextView infoIdTv;
    ImageButton followUser, followCancel;
    ImageView followIcon, addfollowIcon, folloDoneIcon, followCancelIcon;
    TabLayout tablayout;

    Intent intent;

    /* 팔로잉할 유저 아이디, 프로필, 이름 */
    String writeIds;
    String writeProfile;
    String writeName;

    /* 로그인된 사용자 프로필, 이름 */
    String loginProfile;
    String loginName;

    /* 로그인된 사용자 팔로워, 팔로잉 리스트 */
    //ArrayList<UserFollowVO> followArray = new ArrayList<>();
    String followinglist;

    /* Fragment */
    private InfoGridFragment infoGridFragment;
    private InfoListFragment infoListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.other_info);

        infoProfile = (CircleImageView)findViewById(R.id.infoProfile);
        infoIdTv = (TextView)findViewById(R.id.infoIdTv);
        followUser = (ImageButton)findViewById(R.id.followUser);
        followCancel = (ImageButton)findViewById(R.id.followCancel);
        followIcon = (ImageView)findViewById(R.id.followIcon);
        addfollowIcon = (ImageView)findViewById(R.id.addfollowIcon);
        folloDoneIcon = (ImageView)findViewById(R.id.folloDoneIcon);
        followCancelIcon = (ImageView)findViewById(R.id.followCancelIcon);
        tablayout = (TabLayout)findViewById(R.id.tablayout);

        bindWidgetWithEvent();
        setupTabLayout();

        intent = getIntent();
        /* 게시물 클릭해서 넘어온 id 로 프로필 조회 */
        if(intent.getStringExtra("writeId") != null) {
            writeIds = intent.getStringExtra("writeId");
            ApiConfig apiConfig = RetrofitCreator.getapiConfig();
            Call<SelectVO> selectVOCall = apiConfig.selectmembers(writeIds);
            selectVOCall.enqueue(new Callback<SelectVO>() {
                @Override
                public void onResponse(Call<SelectVO> call, Response<SelectVO> response) {
                    SelectVO selectVO = response.body();
                    writeProfile = selectVO.getProfile();
                    writeName = selectVO.getUsername();
                    Glide.with(getApplicationContext()).load("http://13.124.12.50" + selectVO.getProfile()).into(infoProfile);
                    infoIdTv.setText(selectVO.getUserid());
                }

                @Override
                public void onFailure(Call<SelectVO> call, Throwable t) {

                }
            });
        }

        /* 내 팔로워, 팔로잉 리스트 DB 에서 조회 */
        ApiConfig api = RetrofitCreator.getapiConfig();
        Call<FollowingList> userFollowVOCall = api.selectFollowlist(getUserid());
        userFollowVOCall.enqueue(new Callback<FollowingList>() {
            @Override
            public void onResponse(Call<FollowingList> call, Response<FollowingList> response) {
                followinglist = response.body().getFollowingList();
                Log.e("팔로잉 리스트", followinglist);
                if(followinglist.toString().contains(writeIds)) {
                    /* 내 팔로잉 리스트에 존재하면 */
                    followUser.setVisibility(View.GONE);
                    followIcon.setVisibility(View.GONE);
                    addfollowIcon.setVisibility(View.GONE);
                    followCancel.setVisibility(View.VISIBLE);
                    folloDoneIcon.setVisibility(View.VISIBLE);
                    followCancelIcon.setVisibility(View.VISIBLE);
                }else {
                    /* 내 팔로잉 리스트에 존재하지 않으면 */
                    followUser.setVisibility(View.VISIBLE);
                    followIcon.setVisibility(View.VISIBLE);
                    addfollowIcon.setVisibility(View.VISIBLE);
                    followCancel.setVisibility(View.GONE);
                    folloDoneIcon.setVisibility(View.GONE);
                    followCancelIcon.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<FollowingList> call, Throwable t) {

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

    /* 팔로우 신청 버튼 */
    public void followUsers(View view) {
        followUser.setVisibility(View.GONE);
        followIcon.setVisibility(View.GONE);
        addfollowIcon.setVisibility(View.GONE);
        followCancel.setVisibility(View.VISIBLE);
        folloDoneIcon.setVisibility(View.VISIBLE);
        followCancelIcon.setVisibility(View.VISIBLE);

        String message = " 님이 팔로우 했습니다.";

        /* 팔로우 추가
         * DB 에 저장, fcm 알림 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<ServerResponse> serverResponseCall = apiConfig.insertFollows(getUserid(), loginProfile, loginName, writeIds, writeProfile, writeName);
        serverResponseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.e("팔로잉 추가 성공", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("팔로잉 추가 실패", t.getMessage());
            }
        });
        /* fcm 알림 */
        apiConfig = RetrofitCreator.getapiConfig();
        Call<ServerResponse> s = apiConfig.sendfcm(writeIds, getUserid(), message);
        s.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });

        Toast.makeText(this, writeIds + " 님을 팔로우 했습니다", Toast.LENGTH_SHORT).show();
    }

    /* 팔로우 취소 버튼 */
    public void cancelFollow(View view) {
        followUser.setVisibility(View.VISIBLE);
        followIcon.setVisibility(View.VISIBLE);
        addfollowIcon.setVisibility(View.VISIBLE);
        followCancel.setVisibility(View.GONE);
        folloDoneIcon.setVisibility(View.GONE);
        followCancelIcon.setVisibility(View.GONE);

        /* DB 에서 삭제 */
        ApiConfig api = RetrofitCreator.getapiConfig();
        Call<ServerResponse> scall = api.deleteFollows(getUserid(), writeIds);
        scall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.e("팔로잉 삭제 성공", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("팔로잉 삭제 실패", t.getMessage());
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
        }
    }

    //  shared에 저장된 아이디 가져오기
    public String getUserid() {
        SharedPreferences autoLogin = getSharedPreferences("auto", Context.MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동 로그인일때
            return autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            return noAuto.getString("noAutoid", null);
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

    @Override
    protected void onResume() {
        super.onResume();

        /* 현재 로그인된 사용자의 프로필, 이름 DB 에서 가져오기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<SelectVO> selectVOCall = apiConfig.selectmembers(getUserid());
        selectVOCall.enqueue(new Callback<SelectVO>() {
            @Override
            public void onResponse(Call<SelectVO> call, Response<SelectVO> response) {
                SelectVO selectVO = response.body();
                loginProfile = selectVO.getProfile();
                loginName = selectVO.getUsername();
            }

            @Override
            public void onFailure(Call<SelectVO> call, Throwable t) {

            }
        });
    }
}
