package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.FavoriteDetailVO;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteDetail extends AppCompatActivity {
    CircleImageView writePro;
    ImageView writeImg;
    TextView writeId, favoriteTv, commentTv, writecontent;
    ImageButton favoriteImg, commentImg;

    Intent intent;

    //  넘어온 글 인덱스 값 저장 변수
    static int favoriteIdx = 0;

    //  댓글 모두보기 할 때 넘길 변수
    static String writeid = "";
    static String writeimg = "";
    static String writeCon = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_detail);

        writePro = (CircleImageView)findViewById(R.id.writePro);
        writeImg = (ImageView)findViewById(R.id.writeImg);
        writeId = (TextView)findViewById(R.id.writeId);
        writecontent = (TextView)findViewById(R.id.writecontent);
        favoriteTv = (TextView)findViewById(R.id.favoriteTv);
        commentTv = (TextView)findViewById(R.id.commentTv);
        favoriteImg = (ImageButton)findViewById(R.id.favoriteImg);
        commentImg = (ImageButton)findViewById(R.id.commentImg);

        intent = getIntent();
        favoriteIdx = intent.getIntExtra("detailIndex", 0);
    }

    //  하단 메뉴 버튼 클릭 이벤트
    public void clickMenu(View view) {
        int id = view.getId();
        switch (id) {
            //  홈버튼
            case R.id.home:
                intent = new Intent(this, YoilMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;

            //  좋아요 리스트 버튼
            case R.id.favorite:
                intent = new Intent(this, MyFavoriteList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                //overridePendingTransition(R.anim.in_left, R.anim.in_left);
                finish();
                break;

            //  타임라인에 글쓰기 버튼
            case R.id.addtimeline:
                intent = new Intent(this, WriteTimeline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

            //  채팅하기 버튼
            case R.id.chatting:
                break;

            //  내 정보 버튼
            case R.id.myinfo:
                intent = new Intent(this, MyInfo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                break;

        }
    }

    /* 댓글 모두 보기 버튼 클릭 이벤트 */
    public void clickComment(View view) {
        intent = new Intent(getApplicationContext(), Recomment.class);
        intent.putExtra("writeid", writeid);
        intent.putExtra("content", writeCon);
        intent.putExtra("writeImg", writeimg);
        intent.putExtra("index", favoriteIdx);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<FavoriteDetailVO> call = apiConfig.selectFavoriteDetail(favoriteIdx);
        call.enqueue(new Callback<FavoriteDetailVO>() {
            @Override
            public void onResponse(Call<FavoriteDetailVO> call, Response<FavoriteDetailVO> response) {
                FavoriteDetailVO favoriteDetailVO = response.body();
                if(favoriteDetailVO.getWriteimg() == null) {
                    writeImg.setVisibility(View.INVISIBLE);
                }else {
                    Glide.with(getApplicationContext()).load("http://13.124.12.50" + favoriteDetailVO.getWriteimg()).into(writeImg);
                }
                if(favoriteDetailVO.getCommentText().equals("no")) {
                    commentImg.setVisibility(View.INVISIBLE);
                }else {
                    commentTv.setText(favoriteDetailVO.getCommentText());
                }
                Glide.with(getApplicationContext()).load("http://13.124.12.50" + favoriteDetailVO.getWriteprofile()).into(writePro);
                writeId.setText(favoriteDetailVO.getWriteid());
                writecontent.setText(favoriteDetailVO.getWritecontent());
                favoriteTv.setText("좋아요 " + favoriteDetailVO.getFavoriteCount() + " 개");
                writeid = favoriteDetailVO.getWriteid();
                writeimg = favoriteDetailVO.getWriteprofile();
                writeCon = favoriteDetailVO.getWritecontent();
            }

            @Override
            public void onFailure(Call<FavoriteDetailVO> call, Throwable t) {

            }
        });
    }
}
