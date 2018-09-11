package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hyunjujung.yoil.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.StringTokenizer;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by hyunjujung on 2017. 11. 7..
 */

public class ChatListAdapter extends BaseAdapter{
    /* 채팅 리스트 어댑터
     * 채팅방 참여자
     * 채팅방 이름 (없으면 참여자 이름으로)
     * 메시지 안 읽은 개수 */
    Context context;
    ArrayList<JSONObject> chatListArray;
    LayoutInflater layoutInflater;
    ViewHolder viewHolder;

    public ChatListAdapter(Context context, ArrayList<JSONObject> chatListArray) {
        this.context = context;
        this.chatListArray = chatListArray;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return chatListArray.size();
    }

    @Override
    public Object getItem(int i) {
        return chatListArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.chatlist_item, viewGroup, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)view.getTag();
        }

        /* viewholder 로 정의해놓은 xml 에 데이터 넣어주기 */
        try{
            if(viewHolder.chatTitleImg == null) {
                Log.d("log", chatListArray.get(i).get("roomName").toString());
            }else {
                viewHolder.chatTitleImg.setImageResource(R.drawable.noprofile);
                viewHolder.chatTitleTv.setText(chatListArray.get(i).get("roomName").toString());
                if(chatListArray.get(i).getString("LatestChat").contains("/chatImage/")) {
                    viewHolder.chatCurrentTv.setText("사진");
                }else {
                    viewHolder.chatCurrentTv.setText(chatListArray.get(i).get("LatestChat").toString());
                }
                if(chatListArray.get(i).getInt("newChatCount") > 0) {
                    viewHolder.newChatAlarm.setVisibility(View.VISIBLE);
                    viewHolder.newChatAlarm.setText(String.valueOf(chatListArray.get(i).getInt("newChatCount")));
                }else{
                    viewHolder.newChatAlarm.setVisibility(View.INVISIBLE);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void updateChat(ArrayList<JSONObject> chatArray) {
        this.chatListArray = chatArray;
        notifyDataSetChanged();
    }
}

class ViewHolder {
    CircleImageView chatTitleImg;
    TextView chatTitleTv, chatCurrentTv, newChatAlarm;

    public ViewHolder(View view) {
        chatTitleImg = (CircleImageView) view.findViewById(R.id.chatTitleImg);
        chatTitleTv = (TextView) view.findViewById(R.id.chatTitleTv);
        chatCurrentTv = (TextView) view.findViewById(R.id.chatCurrentTv);
        newChatAlarm = (TextView) view.findViewById(R.id.newChatAlarm);
    }
}
