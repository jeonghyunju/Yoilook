package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.hyunjujung.yoil.chatting.ChattingRoom;
import com.example.hyunjujung.yoil.apdater.SelectFriendAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.sqlite.ChatRoomDB;
import com.example.hyunjujung.yoil.vo.SelectVO;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectFriend extends AppCompatActivity {
    /* 공유하기 버튼 누른후 이동되는 친구목록 액티비티 */

    Button doneBtn;

    RecyclerView friendRecycle;
    RecyclerView.LayoutManager friendLayout;
    SelectFriendAdapter friendAdapter;
    ArrayList<SelectVO> friendList = new ArrayList<>();

    /* 대화상대 추가시 대화상대 아이디 저장 */
    ArrayList<String> selectFriendId = new ArrayList<>();

    /* 대화상대 추가시 대화상대 이름 저장 */
    ArrayList<String> selectFriendName = new ArrayList<>();

    /* 대화상대 추가시 대화상대 프로필 이미지 저장 */
    ArrayList<String> selectFriendProfile = new ArrayList<>();

    Intent intent;

    /* 로그인된 사용자 이름 저장 */
    String myName;
    String userProfile;
    String roomId;

    /* DB */
    ChatRoomDB chatRoomDB;

    JSONObject jsonObject = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_friend);

        doneBtn = (Button)findViewById(R.id.doneBtn);

        friendRecycle = (RecyclerView)findViewById(R.id.friendRecycle);

        /* DB 생성 */
        chatRoomDB = new ChatRoomDB(getApplicationContext(), "0", null, 1);

        friendLayout = new LinearLayoutManager(this);
        friendRecycle.setLayoutManager(friendLayout);

        intent = getIntent();
        roomId = intent.getStringExtra("roomId");

        /* DB에서 친구 목록 받아와서 뿌려주기 */
        if(intent.getStringExtra("roomId") != null) {
            /* 기존 채팅방에 대화상대를 추가할떄 */
            jsonObject = chatRoomDB.selectRoomId(intent.getStringExtra("roomId"));
            Log.e("대화상대 추가 json", jsonObject.toString());
            ApiConfig apiConfig = RetrofitCreator.getapiConfig();
            Call<List<SelectVO>> callF = apiConfig.selectFriend(getLoginid());
            callF.enqueue(new Callback<List<SelectVO>>() {
                @Override
                public void onResponse(Call<List<SelectVO>> call, Response<List<SelectVO>> response) {
                    friendList.addAll(response.body());
                    try{
                        friendAdapter = new SelectFriendAdapter(getApplicationContext(), friendList, selectFriendId, selectFriendName, selectFriendProfile, jsonObject.getString("chatUsers"));
                        friendRecycle.setAdapter(friendAdapter);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<List<SelectVO>> call, Throwable t) {

                }
            });
        }else {
            /* 새로운 대화방 생성할때 */
            ApiConfig apiConfig = RetrofitCreator.getapiConfig();
            Call<List<SelectVO>> callF = apiConfig.selectFriend(getLoginid());
            callF.enqueue(new Callback<List<SelectVO>>() {
                @Override
                public void onResponse(Call<List<SelectVO>> call, Response<List<SelectVO>> response) {
                    friendList.addAll(response.body());

                    friendAdapter = new SelectFriendAdapter(getApplicationContext(), friendList, selectFriendId, selectFriendName, selectFriendProfile, "");
                    friendRecycle.setAdapter(friendAdapter);
                }

                @Override
                public void onFailure(Call<List<SelectVO>> call, Throwable t) {

                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        /* 로그인된 사용자 이름 저장 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<SelectVO> call = apiConfig.selectmembers(getLoginid());
        call.enqueue(new Callback<SelectVO>() {
            @Override
            public void onResponse(Call<SelectVO> call, Response<SelectVO> response) {
                SelectVO selectVO = response.body();
                myName = selectVO.getUsername();
                userProfile = selectVO.getProfile();
            }

            @Override
            public void onFailure(Call<SelectVO> call, Throwable t) {

            }
        });
    }

    public void selectFriendClick(View view) {
        int id = view.getId();
        switch (id) {
            /* 친구 추가 완료 버튼 */
            case R.id.doneBtn:
                /* 친구 추가 완료 후 채팅방으로 이동한다 */
                String friendString = "";
                String friendName = "";
                String friendProfile = "";
                selectFriendId = friendAdapter.giveSelectLists();
                selectFriendName = friendAdapter.giveSelectName();
                selectFriendProfile = friendAdapter.giveSelectProfile();
                if(selectFriendId.size() == 0) {
                    Toast.makeText(this, "대화 상대를 추가하세요", Toast.LENGTH_SHORT).show();
                }else {
                    Log.d("대화상대 어레이", selectFriendId.toString());
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
                    String todayDate = sdf.format(calendar.getTime());

                    for(int i=0 ; i<selectFriendId.size() ; i++) {
                        if(i == (selectFriendId.size() - 1)) {
                            friendString += selectFriendId.get(i);
                            friendName += selectFriendName.get(i);
                            friendProfile += selectFriendProfile.get(i);
                        }else {
                            friendString += selectFriendId.get(i) + "-";
                            friendName += selectFriendName.get(i) + ",";
                            friendProfile += selectFriendProfile.get(i) + "-";
                        }
                    }

                    switch (intent.getStringExtra("addUserFlag")) {
                        /* chattingRoom 액티비티에서 유저 추가하기 위해 넘어왔을때
                         * 채팅방 update */
                        case "true":
                            intent = new Intent(this, ChattingRoom.class);

                            /* chatRoomDB update, chatContentDB insert */
                            try{
                                intent.putExtra("getUserName", jsonObject.getString("getUserName") + "," + friendName);
                                intent.putExtra("chatUsers", jsonObject.getString("chatUsers") + "-" + friendString);
                                intent.putExtra("chatPCount", jsonObject.getInt("chatPCount") + selectFriendId.size());
                                intent.putExtra("userProfile", jsonObject.getString("userProfile") + "-" + friendProfile);
                                intent.putExtra("addUserFlag", "true");
                                intent.putExtra("addUser", friendName);
                                Log.e("selectFriend roomId", roomId);
                                chatRoomDB.updateUsers(roomId, jsonObject.getString("getUserName") + "," + friendName,
                                        jsonObject.getString("getUserName") + "," + friendName,
                                        jsonObject.getString("chatUsers") + "-" + friendString,
                                        jsonObject.getInt("chatPCount") + selectFriendId.size(),
                                        jsonObject.getString("userProfile") + "-" + friendProfile);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }

                            setResult(RESULT_OK, intent);
                            finish();
                            break;

                        /* TimelineAdapter 액티비티에서 새로운 채팅방 개설하기 위해 넘어왔을때 */
                        case "false":
                            intent = new Intent(this, ChattingRoom.class);
                            intent.putExtra("newRoomFlag", "true");
                            intent.putExtra("addUserFlag", "false");
                            intent.putExtra("roomId", todayDate + "_" + getLoginid());
                            intent.putExtra("sendProfile", userProfile);

                            /* DB insert */
                            chatRoomDB.insertChatRoom(todayDate + "_" + getLoginid(), myName + "," + friendName, myName + "," + friendName,
                                    getLoginid() + "-" +friendString, selectFriendId.size() + 1, "", "", 0, userProfile + "-" + friendProfile);

                            startActivity(intent);
                            finish();
                            break;
                    }
                }
                break;
        }
    }

    /* Shared 에서 아이디 가져오기 */
    /* 로그인된 사용자 아이디 가져오기 */
    public String getLoginid() {
        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동로그인 일때
            return autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = getSharedPreferences("noAuto", MODE_PRIVATE);
            return noAuto.getString("noAutoid", null);
        }
    }
}
