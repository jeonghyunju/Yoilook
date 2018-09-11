package com.example.hyunjujung.yoil.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.InsertDailyCodi;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.UpdateDailyCodi;
import com.example.hyunjujung.yoil.apdater.DailySubimgAdapter;
import com.example.hyunjujung.yoil.apdater.DailyTagAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.DailyCodiVO;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 10. 12..
 */

public class DailylookFragment extends Fragment{
    ImageView daysImg, dailyImg, favoriteOn;
    ImageButton dailyFavorite;

    /* tag RecyclerView */
    RecyclerView dailyTagRecycle;
    RecyclerView.LayoutManager dailyTagLayout;
    DailyTagAdapter tagAdapter;
    ArrayList<String> tagArrayList = new ArrayList<>();

    /* FloatingActionBar */
    FloatingActionMenu floatingMenu;
    FloatingActionButton addDaily, updateDaily;

    /* 서브 이미지 RecyclerView */
    RecyclerView dailyRecycle;
    RecyclerView.LayoutManager dailyLayout;
    DailySubimgAdapter subimgAdapter;
    ArrayList<String> subArrayList = new ArrayList<>();

    /* shared login id */
    static String loginId = "";
    Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dailylook_fragment, container, false);
        daysImg = (ImageView)view.findViewById(R.id.daysImg);
        dailyImg = (ImageView)view.findViewById(R.id.dailyImg);
        favoriteOn = (ImageView)view.findViewById(R.id.favoriteOn);
        dailyFavorite = (ImageButton)view.findViewById(R.id.dailyFavorite);
        dailyRecycle = (RecyclerView)view.findViewById(R.id.dailyRecycle);
        dailyTagRecycle = (RecyclerView)view.findViewById(R.id.dailyTagRecycle);

        /* FloatingActionBar */
        floatingMenu = (FloatingActionMenu)view.findViewById(R.id.floatingMenu);
        addDaily = (FloatingActionButton)view.findViewById(R.id.addDaily);
        updateDaily = (FloatingActionButton)view.findViewById(R.id.updateDaily);

        /* recyclerview layout */
        dailyLayout = new GridLayoutManager(getContext().getApplicationContext(), 2);
        dailyRecycle.setLayoutManager(dailyLayout);

        /* 좋아요 버튼에 태그 주기 */
        dailyFavorite.setTag("favoriteOff");

        //  관리자 아닐때 floatingActionBar 숨기기
        if(!getLoginId().equals("admin")) {
            floatingMenu.setVisibility(View.INVISIBLE);
        }

        if(tagArrayList.size() > 0) {
            tagArrayList.clear();
        }
        if(subArrayList.size() > 0) {
            subArrayList.clear();
        }

        /* 오늘 날짜 구하기 */
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd");
        final Calendar calendar = Calendar.getInstance();

        /* 관리자 아이디 일때만 FloationActionBar 보이기 */
        /* FloatingActionBar에서 추가, 수정, 삭제 버튼 클릭 이벤트 */
        addDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 추가
                 * 임시 저장되어있는 파일이 있는지 검사한다.
                 * 있으면 임시저장 파일을 불러오는 플래그를 intent를 통해 전달한다 */
                final SharedPreferences tempShared = getContext().getSharedPreferences("tempSave", Context.MODE_PRIVATE);
                if(tempShared.getString("tempDaily", null) != null) {
                    DialogInterface.OnClickListener tempSaveOk = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            intent = new Intent(getContext(), InsertDailyCodi.class);
                            intent.putExtra("tempFlag", "true");
                            startActivity(intent);
                        }
                    };
                    DialogInterface.OnClickListener noTempsave = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            /* 임시 저장 파일 안불러올땐 지운다 */
                            tempShared.edit().clear().commit();
                            intent = new Intent(getContext(), InsertDailyCodi.class);
                            startActivity(intent);
                        }
                    };
                    new AlertDialog.Builder(getContext())
                            .setTitle("알림")
                            .setMessage("임시 저장된 데이터가 있습니다.\n불러오시겠습니까?")
                            .setNegativeButton("아니오", noTempsave)
                            .setPositiveButton("예", tempSaveOk)
                            .show();
                }else {
                    intent = new Intent(getContext(), InsertDailyCodi.class);
                    startActivity(intent);
                }
            }
        });
        updateDaily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* 수정 */
                intent = new Intent(getContext(), UpdateDailyCodi.class);
                intent.putExtra("todayDate", sdf.format(calendar.getTime()));
                startActivity(intent);
            }
        });

        /* 좋아요 버튼 누르면 하트애니메이션 나오기 */
        dailyFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dailyFavorite.getTag().equals("favoriteOff")) {
                    //  좋아요 버튼 누르기
                    //  DB에 저장
                    favoriteAnim();
                    dailyFavorite.setImageResource(R.drawable.colorfavorite);
                    dailyFavorite.setTag("favoriteOn");
                    Toast.makeText(getContext().getApplicationContext(), "좋아요를 눌렀습니다", Toast.LENGTH_SHORT).show();
                    invisibleF();
                }else if(dailyFavorite.getTag().equals("favoriteOn")) {
                    //  좋아요 버튼 삭제
                    //  DB에서 삭제
                    dailyFavorite.setImageResource(R.drawable.favorite);
                    dailyFavorite.setTag("favoriteOff");
                    Toast.makeText(getContext().getApplicationContext(), "좋아요를 취소했습니다", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    /* Shared에서 아이디 가져오기 */
    public String getLoginId() {
        SharedPreferences autoLogin = getContext().getSharedPreferences("auto", Context.MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동 로그인 일때
            loginId = autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = getContext().getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            loginId = noAuto.getString("noAutoid", null);
        }
        return loginId;
    }

    @Override
    public void onResume() {
        super.onResume();
        /* 오늘 날짜 구하기 */
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMd");
        final Calendar calendar = Calendar.getInstance();

        /* 오늘 날짜에 맞는 코디 보이기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<DailyCodiVO> codiVOCall = apiConfig.selectDailycodi(sdf.format(calendar.getTime()));
        codiVOCall.enqueue(new Callback<DailyCodiVO>() {
            @Override
            public void onResponse(Call<DailyCodiVO> call, Response<DailyCodiVO> response) {
                DailyCodiVO dailyCodiVO = response.body();
                String[] subImages = dailyCodiVO.getSubimage().split("-");
                String[] tags = dailyCodiVO.getTag().split("-");

                /* 서브 이미지 split */
                for(int i=0 ; i<subImages.length ; i++) {
                    subArrayList.add(subImages[i]);
                }
                subimgAdapter = new DailySubimgAdapter(getContext(), subArrayList);
                dailyRecycle.setAdapter(subimgAdapter);

                /* 태그 split */
                for(int i=0 ; i<tags.length ; i++) {
                    tagArrayList.add(tags[i]);
                }
                tagAdapter = new DailyTagAdapter(getContext(), tagArrayList, false);
                dailyTagRecycle.setAdapter(tagAdapter);

                Glide.with(getContext()).load("http://13.124.12.50" + dailyCodiVO.getMainimage()).into(dailyImg);
                String dayofWeek = dailyCodiVO.getWeekday();
                switch (dayofWeek) {
                    case "일":
                        daysImg.setImageResource(R.drawable.sunday);
                        break;
                    case "월":
                        daysImg.setImageResource(R.drawable.monday);
                        break;
                    case "화":
                        daysImg.setImageResource(R.drawable.tuesday);
                        break;
                    case "수":
                        daysImg.setImageResource(R.drawable.wednesday);
                        break;
                    case "목":
                        daysImg.setImageResource(R.drawable.thursday);
                        break;
                    case "금":
                        daysImg.setImageResource(R.drawable.friday);
                        break;
                    case "토":
                        daysImg.setImageResource(R.drawable.saturday);
                        break;
                }

            }

            @Override
            public void onFailure(Call<DailyCodiVO> call, Throwable t) {

            }
        });
    }

    /* 좋아요 버튼 누르면 나오는 하트 애니메이션 */
    public void favoriteAnim() {
        favoriteOn.setVisibility(View.VISIBLE);
        Animation favorAnim = AnimationUtils.loadAnimation(getContext().getApplicationContext(), R.anim.favorite_anim);
        favoriteOn.setAnimation(favorAnim);
    }

    public void invisibleF() {
        favoriteOn.setVisibility(View.INVISIBLE);
    }

}
