package com.example.hyunjujung.yoil.chatting;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.SimpleDateFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.apdater.ChatListAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.sqlite.ChatContentDB;
import com.example.hyunjujung.yoil.sqlite.ChatRoomDB;
import com.example.hyunjujung.yoil.vo.SelectVO;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chatting extends AppCompatActivity {

    /* 채팅 목록 리스트뷰 */
    SwipeMenuListView chatListview;
    ArrayList<JSONObject> myRoomlist = new ArrayList<>();
    ChatListAdapter chatListAdapter;

    LinearLayout chatLinear;

    /* DB */
    ChatRoomDB chatRoomDB;
    ChatContentDB chatContentDB;

    /* 서비스 */
    ChatService chatService;

    String socketMessage;
    Handler messageHandler;

    Intent intent;

    ArrayList<JSONObject> updateRoom = new ArrayList<>();

    String roomIds = "No Enter Room";

    /* 로그인된 사용자 이름 저장 */
    String myName;
    String userProfile;

    String nameString = "";
    String idString = "";
    String profileString = "";

    ArrayList<String> nameArray = new ArrayList<>();
    ArrayList<String> idArray = new ArrayList<>();
    ArrayList<String> profileArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting);
        messageHandler = new Handler();
        chatLinear = (LinearLayout)findViewById(R.id.chatLinear);

        /* DB 생성 */
        chatRoomDB = new ChatRoomDB(getApplicationContext(), "0", null, 1);
        chatContentDB = new ChatContentDB(getApplicationContext(), "1", null, 1);

        chatListview = (SwipeMenuListView) findViewById(R.id.chatListview);

        chatListAdapter = new ChatListAdapter(getApplicationContext(), myRoomlist);
        chatListview.setAdapter(chatListAdapter);

        /* 채팅 목록 swipe 해서 삭제하기 */
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem aboutItem = new SwipeMenuItem(getApplicationContext());
                aboutItem.setBackground(new ColorDrawable(Color.rgb(181, 0, 0)));
                aboutItem.setWidth(dp2px(80));
                aboutItem.setIcon(R.drawable.deletechat);
                menu.addMenuItem(aboutItem);
            }
        };

        chatListview.setMenuCreator(creator);
        chatListview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
                final JSONObject exitJson = new JSONObject();
                try{
                    String[] userName = myRoomlist.get(position).getString("getUserName").split(",");
                    String[] chatUser = myRoomlist.get(position).getString("chatUsers").split("-");
                    String[] userProfile = myRoomlist.get(position).getString("userProfile").split("-");
                    for(int j=0 ; j<userName.length ; j++) {
                        if(!chatUser[j].equals(getLoginid())) {
                            nameArray.add(userName[j]);
                            idArray.add(chatUser[j]);
                            profileArray.add(userProfile[j]);
                        }
                    }
                    Log.e("아이디 어레이", idArray.toString());
                }catch (Exception e) {
                    e.printStackTrace();
                }
                switch (index) {
                    /* 채팅 목록에서 삭제하기
                     * 다이얼로그 띄우기
                     * 소켓으로 보내는 메시지에 flag 줘서 채팅방에서 나가도록 한다
                     * DB 에서도 채팅 목록을 지운다 */
                    case 0:
                        Log.e("채팅방 인덱스", myRoomlist.get(position).toString());
                        DialogInterface.OnClickListener exitRoomOk = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                /* 채팅방 나가기
                                 * 나가는 flag, 나가는 사람 */
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String exitDate = simpleDateFormat.format(calendar.getTime());

                                try {
                                    if(idArray.size() > 0) {
                                        /* 채팅 참여 유저가 두명 이상일 경우 */
                                        for(int j=0 ; j<idArray.size() ; j++) {
                                            if(j == (idArray.size() - 1)) {
                                                nameString += nameArray.get(j);
                                                idString += idArray.get(j);
                                                profileString += profileArray.get(j);
                                            }else {
                                                nameString += nameArray.get(j) + ",";
                                                idString += idArray.get(j) + "-";
                                                profileString += profileArray.get(j) + "-";
                                            }
                                        }

                                        exitJson.put("roomId", myRoomlist.get(position).getString("roomId"));
                                        exitJson.put("chatFlag", true);
                                        exitJson.put("sendUser", getLoginid());
                                        exitJson.put("sendProfile", "exitRoom");
                                        exitJson.put("getUserName", nameString);
                                        exitJson.put("getUser", idString);
                                        exitJson.put("chatPCount", myRoomlist.get(position).getInt("chatPCount") - 1);
                                        exitJson.put("userProfile", profileString);
                                        exitJson.put("newRoomFlag", false);
                                        exitJson.put("LatestChat", myName + " 님이 채팅방을 나갔습니다.");
                                        exitJson.put("messageDate", exitDate);

                                        /* DB 에서 삭제 */
                                        chatRoomDB.deleteChatRoom(myRoomlist.get(position).getString("roomId"));
                                        chatContentDB.deleteChatCon(myRoomlist.get(position).getString("roomId"));

                                        /* list 에서 삭제 */
                                        myRoomlist.remove(position);
                                        chatListAdapter.notifyDataSetChanged();

                                        chatService.sendMessageSocket(exitJson);
                                    }else {
                                        /* 채팅방 유저가 자신만 있을 경우 메시지 보내지 않고 DB 에서 삭제만 시킨다 */
                                        /* DB 에서 삭제 */
                                        chatRoomDB.deleteChatRoom(myRoomlist.get(position).getString("roomId"));
                                        chatContentDB.deleteChatCon(myRoomlist.get(position).getString("roomId"));

                                        /* list 에서 삭제 */
                                        myRoomlist.remove(position);
                                        chatListAdapter.notifyDataSetChanged();
                                    }
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        DialogInterface.OnClickListener noExit = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        };
                        new AlertDialog.Builder(chatLinear.getContext())
                                .setTitle("알림")
                                .setMessage("대화내용이 모두 삭제됩니다.\n채팅방을 나가시겠습니까?")
                                .setNegativeButton("아니오", noExit)
                                .setPositiveButton("예", exitRoomOk)
                                .show();
                        break;
                }
                return false;
            }
        });

        /* swipe 메뉴 애니메이션 주기 */
        //chatListview.setCloseInterpolator(new BounceInterpolator());

        /* 채팅방 목록 클릭시에 채팅방으로 들어가기 */
        chatListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(getApplicationContext(), ChattingRoom.class);
                try{
                    intent.putExtra("roomId", myRoomlist.get(i).get("roomId").toString());
                    intent.putExtra("roomName", myRoomlist.get(i).getString("roomName"));
                    intent.putExtra("newRoomFlag", "false");
                    intent.putExtra("sendProfile", userProfile);
                    startActivity(intent);
                    /* 채팅 카운트 0 */
                    chatRoomDB.updateLatestChat(myRoomlist.get(i).getString("roomId"),
                            myRoomlist.get(i).getString("LatestChat"), myRoomlist.get(i).getString("LatestTime"), 0);
                }catch (Exception e) {

                }
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /* 채팅방 목록 보이기 */
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
        for(JSONObject jsons : chatRoomDB.selectChatRoom()) {
            myRoomlist.add(jsons);
        }
        chatListAdapter.updateChat(myRoomlist);
    }

    /* 소켓에서 받은 메시지 */
    private Runnable showMessage = new Runnable() {
        @Override
        public void run() {

            if(updateRoom.size() > 0) {
                updateRoom.clear();
            }
            for(JSONObject jsons : chatRoomDB.selectChatRoom()) {
                updateRoom.add(jsons);
            }
            myRoomlist = updateRoom;
            Message message = handler.obtainMessage();
            handler.sendMessage(message);
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chatListAdapter.updateChat(myRoomlist);
        }
    };

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

    /* 서비스 bind */
    @Override
    protected void onStart() {
        super.onStart();
        Intent bindIntent = new Intent(this, ChatService.class);
        bindService(bindIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        if(myRoomlist.size() > 0) {
            myRoomlist.clear();
        }
    }

    /* binding service callback 메소드 */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ChatService.LocalBinder binder = (ChatService.LocalBinder) iBinder;
            chatService = binder.getService();
            chatService.registerCallback(chatCallBack);
            chatService.registerNoti(notiCallBack);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }

    };

    /* 서비스 콜백메소드
     * 소켓에서 받은 메시지 출력 */
    private ChatService.ChatCallBack chatCallBack = new ChatService.ChatCallBack() {
        @Override
        public void recievedMessage() {
            socketMessage = chatService.getRecieveMessage();
            messageHandler.post(showMessage);
        }
    };

    /* 서비스 콜백메소드
     * notification 띄우기 위한 콜백 */
     private ChatService.NotiCallBack notiCallBack = new ChatService.NotiCallBack() {
        @Override
        public boolean setNotification() {
            socketMessage = chatService.getRecieveMessage();
            if(!socketMessage.contains(roomIds)) {
                return true;
            }else {
                return false;
            }
        }
    };
}
