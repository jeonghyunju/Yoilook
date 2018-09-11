package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.MyInfo;
import com.example.hyunjujung.yoil.OtherInfo;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.Recomment;
import com.example.hyunjujung.yoil.ServerResponse;
import com.example.hyunjujung.yoil.SelectFriend;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.FavoriteVO;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by hyunjujung on 2017. 10. 16..
 */

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {
    Context context;
    ArrayList<TimelineVO> timelineList;
    ArrayList<FavoriteVO> favoriteList;
    int lastPosition = -1;

    public TimelineAdapter(Context context, ArrayList<TimelineVO> timelineList, ArrayList<FavoriteVO> favoriteList) {
        this.context = context;
        this.timelineList = timelineList;
        this.favoriteList = favoriteList;
    }

    //  새로운 뷰 생성
    @Override
    public TimelineAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_timeline, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    //  getView
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        Glide.with(context).load("http://13.124.12.50" + timelineList.get(position).getWriteprofile()).into(viewHolder.writeProfile);
        viewHolder.writeid.setText(timelineList.get(position).getWriteid());
        viewHolder.writedate.setText(timelineList.get(position).getWritedate());
        viewHolder.writecontent.setText(timelineList.get(position).getWritecontent());
        if(timelineList.get(position).getWriteImg() == null) {
            //  게시물 올릴때 사진 안올렸으면
            viewHolder.writeImg.setVisibility(View.INVISIBLE);
        }else {
            Glide.with(context).load("http://13.124.12.50" + timelineList.get(position).getWriteImg()).into(viewHolder.writeImg);
        }
        viewHolder.favoriteC.setText(String.valueOf(timelineList.get(position).getFavoriteCount()));
        viewHolder.commentC.setText(String.valueOf(timelineList.get(position).getCommentCount()));
        viewHolder.contentType.setText(timelineList.get(position).getWritetype());
        viewHolder.favoriteBtn.setImageResource(R.drawable.favorite);

        /* 내가 좋아요 누른 게시글에 표시하기... */
        for(int i=0 ; i<favoriteList.size() ; i++) {
            if(favoriteList.get(i).getFavoriteIdx() == timelineList.get(position).getIdx()) {
                Log.d("좋아요", "" + favoriteList.get(i).getFavoriteIdx());
                viewHolder.favoriteBtn.setImageResource(R.drawable.colorfavorite);
                viewHolder.favoriteBtn.setTag("favoriteOn");
            }
        }

        /* 게시자 프로필 사진 클릭 시에 게시자 타임라인으로 이동 */
        viewHolder.writeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(timelineList.get(position).getWriteid().equals(getUserid())) {
                    /* 게시자가 로그인된 사용자일때 */
                    Intent goMe = new Intent(context.getApplicationContext(), MyInfo.class);
                    goMe.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(goMe);
                }else {
                    /* 게시자가 로그인된 사용자와 다를때 */
                    Intent goWriter = new Intent(context.getApplicationContext(), OtherInfo.class);
                    goWriter.putExtra("writeId", timelineList.get(position).getWriteid());
                    goWriter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(goWriter);
                }

            }
        });

        /* 좋아요 버튼 클릭 이벤트 */
        viewHolder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewHolder.favoriteBtn.getTag() == null || viewHolder.favoriteBtn.getTag().equals("favoriteOff")) {
                    //  좋아요 저장
                    viewHolder.favoriteBtn.setImageResource(R.drawable.colorfavorite);
                    viewHolder.favoriteBtn.setTag("favoriteOn");
                    Log.d("인덱스", ""+timelineList.get(position).getIdx());

                    //  타임라인 좋아요 +1
                    int favoriteCount = Integer.parseInt(viewHolder.favoriteC.getText().toString()) + 1;
                    viewHolder.favoriteC.setText(String.valueOf(favoriteCount));
                    //timelineList.get(position).setIdx(Integer.parseInt(viewHolder.favoriteC.getText().toString()) + 1);

                    //  아이디와 해당 인덱스값 저장
                    insertFavorite(timelineList.get(position).getIdx());

                    //  fcm 보내기
                    //  내가 내 게시물 누를땐 fcm 보내지 않기
                    if(getUserid() != timelineList.get(position).getWriteid()) {
                        String messages = " 님이 좋아요를 눌렀습니다";
                        sendFCM(timelineList.get(position).getWriteid(), getUserid(), messages);
                        Log.d("아이디", timelineList.get(position).getWriteid());
                    }
                }else {
                    //  좋아요 삭제
                    viewHolder.favoriteBtn.setImageResource(R.drawable.favorite);
                    viewHolder.favoriteBtn.setTag("favoriteOff");
                    Log.d("좋아요 카운트", "" + timelineList.get(position).getFavoriteCount());

                    //  타임라인 좋아요 -1
                    int favoriteCount = Integer.parseInt(viewHolder.favoriteC.getText().toString()) - 1;
                    Log.d("좋아요 -1", String.valueOf(favoriteCount));
                    viewHolder.favoriteC.setText(String.valueOf(favoriteCount));

                    //  아이디와 해당 인덱스값 삭제
                    deleteFavorite(timelineList.get(position).getIdx());
                }
            }
        });

        /* 댓글 클릭 이벤트 */
        viewHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  1. 댓글 입력하는 화면으로 이동한다
                //  2. 댓글이 달려있을 수 있으므로 게시글에 해당하는 글번호를 가져간다
                Intent goRecomment = new Intent(context.getApplicationContext(), Recomment.class);
                goRecomment.putExtra("index", timelineList.get(position).getIdx());
                goRecomment.putExtra("writeid", timelineList.get(position).getWriteid());
                goRecomment.putExtra("writeImg", timelineList.get(position).getWriteprofile());
                goRecomment.putExtra("content", timelineList.get(position).getWritecontent());
                goRecomment.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Log.d("인덱스", "" + timelineList.get(position).getIdx());
                context.startActivity(goRecomment);
            }
        });

        viewHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //  공유하기 버튼 클릭 이벤트
                /* 공유하기 버튼 클릭 시에 채팅 가능한 친구 목록을 보여준다 */
                Intent selectFren = new Intent(context.getApplicationContext(), SelectFriend.class);
                selectFren.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                selectFren.putExtra("addUserFlag", "false");
                context.startActivity(selectFren);
            }
        });
    }

    @Override
    public int getItemCount() {
        return timelineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView writeProfile;
        public ImageView writeImg;
        public TextView writeid, writedate, writecontent, favoriteC, commentC, contentType;
        public ImageButton favoriteBtn, commentBtn, shareBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            writeProfile = (CircleImageView)itemView.findViewById(R.id.writeProfile);
            writeImg = (ImageView)itemView.findViewById(R.id.writeImg);
            writeid = (TextView)itemView.findViewById(R.id.writeid);
            writedate = (TextView)itemView.findViewById(R.id.writedate);
            writecontent = (TextView)itemView.findViewById(R.id.writecontent);
            favoriteC = (TextView)itemView.findViewById(R.id.favoriteC);
            commentC = (TextView)itemView.findViewById(R.id.commentC);
            contentType = (TextView)itemView.findViewById(R.id.contentType);
            favoriteBtn = (ImageButton)itemView.findViewById(R.id.favoriteBtn);
            commentBtn = (ImageButton)itemView.findViewById(R.id.commentBtn);
            shareBtn = (ImageButton)itemView.findViewById(R.id.shareBtn);
        }
    }

    //  새로 나타나는 뷰에 애니메이션 주기 위해
    public void setAnimation(View viewAnim, int position) {
        if(position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewAnim.startAnimation(animation);
            lastPosition = position;
        }
    }

    //  DB에 좋아요 insert
    public void insertFavorite(int favoriteIdx) {
        String userid = getUserid();
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<ServerResponse> call = apiConfig.insertFavorite(userid, favoriteIdx);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    //  DB에서 좋아요 삭제
    public void deleteFavorite(int favoriteIdx) {
        String userid = getUserid();
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<ServerResponse> call = apiConfig.deleteFavorite(userid, favoriteIdx);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse serverResponse = response.body();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    //  shared에 저장된 아이디 가져오기
    public String getUserid() {
        SharedPreferences autoLogin = context.getSharedPreferences("auto", Context.MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동 로그인일때
            return autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = context.getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            return noAuto.getString("noAutoid", null);
        }
    }

    //  fcm 보내기
    public void sendFCM(String writeid, String loginid, String message) {
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<ServerResponse> call = apiConfig.sendfcm(writeid, loginid, message);
        call.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

    public void updateList(ArrayList<TimelineVO> updateList) {
        this.timelineList = updateList;
        notifyDataSetChanged();
    }
}
