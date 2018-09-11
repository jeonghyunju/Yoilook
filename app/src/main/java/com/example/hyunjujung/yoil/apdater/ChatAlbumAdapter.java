package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.ChatAlbumSlide;
import com.example.hyunjujung.yoil.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 12. 13..
 */

public class ChatAlbumAdapter extends RecyclerView.Adapter<ChatAlbumAdapter.ViewHolder>{
    Context context;
    ArrayList<JSONObject> albumArrays;

    ArrayList<String> fileArray = new ArrayList<>();
    boolean mCheckBoxState = false;

    public ChatAlbumAdapter(Context context, ArrayList<JSONObject> albumArrays, ArrayList<String> fileArray) {
        this.context = context;
        this.albumArrays = albumArrays;
        this.fileArray = fileArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_album_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        try {
            Glide.with(context).load("http://13.124.12.50" + albumArrays.get(position).getString("LatestChat")).into(holder.albumIv);
        }catch (Exception e) {
            e.printStackTrace();
        }

        /* 앨범 체크박스 모두 보이기 */
        if(mCheckBoxState) {
            holder.albumCheck.setVisibility(View.VISIBLE);
        }else {
            holder.albumCheck.setChecked(false);
            holder.grayScaleIv.setVisibility(View.INVISIBLE);
            holder.albumCheck.setVisibility(View.INVISIBLE);
        }

        /* 앨범 체크박스 클릭 리스너 */
        holder.albumCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(!holder.albumCheck.isChecked()) {
                        fileArray.remove(albumArrays.get(position).getString("LatestChat"));
                        holder.grayScaleIv.setVisibility(View.INVISIBLE);
                    }else {
                        Log.e("앨범 선택", albumArrays.get(position).getString("LatestChat"));
                        fileArray.add(albumArrays.get(position).getString("LatestChat"));
                        holder.grayScaleIv.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        /* 앨범 이미지 클릭 시에 ChatAlbumSlide 로 이동 */
        holder.albumIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent goAlbumSlide = new Intent(context.getApplicationContext(), ChatAlbumSlide.class);
                    goAlbumSlide.putExtra("roomId", albumArrays.get(position).getString("roomId"));
                    Log.e("앨범 이동 룸 아이디", albumArrays.get(position).getString("roomId"));
                    goAlbumSlide.putExtra("albumIndex", position);
                    goAlbumSlide.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(goAlbumSlide);
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumArrays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView albumIv, grayScaleIv;
        CheckBox albumCheck;

        public ViewHolder (View view) {
            super(view);
            albumIv = (ImageView)view.findViewById(R.id.albumIv);
            albumCheck = (CheckBox)view.findViewById(R.id.albumCheck);
            grayScaleIv = (ImageView)view.findViewById(R.id.grayScaleIv);
        }
    }

    public void setChatAlbumArrays(ArrayList<JSONObject> arrays) {
        this.albumArrays = arrays;
        notifyDataSetChanged();
    }

    /* 선택 버튼 눌렀을때 체크박스 모두 보여주기 위해서 */
    public void setCheckBoxState(boolean cState) {
        mCheckBoxState = cState;
        notifyDataSetChanged();
    }

    public ArrayList<String> getFileArray() {
        return fileArray;
    }

    public void fileArrayClear(boolean clearState) {
        Log.e("size of fileArray",fileArray.size()+"..");
        if(clearState) {
            fileArray.clear();
        }
    }
}
