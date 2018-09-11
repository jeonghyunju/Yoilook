package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.Recomment;
import com.example.hyunjujung.yoil.vo.CommentVO;
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.http.POST;

/**
 * Created by hyunjujung on 2017. 10. 22..
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{
    Context context;
    ArrayList<CommentVO> commentList;
    public static final int ITEM_TYPE_RECYCLER_WIDTH = 1000;
    public static final int ITEM_TYPE_ACTION_WIDTH = 1001;
    private ItemTouchHelperExtension itemTouchHelperExtension;

    static String loginImg = "";

    public CommentAdapter(Context context, ArrayList<CommentVO> commentList) {
        this.context = context;
        this.commentList = commentList;
    }

    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recomment_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(commentList.get(position).getcLevel() == 0) {
            //  댓글 일때
            Glide.with(context.getApplicationContext()).load("http://13.124.12.50" + commentList.get(position).getCommentProfile()).into(holder.writeCPimg);
            holder.commentIds.setText(commentList.get(position).getCommentId());
            holder.commentCon.setText(commentList.get(position).getCommentCon());
        }else if(commentList.get(position).getcLevel() == 1){
            //  대댓글일때
            //  layout에 margin 주기
            FrameLayout.LayoutParams rp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rp.setMargins(100, 15, 0, 0);
            holder.recommentLinear.setLayoutParams(rp);
            Glide.with(context.getApplicationContext()).load("http://13.124.12.50" + commentList.get(position).getCommentProfile()).into(holder.writeCPimg);
            holder.recommentBtn.setVisibility(View.GONE);
            holder.commentIds.setText(commentList.get(position).getCommentId());
            holder.commentCon.setText(commentList.get(position).getCommentCon());
        }

        /* 대댓글 달기 버튼 클릭 이벤트 */
        holder.recommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Recomment)context).setRecommentEt(commentList.get(position).getCommentId());
                Log.d("댓글 인덱스", "" + commentList.get(position).getIdx());
                ((Recomment)context).getRecomIdx(commentList.get(position).getIdx());
            }
        });
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView writeCPimg;
        TextView commentIds, commentCon;
        Button recommentBtn;
        FrameLayout recommentLinear;

        public ViewHolder(View view) {
            super(view);
            writeCPimg = (CircleImageView)view.findViewById(R.id.writeCPimg);
            commentIds = (TextView)view.findViewById(R.id.commentIds);
            commentCon = (TextView)view.findViewById(R.id.commentCon);
            recommentBtn = (Button)view.findViewById(R.id.recommentBtn);
            recommentLinear = (FrameLayout)view.findViewById(R.id.recommentLinear);
        }
    }

    /* 로그인 아이디 가져오기 */
    public String getUserid() {
        SharedPreferences autoLogin = context.getSharedPreferences("auto", Context.MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동로그인 일때
            loginImg = autoLogin.getString("autoImg", null);
            return autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = context.getSharedPreferences("noAuto", Context.MODE_PRIVATE);
            loginImg = noAuto.getString("noAutoImg", null);
            return noAuto.getString("noAutoid", null);
        }
    }


    public void updateComment(ArrayList<CommentVO> commentList) {
        this.commentList = commentList;
        notifyDataSetChanged();
    }
}
