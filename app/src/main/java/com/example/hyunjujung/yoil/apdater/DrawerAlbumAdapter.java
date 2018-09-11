package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.ChatAlbumSlide;
import com.example.hyunjujung.yoil.R;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 11. 24..
 */

public class DrawerAlbumAdapter extends RecyclerView.Adapter<DrawerAlbumAdapter.ViewHolder>{
    Context context;
    ArrayList<String> albumArray;
    String roomId;

    public DrawerAlbumAdapter() {
    }

    public DrawerAlbumAdapter(Context context, ArrayList<String> albumArray) {
        this.context = context;
        this.albumArray = albumArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_album_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load("http://13.124.12.50" + albumArray.get(position)).into(holder.albumImageView);

        /* 앨범 이미지 클릭 리스너
         * 클릭하면 viewPager 로 클릭된 이미지의 인덱스 가지고 넘어가기 */
        holder.albumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent albumSlideintent = new Intent(context.getApplicationContext(), ChatAlbumSlide.class);
                albumSlideintent.putExtra("albumIndex", position);
                albumSlideintent.putExtra("roomId", roomId);
                context.startActivity(albumSlideintent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView albumImageView;

        public ViewHolder(View view) {
            super(view);
            albumImageView = (ImageView)view.findViewById(R.id.albumImageView);
        }
    }

    public void setalbumAdapter(ArrayList<String> albums) {
        this.albumArray = albums;
        notifyDataSetChanged();
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}
