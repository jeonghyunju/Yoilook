package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.ServerResponse;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.MyFollowingVO;

import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 11. 30..
 */

public class MyFollowingAdapter extends RecyclerView.Adapter<MyFollowingAdapter.ViewHolder>{
    Context context;
    ArrayList<MyFollowingVO> followingArray = null;
    ArrayList<MyFollowingVO> filterlist;

    String loginid;

    public MyFollowingAdapter() {
    }

    public MyFollowingAdapter(Context context, ArrayList<MyFollowingVO> followingArray) {
        this.context = context;
        this.followingArray = followingArray;
        this.filterlist = new ArrayList<MyFollowingVO>();
        this.filterlist.addAll(followingArray);

        getLoginid();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.following_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context)
                .load("http://13.124.12.50" + followingArray.get(position).getFollowingProfile())
                .into(holder.followingProfile);
        holder.followingId.setText(followingArray.get(position).getFollowingId());
        holder.followingName.setText(followingArray.get(position).getFollowingName());

        /* 팔로잉 취소 */
        holder.cancelfollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener deleteFollowing = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /* 팔로잉 취소 */
                        followingArray.remove(position);
                        notifyDataSetChanged();

                        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                        Call<ServerResponse> cCall = apiConfig.deleteFollows(loginid, followingArray.get(position).getFollowingId());
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
                    }
                };
                DialogInterface.OnClickListener cancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };
                new AlertDialog.Builder(view.getContext())
                        .setTitle("알림")
                        .setMessage("팔로잉을 취소하시겠습니까?")
                        .setNeutralButton("취소", cancel)
                        .setNegativeButton("삭제", deleteFollowing)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return followingArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView followingProfile;
        TextView followingId, followingName;
        Button cancelfollowBtn;

        public ViewHolder(View view) {
            super(view);
            followingProfile = (CircleImageView)view.findViewById(R.id.followingProfile);
            followingId = (TextView)view.findViewById(R.id.followingId);
            followingName = (TextView)view.findViewById(R.id.followingName);
            cancelfollowBtn = (Button)view.findViewById(R.id.cancelfollowBtn);
        }
    }

    public void setFollowingList(ArrayList<MyFollowingVO> followings) {
        this.followingArray = followings;
        notifyDataSetChanged();
    }

    /* 검색 기능 */
    public void filter(String searchText) {
        searchText = searchText.toLowerCase(Locale.getDefault());
        followingArray.clear();
        if(searchText.length() == 0) {
            followingArray.addAll(filterlist);
        }else {
            for(MyFollowingVO followingVO : filterlist) {
                if(followingVO.getFollowingId().contains(searchText)) {
                    followingArray.add(followingVO);
                }
            }
        }
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
}
