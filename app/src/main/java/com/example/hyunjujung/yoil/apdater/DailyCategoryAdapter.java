package com.example.hyunjujung.yoil.apdater;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.vo.DailyCategoryVO;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 10. 30..
 */

/* daily codi 항목 중에서 카테고리 어댑터 */
public class DailyCategoryAdapter extends RecyclerView.Adapter<DailyCategoryAdapter.ViewHolder>{
    Context context;
    /* 이미지 uri Array */
    ArrayList<DailyCategoryVO> arrayList;
    /* 이미지 절대경로 Array */
    ArrayList<String> imagePathList;

    public DailyCategoryAdapter() {
    }

    public DailyCategoryAdapter(Context context, ArrayList<DailyCategoryVO> arrayList, ArrayList<String> imagePathList) {
        this.context = context;
        this.arrayList = arrayList;
        this.imagePathList = imagePathList;
    }
    @Override
    public DailyCategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dailycodi_category_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.categoryImg.setImageURI(arrayList.get(position).getCategoryImg());

        /* 추가한 이미지 삭제 버튼 클릭 이벤트 */
        holder.deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.remove(position);
                imagePathList.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView categoryImg;
        public ImageButton deleteCategory;

        public ViewHolder(View view) {
            super(view);
            categoryImg = (ImageView)view.findViewById(R.id.categoryImg);
            deleteCategory = (ImageButton)view.findViewById(R.id.deleteCategory);
        }
    }
}
