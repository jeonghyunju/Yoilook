package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.SelectVO;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 11. 8..
 */

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>{
    /* 채팅방 어댑터 */
    Context context;
    ArrayList<JSONObject> chatConList;
    String message;

    SelectVO selectVO = new SelectVO();
    String youProfile = "";

    public ChatRoomAdapter(Context context, ArrayList<JSONObject> chatConList) {
        this.context = context;
        this.chatConList = chatConList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_room_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        /* 보내는 유저의 아이디와 로그인한 유저가 같다면 채팅 내용을 오른쪽에 표시한다 */
        try{
            if(chatConList.get(position).getString("LatestChat").contains("초대")
                    || chatConList.get(position).getString("LatestChat").contains("나갔습니다.")) {
                /* 채팅방에 초대하거나 나갔을때 */
                holder.chatyouProfile.setVisibility(View.GONE);
                holder.chatyouId.setVisibility(View.GONE);
                holder.chatyouTv.setVisibility(View.GONE);
                holder.chatyoudate.setVisibility(View.GONE);
                holder.chatmeTv.setVisibility(View.GONE);
                holder.chatmedate.setVisibility(View.GONE);
                holder.chatmeImage.setVisibility(View.GONE);
                holder.meImageDate.setVisibility(View.GONE);
                holder.chatyouImage.setVisibility(View.GONE);
                holder.youImageDate.setVisibility(View.GONE);
                holder.addUserTv.setVisibility(View.VISIBLE);
                holder.addUserTv.setText(chatConList.get(position).getString("LatestChat"));
            } else {
                if(chatConList.get(position).getString("sendUser").equals(getLoginid())) {
                    holder.chatyouProfile.setVisibility(View.GONE);
                    holder.chatyouId.setVisibility(View.GONE);
                    holder.chatyouTv.setVisibility(View.GONE);
                    holder.chatyoudate.setVisibility(View.GONE);
                    holder.addUserTv.setVisibility(View.GONE);
                    holder.chatyouImage.setVisibility(View.GONE);
                    holder.youImageDate.setVisibility(View.GONE);
                    if(chatConList.get(position).getString("LatestChat").contains("/chatImage/")) {
                        /* 이미지 전송했을때 */
                        holder.chatmeTv.setVisibility(View.GONE);
                        holder.chatmedate.setVisibility(View.GONE);
                        holder.chatmeImage.setVisibility(View.VISIBLE);
                        holder.meImageDate.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load("http://13.124.12.50" + chatConList.get(position).getString("LatestChat"))
                                .into(holder.chatmeImage);
                        holder.meImageDate.setText(chatConList.get(position).getString("messageDate").substring(11, 16));
                    }else {
                        holder.chatmeImage.setVisibility(View.GONE);
                        holder.meImageDate.setVisibility(View.GONE);
                        holder.chatmeTv.setVisibility(View.VISIBLE);
                        holder.chatmedate.setVisibility(View.VISIBLE);
                        holder.chatmeTv.setText(chatConList.get(position).getString("LatestChat"));
                        holder.chatmedate.setText(chatConList.get(position).getString("messageDate").substring(11, 16));
                    }
                }else {
                    holder.chatmeTv.setVisibility(View.GONE);
                    holder.chatmedate.setVisibility(View.GONE);
                    holder.addUserTv.setVisibility(View.GONE);
                    holder.chatmeImage.setVisibility(View.GONE);
                    holder.meImageDate.setVisibility(View.GONE);
                    holder.chatyouTv.setVisibility(View.VISIBLE);
                    holder.chatyoudate.setVisibility(View.VISIBLE);
                    /* 내가 보낸 채팅일때 */
                    if(position > 0 && chatConList.get(position-1).getString("sendUser").equals(chatConList.get(position).getString("sendUser"))) {
                        holder.chatyouProfile.setVisibility(View.INVISIBLE);
                        holder.chatyouId.setVisibility(View.GONE);
                    }else {
                        holder.chatyouProfile.setVisibility(View.VISIBLE);
                        holder.chatyouId.setVisibility(View.VISIBLE);
                        Glide.with(context).load("http://13.124.12.50" + chatConList.get(position).getString("sendProfile")).into(holder.chatyouProfile);
                        holder.chatyouId.setText(chatConList.get(position).getString("sendUser"));
                    }
                    /* 채팅에서 전달된 이미지 출력 */
                    if(chatConList.get(position).getString("LatestChat").contains("/chatImage/")) {
                        holder.chatyouTv.setVisibility(View.GONE);
                        holder.chatyoudate.setVisibility(View.GONE);
                        holder.chatyouImage.setVisibility(View.VISIBLE);
                        holder.youImageDate.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load("http://13.124.12.50" + chatConList.get(position).getString("LatestChat"))
                                .into(holder.chatyouImage);
                        holder.youImageDate.setText(chatConList.get(position).getString("messageDate").substring(11, 16));
                    }else {
                        holder.chatyouImage.setVisibility(View.GONE);
                        holder.youImageDate.setVisibility(View.GONE);
                        holder.chatyouTv.setVisibility(View.VISIBLE);
                        holder.chatyoudate.setVisibility(View.VISIBLE);
                        holder.chatyouTv.setText(chatConList.get(position).getString("LatestChat"));
                        holder.chatyoudate.setText(chatConList.get(position).getString("messageDate").substring(11, 16));
                    }
                }
            }
        }catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return chatConList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView chatyouProfile;
        TextView chatyouId, chatyouTv, chatyoudate, chatmeTv, chatmedate, addUserTv, youImageDate, meImageDate;
        ImageView chatyouImage, chatmeImage;

        public ViewHolder(View view) {
            super(view);
            chatyouProfile = (CircleImageView)view.findViewById(R.id.chatyouProfile);
            chatyouId = (TextView)view.findViewById(R.id.chatyouId);
            chatyouTv = (TextView)view.findViewById(R.id.chatyouTv);
            chatyoudate = (TextView)view.findViewById(R.id.chatyoudate);
            chatmeTv = (TextView)view.findViewById(R.id.chatmeTv);
            chatmedate = (TextView)view.findViewById(R.id.chatmedate);
            addUserTv = (TextView)view.findViewById(R.id.addUserTv);
            chatyouImage = (ImageView)view.findViewById(R.id.chatyouImage);
            chatmeImage = (ImageView)view.findViewById(R.id.chatmeImage);
            meImageDate = (TextView)view.findViewById(R.id.meImageDate);
            youImageDate = (TextView)view.findViewById(R.id.youImageDate);

        }
    }

    /* 로그인 아이디 가져오기 */
    public String getLoginid() {
        SharedPreferences autoLogin = context.getSharedPreferences("auto", Context.MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동로그인 일때
            return autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = context.getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            return noAuto.getString("noAutoid", null);
        }
    }

    public void updateChatting(ArrayList<JSONObject> chatlist) {
        this.chatConList = chatlist;
        notifyDataSetChanged();
    }

    public String setYouProfile(String sendUser) {
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<SelectVO> pCalls = apiConfig.selectmembers(sendUser);
        pCalls.enqueue(new Callback<SelectVO>() {
            @Override
            public void onResponse(Call<SelectVO> call, Response<SelectVO> response) {
                selectVO = response.body();
                youProfile = selectVO.getProfile();
            }

            @Override
            public void onFailure(Call<SelectVO> call, Throwable t) {

            }
        });
        return  youProfile;
    }

}
