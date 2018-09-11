package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.OtherInfo;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.ServerResponse;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.MyFollowerVO;
import com.example.hyunjujung.yoil.vo.SelectVO;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 11. 30..
 */

public class MyFollowerAdapter extends RecyclerView.Adapter<MyFollowerAdapter.ViewHolder>{
    Context context;
    ArrayList<MyFollowerVO> followerArray = null;
    ArrayList<MyFollowerVO> filterlist;
    String followingIds;

    String loginid;
    String loginProfile;
    String loginName;

    String message = " 님이 팔로우 했습니다.";

    public MyFollowerAdapter() {
    }

    public MyFollowerAdapter(Context context, ArrayList<MyFollowerVO> followerArray, String followingIds) {
        this.context = context;
        this.followerArray = followerArray;
        this.followingIds = followingIds;
        this.filterlist = new ArrayList<>();
        this.filterlist.addAll(followerArray);

        getLoginid();

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
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.follower_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context).load("http://13.124.12.50" + followerArray.get(position).getFollowerProfile()).into(holder.followerProfile);
        holder.followerId.setText(followerArray.get(position).getFollowerId());
        holder.followerName.setText(followerArray.get(position).getFollowerName());

        /* 내 팔로잉 리스트의 팔로잉 아이디 존재 유무에 따라 버튼 다르게 출력 */
        if(followingIds.contains(followerArray.get(position).getFollowerId())) {
            holder.addfollowBtn.setVisibility(View.GONE);
            holder.cancelfollowBtn.setVisibility(View.VISIBLE);
        }else {
            holder.addfollowBtn.setVisibility(View.VISIBLE);
            holder.cancelfollowBtn.setVisibility(View.GONE);
        }

        /* 프로필 누르면 그 사람 타임라인으로 이동 */
        holder.followerProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), OtherInfo.class);
                intent.putExtra("writeId", followerArray.get(position).getUserid());
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        /* 팔로워 목록에서 삭제 */
        holder.deletefollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener deleteFollow = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* 팔로워 목록에서 삭제 */
                        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                        Call<ServerResponse> dCall = apiConfig.deleteFollowerList(loginid, followerArray.get(position).getFollowerId());
                        dCall.enqueue(new Callback<ServerResponse>() {
                            @Override
                            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                Log.e("팔로워 목록 삭제 성공", response.body().getMessage());

                            }

                            @Override
                            public void onFailure(Call<ServerResponse> call, Throwable t) {
                                Log.e("팔로워 목록 삭제 실패", t.getMessage());
                            }
                        });
                    }
                };
                DialogInterface.OnClickListener cancelDelete = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* 취소 */
                        dialogInterface.dismiss();
                    }
                };
                new AlertDialog.Builder(view.getContext())
                        .setTitle("알림")
                        .setMessage("팔로워를 삭제하시겠습니까?")
                        .setNeutralButton("취소", cancelDelete)
                        .setNegativeButton("삭제", deleteFollow)
                        .show();
            }
        });

        /* 팔로잉 삭제 */
        holder.cancelfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.cancelfollowBtn.setVisibility(View.GONE);
                holder.addfollowBtn.setVisibility(View.VISIBLE);

                ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                Call<ServerResponse> cCall = apiConfig.deleteFollows(loginid, followerArray.get(position).getFollowerId());
                cCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        Log.e("팔로잉 삭제 성공", response.body().getMessage());
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Log.e("팔로잉 삭제 실패", t.getMessage());
                    }
                });

                Toast.makeText(context.getApplicationContext(),
                        followerArray.get(position).getFollowerId() + " 님의 팔로우를 취소했습니다",
                        Toast.LENGTH_SHORT).show();
            }
        });

        /* 팔로잉 추가 & fcm */
        holder.addfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.cancelfollowBtn.setVisibility(View.VISIBLE);
                holder.addfollowBtn.setVisibility(View.GONE);

                ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                Call<ServerResponse> sCall = apiConfig.insertFollows(loginid, loginProfile, loginName,
                        followerArray.get(position).getFollowerId(), followerArray.get(position).getFollowerProfile(), followerArray.get(position).getFollowerName());
                sCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        Log.e("팔로잉 추가 완료", response.body().getMessage());
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Log.e("팔로잉 추가 실패", t.getMessage());
                    }
                });

                /* fcm */
                apiConfig = RetrofitCreator.getapiConfig();
                Call<ServerResponse> fCall = apiConfig.sendfcm(followerArray.get(position).getFollowerId(), loginid, message);
                fCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {

                    }
                });

                Toast.makeText(context.getApplicationContext(),
                        followerArray.get(position).getFollowerId() + " 님을 팔로우 했습니다",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return followerArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView followerProfile;
        TextView followerId, followerName;
        ImageButton deletefollow;
        Button cancelfollowBtn, addfollowBtn;
        public ViewHolder (View view) {
            super(view);
            followerProfile = (CircleImageView)view.findViewById(R.id.followerProfile);
            followerId = (TextView)view.findViewById(R.id.followerId);
            followerName = (TextView)view.findViewById(R.id.followerName);
            deletefollow = (ImageButton)view.findViewById(R.id.deletefollow);
            cancelfollowBtn = (Button)view.findViewById(R.id.cancelfollowBtn);
            addfollowBtn = (Button)view.findViewById(R.id.addfollowBtn);
        }
    }

    public void setFollowerlist(ArrayList<MyFollowerVO> followerlist, String followings) {
        this.followingIds = followings;
        this.followerArray = followerlist;
        notifyDataSetChanged();
    }

    /* Shared 에서 아이디 가져오기 */
    /* 로그인된 사용자 아이디 가져오기 */
    public void getLoginid() {
        SharedPreferences autoLogin = context.getSharedPreferences("auto", Context.MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동로그인 일때
            loginid = autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = context.getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            loginid = noAuto.getString("noAutoid", null);
        }
    }

    /* 검색 기능 */
    public void filter(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());
        followerArray.clear();
        if(searchText.length() == 0) {
            followerArray.addAll(filterlist);
        }else {
            for(MyFollowerVO followerVO : filterlist) {
                if(followerVO.getFollowerId().contains(searchText)) {
                    followerArray.add(followerVO);
                }
            }
        }
        notifyDataSetChanged();
    }
}
