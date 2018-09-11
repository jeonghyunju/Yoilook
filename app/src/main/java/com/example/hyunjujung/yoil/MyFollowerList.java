package com.example.hyunjujung.yoil;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.hyunjujung.yoil.apdater.MyFollowerAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.FollowingList;
import com.example.hyunjujung.yoil.vo.MyFollowerVO;
import com.example.hyunjujung.yoil.vo.SelectVO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFollowerList extends AppCompatActivity {
    EditText searchEt;

    RecyclerView followerRecycle;
    RecyclerView.LayoutManager followLayout;
    MyFollowerAdapter followerAdapter;
    ArrayList<MyFollowerVO> followerArray = new ArrayList<>();

    /* 로그인된 사용자 아이디 */
    String loginid;
    String loginProfile;
    String loginName;

    /* 내 팔로잉 리스트 */
    String followingIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_follower_list);
        getLoginid();

        searchEt = (EditText)findViewById(R.id.searchEt);
        followerRecycle = (RecyclerView)findViewById(R.id.followerRecycle);

        followLayout = new LinearLayoutManager(this);
        followerRecycle.setLayoutManager(followLayout);

        /* 현재 로그인된 사용자의 프로필, 이름 DB 에서 가져오기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<SelectVO> selectVOCall = apiConfig.selectmembers(loginid);
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

        ApiConfig apiC = RetrofitCreator.getapiConfig();
        Call<FollowingList> uCall = apiC.selectFollowlist(loginid);
        uCall.enqueue(new Callback<FollowingList>() {
            @Override
            public void onResponse(Call<FollowingList> call, Response<FollowingList> response) {
                followingIds = response.body().getFollowingList();
                ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                Call<List<MyFollowerVO>> mCall = apiConfig.selectFollower(loginid);
                mCall.enqueue(new Callback<List<MyFollowerVO>>() {
                    @Override
                    public void onResponse(Call<List<MyFollowerVO>> call, Response<List<MyFollowerVO>> response) {
                        followerArray.addAll(response.body());
                        followerAdapter = new MyFollowerAdapter(getApplicationContext(), followerArray, followingIds);
                        followerRecycle.setAdapter(followerAdapter);
                    }

                    @Override
                    public void onFailure(Call<List<MyFollowerVO>> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<FollowingList> call, Throwable t) {

            }
        });

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                followerAdapter.filter(searchEt.getText().toString());
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /* Shared 에서 아이디 가져오기 */
    /* 로그인된 사용자 아이디 가져오기 */
    public void getLoginid() {
        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동로그인 일때
            loginid = autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = getSharedPreferences("noAuto", MODE_PRIVATE);
            loginid = noAuto.getString("noAutoid", null);
        }
    }
}
