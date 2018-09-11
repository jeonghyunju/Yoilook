package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.hyunjujung.yoil.R;
import com.google.gson.JsonArray;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 10. 31..
 */

public class DailyTagAdapter extends RecyclerView.Adapter<DailyTagAdapter.ViewHolder>{
    Context context;
    ArrayList<String> tagArray;
    boolean isTagcheck;

    public DailyTagAdapter(Context context, ArrayList<String> tagArray, boolean isTagcheck) {
        this.context = context;
        this.tagArray = tagArray;
        this.isTagcheck = isTagcheck;
    }

    @Override
    public DailyTagAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailycodi_tag_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tagTv.setText(tagArray.get(position));

        if(isTagcheck) {
           /* 데일리 코디 추가하기 화면에서 넘어온 boolean 변수 일때 (추가하는 화면에서는 삭제가 가능해야 한다)
            * 태그 클릭하면 태그 지우기 */
            holder.tagTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tagArray.remove(position);
                    notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tagArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tagTv;

        public ViewHolder(View view) {
            super(view);
            tagTv = (TextView)view.findViewById(R.id.tagTv);
        }
    }
}
