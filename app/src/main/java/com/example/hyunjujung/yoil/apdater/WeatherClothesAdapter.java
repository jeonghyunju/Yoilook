package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.hyunjujung.yoil.R;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 12. 5..
 */

public class WeatherClothesAdapter extends RecyclerView.Adapter<WeatherClothesAdapter.ViewHolder>{
    Context context;
    ArrayList<Integer> weatherArray;

    public WeatherClothesAdapter() {
    }

    public WeatherClothesAdapter(Context context, ArrayList<Integer> weatherArray) {
        this.context = context;
        this.weatherArray = weatherArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.weather_clothes_item, parent, false);
        ViewHolder viewholder = new ViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.weather_item_ImageView.setImageResource(weatherArray.get(position));
    }

    @Override
    public int getItemCount() {
        return weatherArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView weather_item_ImageView;
        public ViewHolder (View view) {
            super(view);
            weather_item_ImageView = (ImageView)view.findViewById(R.id.weather_item_ImageView);
        }
    }

    public void setclothesArray(ArrayList<Integer> arrays) {
        this.weatherArray = arrays;
        notifyDataSetChanged();
    }
}
