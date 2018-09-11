package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 11. 2..
 */

public class DailySubimgAdapter extends RecyclerView.Adapter<DailySubimgAdapter.ViewHolder>{
    Context context;
    ArrayList<String> subArray;

    public DailySubimgAdapter() {
    }

    public DailySubimgAdapter(Context context, ArrayList<String> subArray) {
        this.context = context;
        this.subArray = subArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailycodi_subimg_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load("http://13.124.12.50/dailyimg/" + subArray.get(position)).into(holder.subImage);

        /* 서브 이미지 한 건에 대한 좋아요 버튼 클릭 이벤트 */
        holder.categoryFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return subArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView subImage;
        public ImageButton categoryFavorite;

        public ViewHolder(View view) {
            super(view);
            subImage = (ImageView)view.findViewById(R.id.subImage);
            categoryFavorite = (ImageButton)view.findViewById(R.id.categoryFavorite);
        }
    }
}
