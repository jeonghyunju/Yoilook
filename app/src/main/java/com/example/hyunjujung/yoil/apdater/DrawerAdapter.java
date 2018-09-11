package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;

import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hyunjujung on 2017. 11. 21..
 */

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.ViewHolder>{
    Context context;
    ArrayList<String> userProfileArray;
    ArrayList<String> userIdArray;

    public DrawerAdapter() {
    }

    public DrawerAdapter(Context context, ArrayList<String> userProfileArray, ArrayList<String> userIdArray) {
        this.context = context;
        this.userProfileArray = userProfileArray;
        this.userIdArray = userIdArray;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context).load("http://13.124.12.50" + userProfileArray.get(position)).into(holder.chatUserProfile);
        holder.chatUserId.setText(userIdArray.get(position));
    }

    @Override
    public int getItemCount() {
        return userIdArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView chatUserProfile;
        TextView chatUserId;
        public ViewHolder (View view) {
            super(view);
            chatUserProfile = (CircleImageView)view.findViewById(R.id.chatUserProfile);
            chatUserId = (TextView)view.findViewById(R.id.chatUserId);
        }
    }
}
