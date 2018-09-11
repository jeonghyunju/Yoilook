package com.example.hyunjujung.yoil.apdater;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 12. 14..
 */

public class ChatAlbumSildeAdapter extends PagerAdapter {
    Context context;
    ArrayList<String> albumImageArray;

    public ChatAlbumSildeAdapter(Context context, ArrayList<String> albumImageArray) {
        this.context = context;
        this.albumImageArray = albumImageArray;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View view = inflater.inflate(R.layout.album_slide_item, container, false);
        ImageView albumImg = (ImageView)view.findViewById(R.id.albumImg);

        Glide.with(context).load("http://13.124.12.50" + albumImageArray.get(position)).into(albumImg);
        ((ViewPager)container).addView(view);
        return view;
    }

    @Override
    public int getCount() {
        return albumImageArray.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager)container).removeView((View)object);
    }
}
