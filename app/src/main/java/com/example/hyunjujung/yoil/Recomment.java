package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.apdater.CommentAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.CommentList;
import com.example.hyunjujung.yoil.vo.CommentVO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Recomment extends AppCompatActivity {
    CircleImageView writePI;
    TextView writeIds, writeCon;
    EditText commentEt;
    Button commentOkBtn;
    ImageButton cancelBtn;
    RecyclerView commentRecycle;
    RecyclerView.LayoutManager commentlayout;

    CommentAdapter commentAdapter;
    ArrayList<CommentVO> commentList = new ArrayList<>();


    Intent intent;

    //  전달된 게시글 내용 저장하는 변수
    static String content = "";
    static String writeid = "";
    static String writeimg = "";
    static int writeidx = 0;

    //  shared에 저장된 로그인 이미지 저장 변수
    static String loginImg = "";

    //  인덱스 저장변수
    static int cGroup = 0;

    //  댓글 아이디 저장 변수
    static String commentId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recomment);
        writePI = (CircleImageView)findViewById(R.id.writePI);
        writeIds = (TextView)findViewById(R.id.writeIds);
        writeCon = (TextView)findViewById(R.id.writeCon);
        commentEt = (EditText)findViewById(R.id.commentEt);
        commentOkBtn = (Button)findViewById(R.id.commentOkBtn);
        commentRecycle = (RecyclerView) findViewById(R.id.commentRecycle);
        cancelBtn = (ImageButton)findViewById(R.id.cancelBtn);

        intent = getIntent();
        content = intent.getStringExtra("content");
        writeid = intent.getStringExtra("writeid");
        writeimg = intent.getStringExtra("writeImg");
        writeidx = intent.getIntExtra("index", 0);

        /* 댓글 작성하는 화면에서 댓글 쓰려는 게시물의 작성자 프로필, 아이디, 게시글 내용 보이기 */
        writeIds.setText(writeid);
        writeCon.setText(content);
        Glide.with(this).load("http://13.124.12.50" + writeimg).into(writePI);

        commentlayout = new LinearLayoutManager(this);
        commentRecycle.setLayoutManager(commentlayout);

        //  데이터와 view 연결
        commentAdapter = new CommentAdapter(this, commentList);
        commentRecycle.setAdapter(commentAdapter);

        /* RecyclerView Swipe */

        Log.d("Recomment onCreate", "Recomment onCreate !!!!!!!!!!");
        if(commentEt.getText().toString().contains("@")) {
            //  대댓글 일때 댓글달기 버튼 태그
            commentOkBtn.setTag("recommentOn");
        }else {
            //  댓글일때 댓글달기 버튼 태그
            commentOkBtn.setTag("recommentOff");
        }

        commentOkBtn.setEnabled(false);
        commentOkBtn.setTextColor(Color.rgb(162, 168, 178));

        //  EditText에 아무 글도 써있지 않을때 버튼 비활성화
        commentEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                int i = editable.toString().indexOf(" ");
                String realContent = editable.toString().substring(i + 1);
                if(realContent.equals("") && editable.toString().contains("@")) {
                    Log.d("대댓글달기 지울때", realContent);
                    commentOkBtn.setEnabled(false);
                }else if(editable.toString().equals("")) {
                    cancelBtn.setVisibility(View.INVISIBLE);
                    commentOkBtn.setTag("recommentOff");
                    commentOkBtn.setEnabled(false);
                    commentOkBtn.setTextColor(Color.rgb(162, 168, 178));
                }else {
                    commentOkBtn.setEnabled(true);
                    commentOkBtn.setTextColor(Color.rgb(36, 86, 165));
                }
            }
        });

    }

    //  댓글 달기 버튼
    public void clickBtn(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.commentOkBtn:
                /* 댓글 등록 !!!!!! ( 댓글, 대댓글 판단 기준 '게시' 버튼 태그 )
                   댓글 달고 게시글 작성자에게 알림보내기
                 * 본인 게시글에 댓글 달 경우에는 알림이 가지 않도록 한다 */
                String commentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                Log.d("댓글 태그", commentOkBtn.getTag().toString());
                // DB에 댓글 insert
                ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                if(commentOkBtn.getTag().equals("recommentOff")) {
                    //  댓글 버튼 태그가 recommentOff면 그냥 댓글 달기
                    Call<CommentList> call = apiConfig.insertComment(getLoginid(), loginImg, commentEt.getText().toString(), commentDate, writeidx);
                    call.enqueue(new Callback<CommentList>() {
                        @Override
                        public void onResponse(Call<CommentList> call, Response<CommentList> response) {
                            //  DB에서 받아온 댓글 리스트 뿌리기
                            commentList = response.body().getCommentList();
                            commentAdapter.updateComment(commentList);
                        }

                        @Override
                        public void onFailure(Call<CommentList> call, Throwable t) {

                        }
                    });

                    /* 게시물 작성자한테 알림 보내기 */
                    String sendMessage = " 님이 게시물에 댓글을 남겼습니다";
                    sendFCM(writeid, getLoginid(), sendMessage);
                }else {
                    /*  대댓글 등록 !!!!!! ( 댓글, 대댓글 판단 기준 '게시' 버튼 태그 )
                     *  앞에 아이디 자르고 내용에 저장하기 */
                    int i = commentEt.getText().toString().indexOf(" ");
                    String realContent = commentEt.getText().toString().substring(i + 1);
                    Log.d("앞에 아이디 자른거", realContent);
                    Log.d("cGroup", "" + cGroup);
                    Call<CommentList> calls = apiConfig.insertRecomment(getLoginid(), loginImg, realContent, commentDate, writeidx, cGroup);
                    calls.enqueue(new Callback<CommentList>() {
                        @Override
                        public void onResponse(Call<CommentList> call, Response<CommentList> response) {
                            commentList = response.body().getCommentList();
                            commentAdapter.updateComment(commentList);
                        }

                        @Override
                        public void onFailure(Call<CommentList> call, Throwable t) {

                        }
                    });

                    /* 댓글 작성자한테 알림 보내기 */
                    String sendMessage = " 님이 회원님을 언급했습니다";
                    Log.d("댓글 아이디", commentId);
                    sendFCM(commentId, getLoginid(), sendMessage);
                }
                commentEt.setText("");
                break;

            //  대댓글 달기 취소 버튼
            case R.id.cancelBtn:
                commentEt.setText("");
                commentOkBtn.setTag("recommentOff");
                commentOkBtn.setEnabled(false);
                cancelBtn.setVisibility(View.INVISIBLE);
                break;
        }
    }

    /* 현재 로그인 된 아이디 가져오는 메소드 */
    public String getLoginid() {
        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동 로그인 일때
            loginImg = autoLogin.getString("autoImg", null);
            return autoLogin.getString("autoId", null);
        }else {
            SharedPreferences noAuto = getSharedPreferences("noAuto", MODE_PRIVATE);
            loginImg = noAuto.getString("noAutoImg", null);
            return noAuto.getString("noAutoid", null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCommentList(writeidx);
        Log.d("onResume", "onResume!!!!!!!" + writeidx);
    }

    /* 댓글 리스트 받아오기 */
    public void getCommentList(int writeidx) {
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<CommentList> call = apiConfig.selectComment(writeidx);
        call.enqueue(new Callback<CommentList>() {
            @Override
            public void onResponse(Call<CommentList> call, Response<CommentList> response) {
                commentList = response.body().getCommentList();
                commentAdapter.updateComment(commentList);
            }

            @Override
            public void onFailure(Call<CommentList> call, Throwable t) {

            }
        });
    }

    /* 대댓글 달때 댓글 다는 EditText 부분에 @아이디 입력하고 태그 바꾸기 */
    public void setRecommentEt(String recommentid) {
        commentId = recommentid;
        commentEt.setText("@" + recommentid + " ");
        commentOkBtn.setTag("recommentOn");
        cancelBtn.setVisibility(View.VISIBLE);
        Editable commentText = commentEt.getText();
        Selection.setSelection(commentText, commentText.length());
    }

    /* 대댓글의 cGroup 값을 위해 position의 idx 값 받아오기 */
    public void getRecomIdx(int idx) {
        cGroup = idx;
    }

    /* FCM 보내기 */
    public void sendFCM(String writeid, String loginid, String messages) {
        ApiConfig apicon = RetrofitCreator.getapiConfig();
        Call<ServerResponse> serverCall = apicon.sendfcm(writeid, loginid, messages);
        serverCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

            }
        });
    }

}
