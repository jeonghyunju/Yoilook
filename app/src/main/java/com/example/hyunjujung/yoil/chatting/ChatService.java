package com.example.hyunjujung.yoil.chatting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.hyunjujung.yoil.MainActivity;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.YoilMain;
import com.example.hyunjujung.yoil.sqlite.ChatContentDB;
import com.example.hyunjujung.yoil.sqlite.ChatRoomDB;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by hyunjujung on 2017. 11. 9..
 */

public class ChatService extends Service {
    /* 서비스에서 발생하는 자원을 액티비티에서도 사용하기 위해서 binder를 이용한다 */
    private final IBinder mBinder = new LocalBinder();

    /* 소켓 연결 변수 */
    private static final String iP = "13.124.12.50";
    private static final int port = 9090;
    Socket socket;
    private BufferedReader socketIn;
    private BufferedWriter writer;
    private PrintWriter socketOut;

    /* 소켓 연결 후 받은 메시지 변수 */
    public String recieveMessage = "";

    /* 보내는 메시지 변수 */
    public String sendMessage = "";

    /* DB */
    ChatRoomDB chatRoomDB;
    ChatContentDB chatContentDB;

    String allRoomid;

    JSONObject jsonObject;
    JSONObject chatObject;

    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        /* 서비스 호출 */
        Log.d("서비스 호출", "서비스 실행 시작");

        /* 서비스 실행시 소켓 연결하고 메시지 받을 준비 */
        ConnectSocket connectSocket = new ConnectSocket();
        chatContentDB = new ChatContentDB(getApplicationContext(), "1", null, 1);
        connectSocket.start();

        /* DB 테이블 생성 */
        chatRoomDB = new ChatRoomDB(getApplicationContext(), "0", null, 1);
        chatContentDB = new ChatContentDB(getApplicationContext(), "1", null, 1);
    }

    public class LocalBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }

    public interface ChatCallBack {
        void recievedMessage();
    }

    public interface NotiCallBack {
        boolean setNotification();
    }

    /* 콜백메소드
     * 소켓에서 받은 메시지 콜백 */
    private ChatCallBack chatCallBack;
    public void registerCallback(ChatCallBack back) {
        chatCallBack = back;
    }

    /* 콜백메소드
     * notification 콜백 */
    private NotiCallBack notiCallBack;
    public void registerNoti(NotiCallBack notiCall) {
        notiCallBack = notiCall;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /* 소켓 연결하고 메시지 받는 부분 */
    public class ConnectSocket extends Thread {
        @Override
        public void run() {
            try{
                /* 소켓 연결 */
                socket = new Socket(iP, port);
                writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                socketIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                socketOut = new PrintWriter(writer, true);
                Log.e("채팅 서비스 메시지 받기", "메시지 받기");

                while (true) {
                    /* 소켓 메시지 받기 */
                    recieveMessage = socketIn.readLine();
                    if(recieveMessage.length() == 0) {
                        continue;
                    }
                    if(chatCallBack != null) {
                        Log.e("받은 메시지~", recieveMessage);
                        setRecieveMessage(recieveMessage);

                        boolean isNoti = notiCallBack.setNotification();
                        jsonObject = new JSONObject(recieveMessage);
                        /* DB 에 채팅 리스트 없을때 채팅방을 새로 만들어 저장하고, 기존에 있는 채팅 리스트일땐 채팅 내용만 update 한다 */
                        switch (jsonObject.getString("newRoomFlag")) {
                            /* 새로운 채팅방 생성 */
                            case "true":
                                allRoomid = chatRoomDB.selectChatRoomId();
                                if(allRoomid.contains(jsonObject.getString("roomId"))) {
                                    chatRoomDB.updateUsers(jsonObject.getString("roomId"),
                                            jsonObject.getString("getUserName"),
                                            jsonObject.getString("getUserName"),
                                            jsonObject.getString("getUser"),
                                            jsonObject.getInt("chatPCount"),
                                            jsonObject.getString("userProfile"));
                                    chatContentDB.insertChatCon(jsonObject.getString("roomId"),
                                            jsonObject.getString("getUser"),
                                            jsonObject.getString("messageDate"),
                                            jsonObject.getString("LatestChat"),
                                            jsonObject.getString("sendUser"),
                                            jsonObject.getString("sendProfile"));
                                    /* Notification */
                                    sendNotification(jsonObject.getString("roomId"),
                                            jsonObject.getString("sendUser"),
                                            jsonObject.getString("newRoomFlag"),
                                            jsonObject.getString("getUserName"));
                                }else {
                                    chatRoomDB.insertChatRoom(jsonObject.getString("roomId"),
                                            jsonObject.getString("getUserName"),
                                            jsonObject.getString("getUserName"),
                                            jsonObject.getString("getUser"),
                                            jsonObject.getInt("chatPCount"),
                                            jsonObject.getString("LatestChat"),
                                            jsonObject.getString("messageDate"), 1,
                                            jsonObject.getString("userProfile"));
                                    chatContentDB.insertChatCon(jsonObject.get("roomId").toString(),
                                            jsonObject.get("getUser").toString(),
                                            jsonObject.get("messageDate").toString(),
                                            jsonObject.get("LatestChat").toString(),
                                            jsonObject.get("sendUser").toString(),
                                            jsonObject.getString("sendProfile"));
                                    /* Notification */
                                    sendNotification(jsonObject.getString("roomId"),
                                            jsonObject.getString("sendUser"),
                                            jsonObject.getString("newRoomFlag"),
                                            jsonObject.getString("getUserName"));
                                }
                                break;

                            /* 기존에 존재하는 채팅방 */
                            case "false":
                                if(jsonObject.getString("sendProfile").equals("exitRoom")) {
                                    /* 다른 유저가 채팅방 나갔을때
                                     * 기존 채팅방 참여 유저수, 참여 유저이름, 아이디 업데이트 */
                                    chatRoomDB.updateUsers(jsonObject.getString("roomId"),
                                            jsonObject.getString("getUserName"),
                                            jsonObject.getString("getUserName"),
                                            jsonObject.getString("getUser"),
                                            jsonObject.getInt("chatPCount"),
                                            jsonObject.getString("userProfile"));
                                    chatContentDB.insertChatCon(jsonObject.getString("roomId"),
                                            jsonObject.getString("getUser"),
                                            jsonObject.getString("messageDate"),
                                            jsonObject.getString("LatestChat"),
                                            jsonObject.getString("sendUser"),
                                            jsonObject.getString("sendProfile"));
                                }else {
                                    if(isNoti) {
                                        /* DB chatCount +1 */
                                        chatRoomDB.updateChatCount(jsonObject.get("roomId").toString(),
                                                jsonObject.get("LatestChat").toString(),
                                                jsonObject.getString("messageDate"));
                                        chatContentDB.insertChatCon(jsonObject.get("roomId").toString(),
                                                jsonObject.get("getUser").toString(),
                                                jsonObject.get("messageDate").toString(),
                                                jsonObject.get("LatestChat").toString(),
                                                jsonObject.get("sendUser").toString(),
                                                jsonObject.getString("sendProfile"));
                                        /* Notification */
                                        sendNotification(jsonObject.getString("roomId"),
                                                jsonObject.getString("sendUser"),
                                                jsonObject.getString("newRoomFlag"),
                                                jsonObject.getString("getUserName"));
                                    }else {
                                        /* 채팅방 액티비티에 사용자가 들어와있을때 채팅 내용 update & insert */
                                        chatRoomDB.updateLatestChat(jsonObject.getString("roomId"),
                                                jsonObject.get("LatestChat").toString(),
                                                jsonObject.getString("messageDate"), 0);
                                        chatContentDB.insertChatCon(jsonObject.getString("roomId"),
                                                jsonObject.getString("getUser"),
                                                jsonObject.get("messageDate").toString(),
                                                jsonObject.get("LatestChat").toString(),
                                                jsonObject.get("sendUser").toString(),
                                                jsonObject.getString("sendProfile"));
                                    }
                                }
                                break;
                        }
                        chatCallBack.recievedMessage();
                    }else {
                        Log.e("ChatCallBack", "null");
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* Notification */
    public void sendNotification(String roomId, String sendUser, String newRoomFlag, String getUserName) {
        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        SharedPreferences noAuto = getSharedPreferences("noAuto", MODE_PRIVATE);
        /* notification 구분하기 위한 ID */
        //int notiID = Integer.parseInt(roomId.substring(0, 11));
        //Log.e("방 아이디", "" + notiID);
        if(autoLogin.getString("autoId", null) != null || noAuto.getString("noAutoid", null) != null) {
            //  자동 로그인일때
            intent = new Intent(this, ChattingRoom.class);
            intent.putExtra("roomId", roomId);
            intent.putExtra("newRoomFlag", newRoomFlag);
            intent.putExtra("roomName", getUserName);
        }else if(autoLogin.getString("autoId", null) == null || noAuto.getString("noAutoid", null) == null){
            intent = new Intent(this, MainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notifications)
                .setContentTitle("알림")
                .setSound(defaultUri)
                .setContentText(sendUser + " 님이 Message 를 보냈습니다")
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notificationBuilder.build());
    }

    /* 메시지 보내기 */
    public void sendMessageSocket(final JSONObject jsonMessage) {
        Log.d("제이슨 메시지", jsonMessage.toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socketOut.println(jsonMessage.toString());
                    socketOut.flush();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /* getter / setter */
    public String getRecieveMessage() {
        return recieveMessage;
    }

    public void setRecieveMessage(String recieveMessage) {
        this.recieveMessage = recieveMessage;
    }

    public String getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(String sendMessage) {
        this.sendMessage = sendMessage;
    }


}
