package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyunjujung on 2017. 10. 17..
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder>{
    Context context;
    ArrayList<TimelineVO> arrayList;

    public InfoAdapter(Context context, ArrayList<TimelineVO> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public InfoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        if(arrayList.get(position).getWriteImg() != null) {
            Glide.with(context).load("http://13.124.12.50" + arrayList.get(position).getWriteImg()).into(viewHolder.userImg);
        }else {
            viewHolder.userImg.setVisibility(View.INVISIBLE);
            viewHolder.contentTv.setText(arrayList.get(position).getWritecontent());
        }
        viewHolder.dateTv.setText(arrayList.get(position).getWritedate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImg;
        public TextView contentTv, dateTv;

        public ViewHolder(View itemView) {
            super(itemView);
            userImg = (ImageView)itemView.findViewById(R.id.userImg);
            contentTv = (TextView)itemView.findViewById(R.id.contentTv);
            dateTv = (TextView)itemView.findViewById(R.id.dateTv);
        }
    }
}
