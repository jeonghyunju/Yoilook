package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

public class DailyUpdateAdapter extends RecyclerView.Adapter<DailyUpdateAdapter.ViewHolder>{
    Context context;
    ArrayList<String> fileName;
    ArrayList<String> subimageArray;

    public DailyUpdateAdapter() {
    }

    public DailyUpdateAdapter(Context context, ArrayList<String> fileName, ArrayList<String> subimageArray) {
        this.context = context;
        this.fileName = fileName;
        this.subimageArray = subimageArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailycodi_category_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(fileName.get(position).startsWith("*")) {
            Glide.with(context).load("http://13.124.12.50/dailyimg/" + fileName.get(position).substring(1)).into(holder.categoryImg);
        }else if(fileName.get(position).startsWith("#")){
            holder.categoryImg.setImageURI(Uri.parse(fileName.get(position).substring(1)));
            Log.d("포지션", "" + position);
        }

        /* 이미지 삭제 버튼 클릭 이벤트 */
        holder.deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileName.remove(position);
                subimageArray.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileName.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryImg;
        public ImageButton deleteCategory;

        public ViewHolder(View view) {
            super(view);
            categoryImg = (ImageView)view.findViewById(R.id.categoryImg);
            deleteCategory = (ImageButton)view.findViewById(R.id.deleteCategory);
        }
    }
}
