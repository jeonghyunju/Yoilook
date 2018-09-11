package com.example.hyunjujung.yoil.chatting;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunjujung.yoil.ChangeRoomTitle;
import com.example.hyunjujung.yoil.ChatAlbum;
import com.example.hyunjujung.yoil.R;
import com.example.hyunjujung.yoil.SelectFriend;
import com.example.hyunjujung.yoil.ServerResponse;
import com.example.hyunjujung.yoil.apdater.DrawerAdapter;
import com.example.hyunjujung.yoil.apdater.DrawerAlbumAdapter;
import com.example.hyunjujung.yoil.apdater.ChatRoomAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.sqlite.ChatContentDB;
import com.example.hyunjujung.yoil.sqlite.ChatRoomDB;
import com.example.hyunjujung.yoil.vo.SelectVO;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hyunjujung on 2017. 11. 8..
 */

public class ChattingRoom extends AppCompatActivity {
    static final int CHANGE_TITLE = 1001;
    static final int ADD_USER = 1002;
    static final int PICK_CAMERA = 1003;
    static final int PICK_ALBUM = 1004;
    static final int CROP_IMAGE = 1005;

    RecyclerView conRecycle;
    RecyclerView.LayoutManager conLayout;
    ChatRoomAdapter chatRoomAdapter;
    ArrayList<JSONObject> messageList = new ArrayList<>();

    EditText writeChatEt;
    TextView roomTitle, drawerRoomTitle, albumTv;
    ImageButton sendImage;
    Toolbar chatToolbar;
    CircleImageView drawerChatProfile;

    /* Navigation Drawer */
    DrawerLayout chat_drawer;
    NavigationView chat_Navi;
    RecyclerView drawerRecycle;
    RecyclerView.LayoutManager drawerLayout;
    DrawerAdapter drawerAdapter;
    ArrayList<String> userProfileArray = new ArrayList<>();
    ArrayList<String> userNameArray = new ArrayList<>();
    ArrayList<String> userIdArray = new ArrayList<>();
    RecyclerView albumRecycleView;
    RecyclerView.LayoutManager albumLayout;
    DrawerAlbumAdapter drawerAlbumAdapter;
    ArrayList<String> albumArray = new ArrayList<>();

    /* 인텐트로 넘겨받은 대화상대 리스트 */
    String selectUserid;
    String selectUserName;

    /* 로그인 아이디, 이름, 프로필 */
    String loginId, loginName, loginP;
    String exitId = "";
    String exitName = "";
    String exitProfile = "";

    /* 채팅방 참여 인원 */
    int chatPCount;

    Intent intent;

    /* 룸아이디 지정하기 위한 오늘날짜 */
    String todays;
    String loginProfile;
    String[] chatUserProfile;
    String[] chatUsername;
    String[] chatUserId;

    /* 소켓에서 넘어온 메시지 저장 */
    Handler messageHandler;
    static String socketMessage;

    /* 바인드된 서비스
     * - 바인드된 서비스에는 소켓 연결과 연결 후 메시지를 받는 메서드와 메시지를 전송하는 메서드가 존재한다 */
    ChatService chatService;

    JSONObject jsonObject = new JSONObject();
    JSONObject chatJsonObject = new JSONObject();

    /* DB */
    ChatRoomDB chatRoomDB;
    ChatContentDB chatContentDB;

    //  카메라로 찍어서 크롭한 uri와 앨범에서 가져와서 크롭한 uri
    Uri cameraUri, albumUri;

    //  이미지의 현재 경로
    static String photoPath;

    //  카메라로 찍어서 크롭한 경로와 앨범에서 가져와서 크롭한 경로를 다르게 지정해주기 위해 선언해주는 변수
    Boolean album = false;

    static String imageFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_room);
        messageHandler = new Handler();

        conRecycle = (RecyclerView)findViewById(R.id.conRecycle);
        writeChatEt = (EditText)findViewById(R.id.writeChatEt);
        roomTitle = (TextView)findViewById(R.id.roomTitle);
        albumTv = (TextView)findViewById(R.id.albumTv);
        drawerChatProfile = (CircleImageView)findViewById(R.id.drawerChatProfile);
        sendImage = (ImageButton)findViewById(R.id.sendImage);
        chatToolbar = (Toolbar)findViewById(R.id.chatToolbar);
        chat_drawer = (DrawerLayout)findViewById(R.id.chat_drawer);
        chat_Navi = (NavigationView)findViewById(R.id.chat_Navi);
        drawerRoomTitle = (TextView)findViewById(R.id.drawerRoomTitle);
        drawerRecycle = (RecyclerView)findViewById(R.id.drawerRecycle);
        albumRecycleView = (RecyclerView)findViewById(R.id.albumRecycleView);

        drawerChatProfile.setImageResource(R.drawable.noprofile);

        /* 채팅 내용 RecyclerView */
        conLayout = new LinearLayoutManager(this);
        conRecycle.setLayoutManager(conLayout);

        /* DrawerLayout 채팅 참여자 RecyclerView */
        drawerLayout = new LinearLayoutManager(this);
        drawerRecycle.setLayoutManager(drawerLayout);

        /* DrawerLayout 앨범 RecyclerView */
        albumLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        albumRecycleView.setLayoutManager(albumLayout);

        chatRoomAdapter = new ChatRoomAdapter(this, messageList);
        conRecycle.setAdapter(chatRoomAdapter);

        drawerAdapter = new DrawerAdapter(this, userProfileArray, userNameArray);
        drawerRecycle.setAdapter(drawerAdapter);

        drawerAlbumAdapter = new DrawerAlbumAdapter(this, albumArray);
        albumRecycleView.setAdapter(drawerAlbumAdapter);

        setSupportActionBar(chatToolbar);

        /* DB 생성 */
        chatRoomDB = new ChatRoomDB(getApplicationContext(), "0", null, 1);
        chatContentDB = new ChatContentDB(getApplicationContext(), "1", null, 1);

        intent = getIntent();
        todays = intent.getStringExtra("roomId");

        /* 채팅방에 해당하는 채팅내용 가져오기 */
        chatJsonObject = chatRoomDB.selectRoomId(todays);

        try{
            roomTitle.setText(chatJsonObject.getString("roomName"));
            drawerRoomTitle.setText(chatJsonObject.getString("roomName"));
            jsonObject.put("roomId", todays);
            jsonObject.put("chatFlag", true);
            jsonObject.put("sendUser", getLoginid());
            jsonObject.put("sendProfile", intent.getStringExtra("sendProfile"));
            jsonObject.put("getUserName", chatJsonObject.getString("getUserName"));
            Log.e("대화상대 추가???", chatJsonObject.getString("getUserName"));
            jsonObject.put("getUser", chatJsonObject.getString("chatUsers"));
            jsonObject.put("chatPCount", chatJsonObject.getInt("chatPCount"));
            jsonObject.put("userProfile", chatJsonObject.getString("userProfile"));
            if(intent.getStringExtra("newRoomFlag").equals("true")) {
                /* SelectFriend 액티비티에서 바로 넘어왔을때 */
                jsonObject.put("newRoomFlag", true);
            }else {
                /* Chatting 액티비티에서 넘어왔을때 */
                jsonObject.put("newRoomFlag", false);
            }

            if(userNameArray.size() > 0) {
                userProfileArray.clear();
                userNameArray.clear();
                userIdArray.clear();
            }
            try {
                chatUserProfile = chatJsonObject.getString("userProfile").split("-");
                chatUsername = chatJsonObject.getString("getUserName").split(",");
                chatUserId = chatJsonObject.getString("chatUsers").split("-");
                for(int i=0 ; i<chatUsername.length ; i++) {
                    userProfileArray.add(chatUserProfile[i]);
                    userNameArray.add(chatUsername[i]);
                    userIdArray.add(chatUserId[i]);
                }
                drawerAdapter.notifyDataSetChanged();
            }catch (Exception e) {
                e.printStackTrace();
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        /* 채팅방에 해당하는 앨범 가져오기 */
        albumArray = chatContentDB.selectAlbumImage(todays);
        Log.e("드로어 앨범", albumArray.toString());
        drawerAlbumAdapter.setalbumAdapter(albumArray);
        drawerAlbumAdapter.setRoomId(todays);

        loginId = getLoginid();

        /* 로그인된 아이디, 이름, 프로필 구하기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<SelectVO> loginCall = apiConfig.selectmembers(loginId);
        loginCall.enqueue(new Callback<SelectVO>() {
            @Override
            public void onResponse(Call<SelectVO> call, Response<SelectVO> response) {
                SelectVO s = response.body();
                loginName = s.getUsername();
                loginP = s.getProfile();
            }

            @Override
            public void onFailure(Call<SelectVO> call, Throwable t) {

            }
        });

        /* Navigation Drawer 에서 앨범 클릭하면 Grid 로 앨범 이미지 모두 보이기 */
        albumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goAlbumIntent = new Intent(getApplicationContext(), ChatAlbum.class);
                goAlbumIntent.putExtra("roomId", todays);
                startActivity(goAlbumIntent);
            }
        });

    }

    /* Navigation Drawer 열림 */
    public void showDrawer(View view) {
        chat_drawer.openDrawer(Gravity.RIGHT);
    }

    /* 채팅방 이름 변경 */
    public void setTitle(View v) {
        intent = new Intent(this, ChangeRoomTitle.class);
        intent.putExtra("roomId", todays);
        intent.putExtra("roomName", roomTitle.getText().toString());
        startActivityForResult(intent, CHANGE_TITLE);
        chat_drawer.closeDrawer(Gravity.RIGHT);
    }

    /* 채팅유저 추가하기 */
    public void addChatUser(View view) {
        intent = new Intent(this, SelectFriend.class);
        Log.e("채팅유저추가 roomId", todays);
        intent.putExtra("roomId", todays);
        intent.putExtra("addUserFlag", "true");
        startActivityForResult(intent, ADD_USER);
        chat_drawer.closeDrawer(Gravity.RIGHT);
    }

    /* 채팅방 나가기 */
    public void exitChatRoom(View view) {
        chat_drawer.closeDrawer(Gravity.RIGHT);

        DialogInterface.OnClickListener exitRoomOk = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                /* 채팅방 나간후 채팅목록으로 이동 */
                userIdArray.remove(loginId);
                userNameArray.remove(loginName);
                userProfileArray.remove(loginP);

                if(userIdArray.size() > 0) {
                    /* 채팅방 참여 유저가 1명 이상일때 */
                    for(int j=0 ; j<userIdArray.size() ; j++) {
                        if(j == (userIdArray.size() - 1)) {
                            exitId += userIdArray.get(j);
                            exitName += userNameArray.get(j);
                            exitProfile += userProfileArray.get(j);
                        }else {
                            exitId += userIdArray.get(j) + "-";
                            exitName += userNameArray.get(j) + ",";
                            exitProfile += userProfileArray.get(j) + "-";
                        }
                    }

                    try{
                        jsonObject.put("sendProfile", "exitRoom");
                        jsonObject.put("getUserName", exitName);
                        jsonObject.put("getUser", exitId);
                        jsonObject.put("chatPCount", chatJsonObject.getInt("chatPCount") - 1);
                        jsonObject.put("userProfile", exitProfile);
                        jsonObject.put("newRoomFlag", false);
                        jsonObject.put("LatestChat", loginName + " 님이 채팅방을 나갔습니다.");
                        jsonObject.put("messageDate", "");

                        /* DB 에서 삭제 */
                        chatRoomDB.deleteChatRoom(todays);
                        chatContentDB.deleteChatCon(todays);

                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    /* 채팅목록으로 이동 */
                    intent = new Intent(getApplicationContext(), Chatting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    chatService.sendMessageSocket(jsonObject);

                }else {
                    /* 채팅방 참여 유저가 자신뿐일때 */
                    /* DB 에서 삭제 */
                    chatRoomDB.deleteChatRoom(todays);
                    chatContentDB.deleteChatCon(todays);
                    /* 채팅목록으로 이동 */
                    intent = new Intent(getApplicationContext(), Chatting.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }

            }
        };
        DialogInterface.OnClickListener noExit = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };
        new AlertDialog.Builder(this)
                .setTitle("알림")
                .setMessage("대화내용이 모두 삭제됩니다.\n채팅방을 나가시겠습니까?")
                .setNegativeButton("아니오", noExit)
                .setPositiveButton("예", exitRoomOk)
                .show();
    }

    /* 채팅 메시지 보내기 버튼 */
    public void chatRoomBtn(View view) {
        int id = view.getId();

        switch (id) {
            /* 채팅 메시지 보내기 */
            case R.id.sendChat:
                if(writeChatEt.getText().toString() == null || writeChatEt.getText().toString().equals("")) {
                    Toast.makeText(this, "메시지를 입력해주세요", Toast.LENGTH_SHORT).show();
                }else {
                    /* printwriter 로 서버에 메시지 보내기
                     * json 형식 : {"chatFlag":"true", "roomId" : "룸아이디", "sendUser" : "보내는 유저", "getUser" : "받는 유저", "message" : "내용"} */
                    try{
                        /* 메시지 보낼때마다 보낸 시간 얻기 */
                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String messageDate = simpleDateFormat.format(calendar.getTime());

                        jsonObject.put("LatestChat", writeChatEt.getText().toString());
                        jsonObject.put("messageDate", messageDate);

                        /* 입력한 대화내용 리사이클러뷰에 출력한다 */
                        messageList.add(jsonObject);
                        Message handlerM = handler.obtainMessage();
                        handler.sendMessage(handlerM);

                        /* 대화내용을 서비스를 통해 소켓으로 보낸다 */
                        chatService.sendMessageSocket(jsonObject);

                        /* 채팅방 내용을 업데이트하고, 채팅 내용 DB에 insert 한다, 채팅 카운트 안함 */
                        Log.e("채팅내용 insert 방 아이디", todays);
                        chatRoomDB.updateLatestChat(todays, writeChatEt.getText().toString(), messageDate, 0);
                        chatContentDB.insertChatCon(todays, selectUserid, messageDate, writeChatEt.getText().toString(), getLoginid(), loginProfile);
                        if(jsonObject.getBoolean("newRoomFlag")) {
                            jsonObject.put("newRoomFlag", false);
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    writeChatEt.setText("");
                }
                break;
        }
    }

    /* 이미지 등 파일 전송 */
    public void sendFiles(View v) {
        //  앨범선택, 카메라로 사진찍기 선택할 수 있는 다이얼로그 띄우기
        //  1. 카메라로 찍기
        DialogInterface.OnClickListener cameraListener =  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                takePicture();
            }
        };
        //  2. 앨범에서 가져오기
        DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                takeAlbum();
            }
        };
        //  3. 취소
        DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        };
        //  다이얼로그 띄우기
        new AlertDialog.Builder(this)
                .setTitle("이미지를 선택하세요")
                .setPositiveButton("사진촬영", cameraListener)
                .setNegativeButton("앨범선택", albumListener)
                .setNeutralButton("취소", cancelListener)
                .show();
    }

    /* 기존 채팅방에 해당하는 채팅 내용 불러오기 */
    @Override
    protected void onResume() {
        super.onResume();
        Log.e("onResume", "onResume Start");
        if(messageList.size() > 0) {
            messageList.clear();
        }
        for(JSONObject jsons : chatContentDB.selectChatCon(intent.getStringExtra("roomId"))) {
            messageList.add(jsons);
        }
        conRecycle.scrollToPosition(chatRoomAdapter.getItemCount() - 1);
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
    }

    /* ActivityResult */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            Log.e("정보 변경 안됐음", "정보 변경 안됐음");
        }else {
            switch (requestCode) {
                /* 채팅방 이름 변경 */
                case CHANGE_TITLE:
                    roomTitle.setText(data.getStringExtra("roomName"));
                    drawerRoomTitle.setText(data.getStringExtra("roomName"));
                    break;

                /* 기존 채팅방에 유저 추가 */
                case ADD_USER:
                    Log.e("채팅방 유저 추가", "채팅방 유저 추가 activity result");
                    try {
                        jsonObject.put("newRoomFlag", true);
                        jsonObject.put("getUserName", data.getStringExtra("getUserName"));
                        jsonObject.put("getUser", data.getStringExtra("chatUsers"));
                        jsonObject.put("chatPCount", data.getIntExtra("chatPCount", 0));
                        jsonObject.put("userProfile", data.getStringExtra("userProfile"));

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String adduserMessage = simpleDateFormat.format(calendar.getTime());

                        jsonObject.put("LatestChat", data.getStringExtra("addUser") + " 님을 초대했습니다.");
                        jsonObject.put("messageDate", adduserMessage);

                            /* 입력한 대화내용 리사이클러뷰에 출력한다 */
                        messageList.add(jsonObject);
                        chatRoomAdapter.notifyDataSetChanged();

                        // 채팅방 내용을 업데이트하고, 채팅 내용 DB에 insert 한다, 채팅 카운트 안함
                        Log.e("채팅내용 insert 방 아이디", todays);
                        chatRoomDB.updateLatestChat(todays, data.getStringExtra("addUser") + " 님을 초대했습니다.", adduserMessage, 0);
                        chatContentDB.insertChatCon(todays, data.getStringExtra("chatUsers"), adduserMessage,
                                data.getStringExtra("addUser") + " 님을 초대했습니다.", getLoginid(), loginProfile);

                        if(userNameArray.size() > 0) {
                            userProfileArray.clear();
                            userNameArray.clear();
                            userIdArray.clear();
                        }

                            /* 유저 초대후 채팅유저 목록 보여주기 위해서 */
                        chatUserProfile = data.getStringExtra("userProfile").split("-");
                        chatUsername = data.getStringExtra("getUserName").split(",");
                        chatUserId = data.getStringExtra("chatUsers").split("-");
                        for(int i=0 ; i<chatUsername.length ; i++) {
                            userProfileArray.add(chatUserProfile[i]);
                            userNameArray.add(chatUsername[i]);
                            userIdArray.add(chatUserId[i]);
                        }
                        drawerAdapter.notifyDataSetChanged();

                            /* 유저 초대 후 채팅방 이름 변경 */
                        if(data.getStringExtra("getUserName").contains(roomTitle.getText().toString())) {
                            roomTitle.setText(data.getStringExtra("getUserName"));
                            drawerRoomTitle.setText(data.getStringExtra("getUserName"));
                        }
                    }catch (Exception e) {

                    }
                        /* 대화내용을 서비스를 통해 소켓으로 보낸다 */
                    chatService.sendMessageSocket(jsonObject);
                    break;

                /* 앨범에서 사진 선택 */
                case PICK_ALBUM:
                    album = true;
                    File albumFile = null;
                    try {
                        //  가져온 사진을 저장할 파일 위치를 생성한다
                        albumFile = createimageFile();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(albumFile != null) {
                        //  앨범에서 가져온 사진을 크롭한 결과는 새로운 위치에 저장한다
                        albumUri = Uri.fromFile(albumFile);
                    }
                    //  앨범 이미지의 경로
                    cameraUri = data.getData();
                    cropimage();
                    break;

                /* 카메라로 사진 촬영 */
                case PICK_CAMERA:
                    album = false;
                    cropimage();
                    break;

                /* 이미지 크롭 */
                case CROP_IMAGE:
                    galleryAddPic();
                    /* 이미지 크롭 후 서버로 이미지 전송 및 소켓 메시지 전달 */
                    sendImageToServer(photoPath, imageFileName);
                    break;

            }
        }
    }

    /* 받은 채팅 메시지 리사이클러뷰에 출력 */
    private Runnable showMessage = new Runnable() {
        @Override
        public void run() {
            try{
                JSONObject chatJson = new JSONObject(socketMessage);
                selectUserid = chatJson.get("getUser").toString();
                selectUserName = chatJson.get("getUserName").toString();
                chatPCount = chatJson.getInt("chatPCount");
                Log.e("받은 채팅 업데이트", chatJson.toString());

                /* 현재 들어와있는 채팅방 이름과 넘어온 채팅방 이름이 같을 때에만 메시지 출력 */
                if(intent.getStringExtra("roomId").equals(chatJson.getString("roomId"))) {
                    messageList.add(chatJson);
                    Message message = handler.obtainMessage();
                    handler.sendMessage(message);

                    if(chatJson.getString("LatestChat").contains("/chatImage/")) {
                        albumArray.add(chatJson.getString("LatestChat"));
                        drawerAlbumAdapter.setalbumAdapter(albumArray);
                    }
                }

                /* 유저 추가 후 채팅방 이름 변경 */
                if(chatJson.getString("getUserName").contains(roomTitle.getText().toString()) || chatJson.getString("sendProfile").equals("exitRoom")) {
                    roomTitle.setText(chatJson.getString("getUserName"));
                    drawerRoomTitle.setText(chatJson.getString("getUserName"));

                    setChatUserList(chatJson);

                    jsonObject.put("getUser", chatJson.getString("getUser"));
                    jsonObject.put("getUserName", chatJson.getString("getUserName"));
                    jsonObject.put("chatPCount", chatJson.getInt("chatPCount"));
                    jsonObject.put("userProfile", chatJson.getString("userProfile"));

                }
                if(chatJson.getString("getUser").equals(loginId)) {
                    /* 현재 채팅방에 자신밖에 없을 경우 (모두 나간 경우) */
                    roomTitle.setText("알 수 없음");
                    drawerRoomTitle.setText("알 수 없음");

                    setChatUserList(chatJson);
                }

            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            chatRoomAdapter.notifyItemInserted(messageList.size() - 1);
            conRecycle.scrollToPosition(chatRoomAdapter.getItemCount() - 1);
        }
    };

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

    /* DrawerLayout 채팅 참여 리스트 출력 메소드 */
    public void setChatUserList(JSONObject j) {
        if(userNameArray.size() > 0) {
            userProfileArray.clear();
            userNameArray.clear();
            userIdArray.clear();
        }
        try {
            chatUserProfile = j.getString("userProfile").split("-");
            chatUsername = j.getString("getUserName").split(",");
            chatUserId = j.getString("getUser").split("-");
            for(int i=0 ; i<chatUsername.length ; i++) {
                userProfileArray.add(chatUserProfile[i]);
                userNameArray.add(chatUsername[i]);
                userIdArray.add(chatUserId[i]);
            }
            drawerAdapter.notifyDataSetChanged();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================== 이미지 선택 메소드 ================= */
    //  카메라로 사진 찍어서 크롭하기
    public void takePicture() {
        String state = Environment.getExternalStorageState();
        //  외장 메모리 검사
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            //  인텐트로 카메라 호출
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
                //  사진찍은 후 저장할 임시 파일 선언
                File photoFile = null;
                try{
                    photoFile = createimageFile();
                }catch(Exception e) {
                    Toast.makeText(getApplicationContext(), "createFile Failed", Toast.LENGTH_LONG).show();
                }
                //  폴더가 생성되어 있다면 그 폴더에 사진을 저장한다
                if(photoFile != null) {
                    //  getUriForFile의 두번째 인자는 Manifest 의 provider 에서 authorities 의 경로와 같아야한다
                    cameraUri = FileProvider.getUriForFile(this, "com.example.hyunjujung.yoil.provider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                    startActivityForResult(takePictureIntent, PICK_CAMERA);
                }
            }
        }else {
            Toast.makeText(this, "저장공간에 접근 불가능한 기기입니다", Toast.LENGTH_LONG).show();
        }
    }

    //  앨범에서 선택
    public void takeAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType("image/*");
        albumIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(albumIntent, PICK_ALBUM);
    }

    //  사진을 찍거나 앨범에서 크롭했을 경우 저장할 파일 위치를 생성한다
    public File createimageFile() throws IOException {
        //  특정 경로와 폴더를 지정하지 않고 메모리 최상 위치에 저장한다
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "temp_" + timestamp + ".jpg";
        File imageFiles = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "yoil");
        if(!storageDir.exists()) {
            //  만약 앨범에 폴더가 존재하지 않는다면 폴더를 만들어 저장한다
            storageDir.mkdirs();
        }
        imageFiles = new File(storageDir, imageFileName);
        //  저장된 사진의 절대경로를 저장한다
        photoPath = imageFiles.getAbsolutePath();
        return imageFiles;
    }

    //  사진 크롭하기
    public void cropimage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setDataAndType(cameraUri, "image/*");
        cropIntent.putExtra("scale", true);
        if(album == false) {
            //  카메라로 사진을 찍어서 크롭했을 경우 해당 경로에 저장한다
            cropIntent.putExtra("output", cameraUri);
        }else if(album == true) {
            //  앨범에서 선택해서 크롭했을 경우 해당 경로에 저장한다
            cropIntent.putExtra("output", albumUri);
        }
        startActivityForResult(cropIntent, CROP_IMAGE);
    }

    // 앨범에 크롭한 사진을 업로드한다
    public void galleryAddPic() {
        Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //  해당 경로에 있는 파일을 객체화 (새로 파일을 만든다)
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaIntent.setData(contentUri);
        sendBroadcast(mediaIntent);
    }

    /* 서버 이미지 전송 메소드 */
    public void sendImageToServer(String imagePath, String imageName) {
        File files = new File(imagePath);
        RequestBody filebody = RequestBody.create(MediaType.parse("image/*"), files);
        MultipartBody.Part multipart = MultipartBody.Part.createFormData("uploaded_file", files.getName(), filebody);

        try{
            /* 메시지 보낼때마다 보낸 시간 얻기 */
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String messageDate = simpleDateFormat.format(calendar.getTime());

            jsonObject.put("LatestChat", "/chatImage/" + imageFileName);
            jsonObject.put("messageDate", messageDate);

            /* 입력한 대화내용 리사이클러뷰에 출력한다 */
            messageList.add(jsonObject);
            Message handlerM = handler.obtainMessage();
            handler.sendMessage(handlerM);

            /* 채팅방 내용을 업데이트하고, 채팅 내용 DB에 insert 한다, 채팅 카운트 안함 */
            chatRoomDB.updateLatestChat(todays, "/chatImage/" + imageFileName, messageDate, 0);
            chatContentDB.insertChatCon(todays, selectUserid, messageDate, "/chatImage/" + imageFileName, getLoginid(), loginProfile);
            if(jsonObject.getBoolean("newRoomFlag")) {
                jsonObject.put("newRoomFlag", false);
            }

        }catch (Exception e) {
            e.printStackTrace();
        }

        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<ServerResponse> imageCall = apiConfig.updateChatImage(multipart);
        imageCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                /* 이미지 업로드 후 대화내용을 서비스를 통해 소켓으로 보낸다 */
                chatService.sendMessageSocket(jsonObject);
                albumArray.add("/chatImage/" + imageFileName);
                drawerAlbumAdapter.setalbumAdapter(albumArray);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.e("이미지 업롤드 실패", t.getMessage());
            }
        });
    }

    /* =================== Service CallBack =================== */
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
            if(!socketMessage.contains(todays)) {
                return true;
            }else {
                return false;
            }
        }
    };
}
