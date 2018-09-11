package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.Recomment;
import com.example.hyunjujung.yoil.ServerResponse;
import com.example.hyunjujung.yoil.UpdateTimeLine;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.FavoriteVO;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 10. 25..
 */

public class InfoListAdapter extends RecyclerView.Adapter<InfoListAdapter.ViewHolder>{
    Context context;
    ArrayList<TimelineVO> timelineList;
    ArrayList<FavoriteVO> favoriteList;

    Intent intent;

    public InfoListAdapter(Context context, ArrayList<TimelineVO> timelineList, ArrayList<FavoriteVO> favoriteList) {
        this.context = context;
        this.timelineList = timelineList;
        this.favoriteList = favoriteList;
    }

    @Override
    public InfoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_list_item, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context).load("http://13.124.12.50" + timelineList.get(position).getWriteprofile()).into(holder.writeProfile);
        holder.writeids.setText(timelineList.get(position).getWriteid());
        holder.writecontent.setText(timelineList.get(position).getWritecontent());
        holder.writedate.setText(timelineList.get(position).getWritedate());
        if(timelineList.get(position).getWriteImg() == null) {
            holder.writeImg.setVisibility(View.INVISIBLE);
        }else {
            Glide.with(context).load("http://13.124.12.50" + timelineList.get(position).getWriteImg()).into(holder.writeImg);
        }
        holder.favoriteC.setText(String.valueOf(timelineList.get(position).getFavoriteCount()));
        holder.commentC.setText(String.valueOf(timelineList.get(position).getCommentCount()));
        holder.favoriteBtn.setImageResource(R.drawable.favorite);

        /* 내가 누른 좋아요 표시하기 */
        for(int i=0 ; i<favoriteList.size() ; i++) {
            if(favoriteList.get(i).getFavoriteIdx() == timelineList.get(position).getIdx()) {
                Log.d("좋아요", "" + timelineList.get(position).getIdx());
                holder.favoriteBtn.setImageResource(R.drawable.colorfavorite);
                holder.favoriteBtn.setTag("favoriteOn");
            }
        }

        /* 좋아요 버튼 클릭 이벤트 */
        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.favoriteBtn.getTag() == null || holder.favoriteBtn.getTag().equals("favoriteOff")) {
                    //  좋아요 이미지 바꾸기
                    holder.favoriteBtn.setImageResource(R.drawable.colorfavorite);
                    holder.favoriteBtn.setTag("favoriteOn");

                    //  좋아요 카운트 +1
                    int favoriteCount = Integer.parseInt(holder.favoriteC.getText().toString()) + 1;
                    holder.favoriteC.setText(String.valueOf(favoriteCount));

                    //  아이디와 해당 인덱스값 저장
                    insertFavorite(timelineList.get(position).getIdx());
                }else {
                    //  좋아요 이미지 바꾸기
                    holder.favoriteBtn.setImageResource(R.drawable.favorite);
                    holder.favoriteBtn.setTag("favoriteOff");

                    //  좋아요 카운트 -1
                    int favoriteCount = Integer.parseInt(holder.favoriteC.getText().toString()) - 1;
                    holder.favoriteC.setText(String.valueOf(favoriteCount));

                    //  아이디와 해당 인덱스값 삭제
                    deleteFavorite(timelineList.get(position).getIdx());
                }
            }
        });


        /* 댓글 버튼 클릭 이벤트 */
        holder.commentBtn.setOnClickListener(new View.OnClickListener() {
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
                context.startActivity(goRecomment);
            }
        });

        /* 공유하기 버튼 클릭 이벤트 */
        holder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        /* 내가 쓴 게시물 아닐때 게시물 수정, 삭제 버튼 안보이기 */
        if(!timelineList.get(position).getWriteid().equals(getUserid())) {
            holder.updateNdelete.setVisibility(View.GONE);
        }else {
            holder.updateNdelete.setVisibility(View.VISIBLE);
        }

        /* 게시글 수정, 삭제 버튼 클릭 이벤트
         * 옵션 메뉴 붙이기 */
        holder.updateNdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                /* 팝업 메뉴 */
                PopupMenu popUp = new PopupMenu(view.getContext(), view);
                popUp.inflate(R.menu.update_timeline);

                /* 팝업 메뉴 클릭 이벤트 */
                popUp.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            /* 글 수정 */
                            case R.id.update:
                                intent = new Intent(view.getContext(), UpdateTimeLine.class);
                                intent.putExtra("updateIndex", timelineList.get(position).getIdx());
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                                break;

                            /* 글 삭제 */
                            case R.id.delete:
                                //  다이얼로그 띄우고 삭제
                                Log.d("리스트 포지션", "" + position);
                                DialogInterface.OnClickListener deleteOk =  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Log.d("리스트 포지션", "" + position);
                                        Log.d("포지션 인덱스", "" + timelineList.get(position).getIdx());
                                        deleteTimeline(timelineList.get(position).getIdx());
                                        timelineList.remove(position);
                                        notifyItemRemoved(position);
                                        Toast.makeText(context.getApplicationContext(), "글이 삭제되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                };
                                //  취소
                                DialogInterface.OnClickListener cancelDelete = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                };
                                //  다이얼로그 띄우기
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("알림")
                                        .setMessage("해당 글을 삭제하시겠습니까?")
                                        .setPositiveButton("예", deleteOk)
                                        .setNegativeButton("아니오", cancelDelete)
                                        .show();
                                break;
                        }
                        return false;
                    }
                });
                popUp.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return timelineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView writeProfile;
        public ImageView writeImg;
        public TextView writeids, writedate, writecontent, favoriteC, commentC;
        public ImageButton favoriteBtn, commentBtn, shareBtn, updateNdelete;

        public ViewHolder(View view) {
            super(view);
            writeProfile = (CircleImageView)view.findViewById(R.id.writeProfile);
            writeImg = (ImageView)view.findViewById(R.id.writeImg);
            writeids = (TextView)view.findViewById(R.id.writeids);
            writedate = (TextView)view.findViewById(R.id.writedate);
            writecontent = (TextView)view.findViewById(R.id.writecontent);
            favoriteC = (TextView)view.findViewById(R.id.favoriteC);
            commentC = (TextView)view.findViewById(R.id.commentC);
            favoriteBtn = (ImageButton)view.findViewById(R.id.favoriteBtn);
            commentBtn = (ImageButton)view.findViewById(R.id.commentBtn);
            shareBtn = (ImageButton)view.findViewById(R.id.shareBtn);
            updateNdelete = (ImageButton)view.findViewById(R.id.updateNdelete);
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

    //  타임라인 게시글 한 건 삭제
    public void deleteTimeline(int deleteIdx) {
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<ServerResponse> deleteCall = apiConfig.deleteOnetimeline(deleteIdx);
        deleteCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                Log.d("삭제", response.body().getMessage());
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d("삭제 실패", t.getMessage());
            }
        });
    }

    public void updatelist(ArrayList<TimelineVO> updateList) {
        this.timelineList = updateList;
        notifyDataSetChanged();
    }
}
