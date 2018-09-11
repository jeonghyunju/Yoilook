package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunjujung.yoil.apdater.ChatAlbumSildeAdapter;
import com.example.hyunjujung.yoil.asynctask.SaveImageTask;
import com.example.hyunjujung.yoil.sqlite.ChatContentDB;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class ChatAlbumSlide extends AppCompatActivity {
    TextView albumTitle, albumCountText;

    /* 이미지 ViewPager */
    ViewPager albumViewpager;
    ChatAlbumSildeAdapter slideAdapter;
    ArrayList<String> albumImageArray = new ArrayList<>();
    ArrayList<JSONObject> albumArray = new ArrayList<>();

    Intent intent;

    /* DataBase */
    ChatContentDB chatContentDB;

    /* 넘겨받은 채팅방 아이디 */
    String roomId;
    int albumPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_album_slide);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        chatContentDB = new ChatContentDB(this, "1", null, 1);

        albumTitle = (TextView)findViewById(R.id.albumTitle);
        albumCountText = (TextView)findViewById(R.id.albumCountText);
        albumViewpager = (ViewPager)findViewById(R.id.albumViewpager);

        BitmapFactory.Options bmOptions;
        bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        intent = getIntent();
        if(intent.getStringExtra("roomId") != null) {
            //Log.e("roomId", roomId);
            roomId = intent.getStringExtra("roomId");
            albumPosition = intent.getIntExtra("albumIndex", 0);
        }

        /* 앨범 하나씩 슬라이드 될때 상단에 보낸사람과 날짜, 앨범 카운트 보여주기 */
        albumViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                albumCountText.setText("( " + (albumViewpager.getCurrentItem()+1) + " / " + albumImageArray.size() + " )");
                try{
                    albumTitle.setText(albumArray.get(albumViewpager.getCurrentItem()).getString("sendUser") + " ( "
                     + albumArray.get(albumViewpager.getCurrentItem()).getString("messageDate").substring(0, 10) + " )");
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* DataBase 에서 채팅방에 해당하는 이미지경로와 모든 데이터를 가져온다 */
        albumImageArray = chatContentDB.selectAlbumImage(roomId);
        albumArray = chatContentDB.selectAlbums(roomId);

        /* viewPager Adapter 에 데이터를 연결한다 */
        slideAdapter = new ChatAlbumSildeAdapter(this, albumImageArray);
        albumViewpager.setAdapter(slideAdapter);

        /* 클릭된 이미지부터 보여준다 */
        albumViewpager.setCurrentItem(albumPosition);

        /* 앨범에 있는 이미지 개수와 현재 이미지가 몇 번째 이미지인지 알려준다 */
        albumCountText.setText("( " + (albumPosition+1) + " / " + albumImageArray.size() + " )");

        /* 클릭된 이미지의 보낸 사람과 날짜를 세팅한다 */
        try {
            albumTitle.setText(albumArray.get(albumPosition).getString("sendUser") + " ( "
                    + albumArray.get(albumPosition).getString("messageDate").substring(0, 10) + " )");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 이미지 저장 버튼 */
    public void saveAlbumBtn(View view) {
        Log.e("현재 이미지 인덱스", "" + albumViewpager.getCurrentItem());
        ArrayList<String> urls = new ArrayList<>();
        String url = albumImageArray.get(albumViewpager.getCurrentItem());
        urls.add(url);
        SaveImageTask saveImageTask = new SaveImageTask(this, urls);
        saveImageTask.execute(url);
        Toast.makeText(this, "사진이 저장되었습니다", Toast.LENGTH_SHORT).show();
    }

    public void returnBtn(View view) {
        int id = view.getId();
        switch (id) {
            /* 뒤로 가기 버튼 */
            case R.id.backBtn:
                finish();
                break;

            /* 앨범으로 가기 버튼 */
            case R.id.goAlbumtext:
                Intent goAlbum = new Intent(this, ChatAlbum.class);
                goAlbum.putExtra("roomId", roomId);
                goAlbum.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(goAlbum);
                finish();
                break;
        }
    }
}
