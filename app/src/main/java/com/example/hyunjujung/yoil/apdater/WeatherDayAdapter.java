package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.vo.WeatherDayVO;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 12. 6..
 */

public class WeatherDayAdapter extends RecyclerView.Adapter<WeatherDayAdapter.ViewHolder>{
    Context context;
    ArrayList<WeatherDayVO> weatherDayArray;

    public WeatherDayAdapter() {
    }

    public WeatherDayAdapter(Context context, ArrayList<WeatherDayVO> weatherDayArray) {
        this.context = context;
        this.weatherDayArray = weatherDayArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_day_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.weather_icon.setImageResource(weatherDayArray.get(position).getWeatherIcons());
        holder.now_temp_Tv.setText(weatherDayArray.get(position).getTemps() + " °C");
        if(weatherDayArray.get(position).getTimes() >= 24) {
            int times = weatherDayArray.get(position).getTimes() - 24;
            if(times == 0) {
                holder.now_time_Tv.setText("오전 12시");
            }else {
                if(times == 12) {
                    holder.now_time_Tv.setText("오후 12시");
                }else if(times > 12){
                    holder.now_time_Tv.setText("오후 " + (times-12) + "시");
                }else {
                    holder.now_time_Tv.setText("오전 " + times + "시");
                }
            }
        }else {
            int times = weatherDayArray.get(position).getTimes() - 12;
            if(times == 0) {
                holder.now_time_Tv.setText("오후 12시");
            }else {
                holder.now_time_Tv.setText("오후 " + times + "시");
            }
        }
    }

    @Override
    public int getItemCount() {
        return weatherDayArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView weather_icon;
        TextView now_time_Tv, now_temp_Tv;

        public ViewHolder (View view) {
            super(view);
            weather_icon = (ImageView)view.findViewById(R.id.weather_icon);
            now_temp_Tv = (TextView)view.findViewById(R.id.now_temp_Tv);
            now_time_Tv = (TextView)view.findViewById(R.id.now_time_Tv);
        }
    }

    public void setWeatherDays(ArrayList<WeatherDayVO> arrays) {
        this.weatherDayArray = arrays;
        notifyDataSetChanged();
    }
}
