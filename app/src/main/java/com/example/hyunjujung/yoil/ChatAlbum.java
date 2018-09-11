package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.hyunjujung.yoil.apdater.ChatAlbumAdapter;
import com.example.hyunjujung.yoil.asynctask.SaveImageTask;
import com.example.hyunjujung.yoil.sqlite.ChatContentDB;

import org.json.JSONObject;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

public class ChatAlbum extends AppCompatActivity {

    /* DataBase */
    ChatContentDB chatContentDB;

    LinearLayout bottomLinear;
    Button choiceAlbumBtn;
    ImageView saveDoneImageView;

    /* 채팅방 앨범 RecyclerView */
    RecyclerView chatAlbumRecycle;
    RecyclerView.LayoutManager chatAlbumLayout;
    ChatAlbumAdapter chatAlbumAdapter;
    ArrayList<JSONObject> chatAlbumArray = new ArrayList<>();
    ArrayList<String> fileNameArray = new ArrayList<>();

    Intent intent;

    /* 채팅방에서 Navigation Drawer 의 앨범 선택했을때 넘어오는 채팅방 아이디 저장 변수 */
    String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_album);

        bottomLinear = (LinearLayout)findViewById(R.id.bottomLinear);
        choiceAlbumBtn = (Button)findViewById(R.id.choiceAlbumBtn);
        saveDoneImageView = (ImageView)findViewById(R.id.saveDoneImageView);
        chatAlbumRecycle = (RecyclerView)findViewById(R.id.chatAlbumRecycle);

        chatContentDB = new ChatContentDB(this, "1", null, 1);

        chatAlbumLayout = new GridLayoutManager(this, 3);
        chatAlbumRecycle.setLayoutManager(chatAlbumLayout);

        chatAlbumAdapter = new ChatAlbumAdapter(this, chatAlbumArray, fileNameArray);
        chatAlbumRecycle.setAdapter(chatAlbumAdapter);

        intent = getIntent();
        if(intent.getStringExtra("roomId") != null) {
            roomId = intent.getStringExtra("roomId");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        chatAlbumArray = chatContentDB.selectAlbums(roomId);
        chatAlbumAdapter.setChatAlbumArrays(chatAlbumArray);
    }

    public void exitAlbumNchoickAlbum(View view) {
        int id = view.getId();
        switch (id) {
            /* 채팅방 앨범 닫기 */
            case R.id.exitAlbumBtn:
                finish();
                break;

            /* 채팅방 앨범 체크박스 모두 보이기 */
            case R.id.choiceAlbumBtn:
                if(choiceAlbumBtn.getText().toString().equals("선택")) {
                    bottomLinear.setVisibility(View.VISIBLE);
                    chatAlbumAdapter.setCheckBoxState(true);
                    choiceAlbumBtn.setText("취소");
                }else {
                    bottomLinear.setVisibility(View.INVISIBLE);
                    chatAlbumAdapter.setCheckBoxState(false);
                    chatAlbumAdapter.fileArrayClear(true);
                    choiceAlbumBtn.setText("선택");
                }
                break;
        }
    }

    public void saveAlbumBtn(View view) {
        /* 선택된 이미지 저장
         * 1. 선택된 이미지가 없다면 알림 띄운다 */

        fileNameArray = chatAlbumAdapter.getFileArray();

        if(fileNameArray.size() == 0) {
            Toast.makeText(getApplicationContext(), "선택된 이미지가 없습니다.", Toast.LENGTH_SHORT).show();
        }else {
            SaveImageTask saveImageTask = new SaveImageTask(this, fileNameArray, chatAlbumAdapter, true);
            saveImageTask.execute();

            chatAlbumAdapter.setCheckBoxState(false);
            choiceAlbumBtn.setText("선택");

            /* 사진 저장 후 애니메이션 */
            saveDoneImageView.setVisibility(View.VISIBLE);
            bottomLinear.setVisibility(View.INVISIBLE);
            Animation saveDoneAnim = AnimationUtils.loadAnimation(this, R.anim.favorite_anim);
            saveDoneImageView.setAnimation(saveDoneAnim);
            Toast.makeText(this, "사진이 모두 저장되었습니다.", Toast.LENGTH_SHORT).show();
            saveDoneImageView.setVisibility(View.INVISIBLE);

            //chatAlbumAdapter.fileArrayClear(true);
        }
    }

}
