package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.WeekCodiDetail;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 11. 3..
 */

public class WeekCodiYoilAdapter extends RecyclerView.Adapter<WeekCodiYoilAdapter.ViewHolder>{
    Context context;
    ArrayList<Integer> backArray;
    ArrayList<String> yoilArray;
    ArrayList<String> dayArray;

    public WeekCodiYoilAdapter() {
    }

    public WeekCodiYoilAdapter(Context context, ArrayList<Integer> backArray, ArrayList<String> yoilArray, ArrayList<String> dayArray) {
        this.context = context;
        this.backArray = backArray;
        this.yoilArray = yoilArray;
        this.dayArray = dayArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weekcodi_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.backRel.setBackgroundResource(backArray.get(position));
        holder.weekTv.setText(yoilArray.get(position));

        /* recyclerview 아이템 클릭 리스너 */
        holder.backRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* dayArray 에 요일에 해당하는 데이터 있으므로 intent로 넘긴다 */
                Intent weekcodi = new Intent(context.getApplicationContext(), WeekCodiDetail.class);
                weekcodi.putExtra("weekYmd", dayArray.get(position));
                weekcodi.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(weekcodi);
            }
        });
    }

    @Override
    public int getItemCount() {
        return backArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout backRel;
        public TextView weekTv;

        public ViewHolder(View view) {
            super(view);
            backRel = (RelativeLayout) view.findViewById(R.id.backRel);
            weekTv = (TextView) view.findViewById(R.id.weekTv);
        }
    }
}
