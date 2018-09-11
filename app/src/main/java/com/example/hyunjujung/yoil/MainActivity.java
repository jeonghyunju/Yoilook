package com.example.hyunjujung.yoil;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunjujung.yoil.chatting.ChatService;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.CheckPassVO;
import com.example.hyunjujung.yoil.vo.SelectVO;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText loginId, loginPw;
    TextView loginIdcheck, loginPwcheck;
    CheckBox autoLoginchk;

    ChatService chatService;

    String socketMessage;

    String roomIds = "No Enter Room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loginId = (EditText)findViewById(R.id.loginId);
        loginPw = (EditText)findViewById(R.id.loginPw);
        loginIdcheck = (TextView)findViewById(R.id.loginIdcheck);
        loginPwcheck = (TextView)findViewById(R.id.loginPwcheck);
        autoLoginchk = (CheckBox)findViewById(R.id.autoLoginchk);

        //  로그인하기
        //  1. 로그인 버튼 눌렀을때 DB에서 로그인 정보 받아오기
        //  2. 받아온 로그인 정보와 입력한 로그인 정보 비교하기
        //  3. 정보 일치하는지 보기
        //  4. 그냥 로그인 시에 shared에 아이디와 패스워드 저장
        //  5. 자동 로그인 체크했을때는 자동로그인 Shared 에 아이디 패스워드 따로 저장 (앱을 켜고 껐을때 확인하기 위해)


        //  자동로그인 되어있는지 체크
        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        String autoId = autoLogin.getString("autoId", null);
        String autoPw = autoLogin.getString("autoPw", null);
        if(autoId != null && autoPw != null) {
            Toast.makeText(this, autoId + " 님 자동로그인 되었습니다", Toast.LENGTH_SHORT).show();
            Intent autologin = new Intent(this, YoilMain.class);
            startActivity(autologin);
            finish();
        }

        //  존재하는 아이디인지 확인
        loginId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(final Editable editable) {
                ApiConfig apicon = RetrofitCreator.getapiConfig();
                Call<SelectVO> call = apicon.selectmembers(editable.toString());
                call.enqueue(new Callback<SelectVO>() {
                    @Override
                    public void onResponse(Call<SelectVO> call, Response<SelectVO> response) {
                        SelectVO selectVO = response.body();
                        if(selectVO.getUserid() != null) {
                            loginIdcheck.setText("");
                        }else if(editable.toString().equals("")){
                            loginIdcheck.setText("");
                        }else {
                            loginIdcheck.setText("* ID 가 존재하지 않습니다");
                            loginIdcheck.setTextColor(Color.RED);
                        }
                    }

                    @Override
                    public void onFailure(Call<SelectVO> call, Throwable t) {

                    }
                });
            }
        });

    }

    public void loginandjoin(View view) {
        int id = view.getId();
        switch (id) {
            //  로그인
            case R.id.loginokBtn :
                //  로그인 시에 자동로그인 체크함
                if(autoLoginchk.isChecked()) {
                    if(loginId.getText().toString().equals("") || loginPw.getText().toString().equals("")) {
                        Toast.makeText(this, "빈 칸을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else if(loginIdcheck.getText().toString().equals("없음")) {
                        Toast.makeText(this, "ID 를 확인하세요", Toast.LENGTH_SHORT).show();
                    }else {
                        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                        Call<CheckPassVO> call = apiConfig.checkpassword(loginId.getText().toString(), loginPw.getText().toString());
                        call.enqueue(new Callback<CheckPassVO>() {
                            @Override
                            public void onResponse(Call<CheckPassVO> call, Response<CheckPassVO> response) {
                                CheckPassVO checkPassVO = response.body();
                                if(checkPassVO.getCheckPassword() == 0) {
                                    //  비밀번호 불일치
                                    loginPwcheck.setText("* 비밀번호가 일치하지 않습니다");
                                    loginPwcheck.setTextColor(Color.RED);
                                }else {
                                    //  비밀번호 일치, 로그인 성공
                                    //  자동로그인 shared에 저장
                                    loginPwcheck.setText("");
                                    SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
                                    SharedPreferences.Editor autoEdit = autoLogin.edit();
                                    autoEdit.putString("autoId", loginId.getText().toString());
                                    autoEdit.putString("autoPw", loginPw.getText().toString());
                                    autoEdit.putString("autoImg", checkPassVO.getLoginImg());
                                    autoEdit.commit();

                                    /* FCM을 위한 토큰 생성 */
                                    String getToken = FirebaseInstanceId.getInstance().getToken();

                                    /* 아이디랑 생성된 토큰을 DB에 저장 */
                                    ApiConfig apcon = RetrofitCreator.getapiConfig();
                                    Call<ServerResponse> callT = apcon.insertToken(loginId.getText().toString(), getToken);
                                    callT.enqueue(new Callback<ServerResponse>() {
                                        @Override
                                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                            ServerResponse serverResponse = response.body();
                                            Log.d("토큰 등록 성공", serverResponse.getMessage());
                                        }

                                        @Override
                                        public void onFailure(Call<ServerResponse> call, Throwable t) {

                                        }
                                    });

                                    Intent loginok = new Intent(getApplicationContext(), YoilMain.class);
                                    startActivity(loginok);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                }
                                Log.d("로그인 비밀번호", loginPw.getText().toString());
                            }

                            @Override
                            public void onFailure(Call<CheckPassVO> call, Throwable t) {

                            }
                        });
                    }
                }else {
                    //  로그인 시에 자동로그인 체크 안함
                    if(loginId.getText().toString().equals("") || loginPw.getText().toString().equals("")) {
                        Toast.makeText(this, "빈 칸을 모두 입력해주세요", Toast.LENGTH_SHORT).show();
                    }else if(loginIdcheck.getText().toString().equals("없음")) {
                        Toast.makeText(this, "ID 를 확인하세요", Toast.LENGTH_SHORT).show();
                    }else {
                        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                        Call<CheckPassVO> call = apiConfig.checkpassword(loginId.getText().toString(), loginPw.getText().toString());
                        call.enqueue(new Callback<CheckPassVO>() {
                            @Override
                            public void onResponse(Call<CheckPassVO> call, Response<CheckPassVO> response) {
                                CheckPassVO checkPassVO = response.body();
                                if(checkPassVO.getCheckPassword() == 0) {
                                    //  비밀번호 불일치
                                    loginPwcheck.setText("* 비밀번호가 일치하지 않습니다");
                                    loginPwcheck.setTextColor(Color.RED);
                                }else {
                                    //  비밀번호 일치, 로그인 성공
                                    //  자동로그인 shared에 저장
                                    loginPwcheck.setText("");
                                    SharedPreferences noAutoLogin = getSharedPreferences("noAuto", MODE_PRIVATE);
                                    SharedPreferences.Editor noAutoEdit = noAutoLogin.edit();
                                    noAutoEdit.putString("noAutoid", loginId.getText().toString());
                                    noAutoEdit.putString("noAutopw", loginPw.getText().toString());
                                    noAutoEdit.putString("noAutoImg", checkPassVO.getLoginImg());
                                    noAutoEdit.commit();

                                    /* FCM을 위한 토큰 생성 */
                                    String getToken = FirebaseInstanceId.getInstance().getToken();

                                    /* DB에 기기토큰 저장 */
                                    ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                                    Call<ServerResponse> callnoAuto = apiConfig.insertToken(loginId.getText().toString(), getToken);
                                    callnoAuto.enqueue(new Callback<ServerResponse>() {
                                        @Override
                                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                                            ServerResponse serverResponse = response.body();
                                            Log.d("토큰 등록 성공", serverResponse.getMessage());
                                        }

                                        @Override
                                        public void onFailure(Call<ServerResponse> call, Throwable t) {

                                        }
                                    });

                                    Intent oklogin = new Intent(getApplicationContext(), YoilMain.class);
                                    startActivity(oklogin);

                                    /* 자동 로그인이 아닐때는 로그인 했을때 서비스를 start 해서 소켓을 연결한다 */
                                    Intent serviceIntent = new Intent(getApplicationContext(), ChatService.class);
                                    startService(serviceIntent);
                                    finish();
                                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                }
                                Log.d("로그인 비밀번호", loginPw.getText().toString());

                            }

                            @Override
                            public void onFailure(Call<CheckPassVO> call, Throwable t) {

                            }
                        });
                        /* 로그인 시에 로그인 정보를 서버에 넘겨서 소켓을 구분할 수 있도록 한다 */
                        JSONObject jsonObject = new JSONObject();
                        try{
                            jsonObject.put("chatFlag", false);
                            jsonObject.put("roomId", "");
                            jsonObject.put("sendUser", loginId.getText().toString());
                            jsonObject.put("getUser", "");
                            jsonObject.put("LatestChat", "");
                            jsonObject.put("newRoomFlag", false);
                            jsonObject.put("getUserName", "");
                            jsonObject.put("chatPCount", 0);
                            jsonObject.put("messageDate", "");
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        chatService.sendMessageSocket(jsonObject);
                    }
                }

                break;

            //  회원가입 하기
            case R.id.joinBtn :
                Intent intent = new Intent(this, Join.class);
                startActivity(intent);
                break;
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
