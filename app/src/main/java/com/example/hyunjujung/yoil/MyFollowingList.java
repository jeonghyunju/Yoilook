package com.example.hyunjujung.yoil;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.example.hyunjujung.yoil.apdater.MyFollowingAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.MyFollowingVO;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFollowingList extends AppCompatActivity {
    EditText searchEt;

    RecyclerView followingRecycle;
    RecyclerView.LayoutManager followingLayout;
    MyFollowingAdapter followingAdapter;
    ArrayList<MyFollowingVO> followingArray = new ArrayList<>();

    String loginid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_following_list);

        searchEt = (EditText)findViewById(R.id.searchEt);
        followingRecycle = (RecyclerView)findViewById(R.id.followingRecycle);

        followingLayout = new LinearLayoutManager(this);
        followingRecycle.setLayoutManager(followingLayout);

        getLoginid();

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //String filterText = searchEt.getText().toString().toLowerCase(Locale.getDefault());
                followingAdapter.filter(searchEt.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /* 내 팔로잉 리스트 불러오기 */
        ApiConfig apicon = RetrofitCreator.getapiConfig();
        Call<List<MyFollowingVO>> mCall = apicon.selectFollowings(loginid);
        mCall.enqueue(new Callback<List<MyFollowingVO>>() {
            @Override
            public void onResponse(Call<List<MyFollowingVO>> call, Response<List<MyFollowingVO>> response) {
                followingArray.addAll(response.body());
                followingAdapter = new MyFollowingAdapter(getApplicationContext(), followingArray);
                followingRecycle.setAdapter(followingAdapter);
            }

            @Override
            public void onFailure(Call<List<MyFollowingVO>> call, Throwable t) {

            }
        });
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
