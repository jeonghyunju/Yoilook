package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.FavoriteDetail;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 10. 20..
 */

public class MyfavoriteAdapter extends RecyclerView.Adapter<MyfavoriteAdapter.ViewHolder>{
    Context context;
    ArrayList<TimelineVO> favoriteList;

    public MyfavoriteAdapter(Context context, ArrayList<TimelineVO> favoriteList) {
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Glide.with(context).load("http://13.124.12.50" + favoriteList.get(position).getWriteImg()).into(holder.likeImg);

        /* 좋아요 게시물 클릭 이벤트 (게시글 상세로 이동) */
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goDetail = new Intent(context.getApplicationContext(), FavoriteDetail.class);
                goDetail.putExtra("detailIndex", favoriteList.get(position).getIdx());
                goDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(goDetail);
                Log.d("인덱스", "" + favoriteList.get(position).getIdx());
            }
        });
    }

    @Override
    public MyfavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_favorite_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView likeImg;

        public ViewHolder(View view) {
            super(view);
            likeImg = (ImageView)view.findViewById(R.id.likeImg);
        }
    }
}
