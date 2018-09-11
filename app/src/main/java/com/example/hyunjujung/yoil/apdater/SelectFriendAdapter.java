package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.vo.SelectVO;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hyunjujung on 2017. 11. 8..
 */

public class SelectFriendAdapter extends RecyclerView.Adapter<SelectFriendAdapter.ViewHolder>{
    /* 친구목록 어댑터 */
    Context context;
    ArrayList<SelectVO> friendList;

    /* 대화 상대 추가  */
    ArrayList<String> selectList;
    ArrayList<String> selectName;
    ArrayList<String> selectProfile;

    /* 기존 대화방에 존재하는 유저 아이디 */
    String existUserid;

    public SelectFriendAdapter() {
    }

    public SelectFriendAdapter(Context context, ArrayList<SelectVO> friendList, ArrayList<String> selectList, ArrayList<String> selectName, ArrayList<String> selectProfile, String existUserid) {
        this.context = context;
        this.friendList = friendList;
        this.selectList = selectList;
        this.selectName = selectName;
        this.selectProfile = selectProfile;
        this.existUserid = existUserid;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_friend_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Glide.with(context).load("http://13.124.12.50" + friendList.get(position).getProfile()).into(holder.friendProfile);
        holder.friendId.setText(friendList.get(position).getUsername());

        if(existUserid.contains(friendList.get(position).getUserid())) {
            /* 기존 채팅방 유저이면 checkbox enabled 하기 */
            holder.selectFriend.setChecked(true);
            holder.selectFriend.setEnabled(false);
        }else {
            holder.selectFriend.setChecked(false);
            holder.selectFriend.setEnabled(true);
        }

        holder.selectFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!holder.selectFriend.isChecked()) {
                    selectList.remove(friendList.get(position).getUserid());
                    selectName.remove(friendList.get(position).getUsername());
                    selectProfile.remove(friendList.get(position).getProfile());
                }else {
                    selectList.add(friendList.get(position).getUserid());
                    selectName.add(friendList.get(position).getUsername());
                    selectProfile.add(friendList.get(position).getProfile());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView friendProfile;
        TextView friendId;
        CheckBox selectFriend;

        public ViewHolder (View view) {
            super(view);
            friendProfile = (CircleImageView)view.findViewById(R.id.friendProfile);
            friendId = (TextView)view.findViewById(R.id.friendId);
            selectFriend = (CheckBox)view.findViewById(R.id.selectFriend);
        }
    }

    public ArrayList<String> giveSelectLists() {
        return selectList;
    }
    public ArrayList<String> giveSelectName() {
        return selectName;
    }
    public ArrayList<String> giveSelectProfile() { return selectProfile; }
}
