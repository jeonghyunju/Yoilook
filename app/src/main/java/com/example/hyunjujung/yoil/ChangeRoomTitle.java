package com.example.hyunjujung.yoil;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.hyunjujung.yoil.chatting.ChattingRoom;
import com.example.hyunjujung.yoil.sqlite.ChatRoomDB;

public class ChangeRoomTitle extends AppCompatActivity {
    EditText changeRoomTitle;
    ImageButton deleteTitle;

    Intent intent;

    String roomId;

    /* DB */
    ChatRoomDB chatRoomDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_room_title);

        chatRoomDB = new ChatRoomDB(this, "0", null, 1);

        changeRoomTitle = (EditText)findViewById(R.id.changeRoomTitle);
        deleteTitle = (ImageButton)findViewById(R.id.deleteTitle);

        intent = getIntent();
        roomId = intent.getStringExtra("roomId");
        changeRoomTitle.setHint(intent.getStringExtra("roomName"));
        changeRoomTitle.setText(intent.getStringExtra("roomName"));

        /* edittext 포커스 뒤로 주기 */
        Editable editable = changeRoomTitle.getText();
        Selection.setSelection(editable, changeRoomTitle.length());

        /* 글이 써지면 deleteTitle 버튼 나오게한다 */
        changeRoomTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                deleteTitle.setVisibility(View.VISIBLE);
            }
        });

    }

    public void clickBtn(View view) {
        int id = view.getId();
        switch (id) {
            /* 채팅방 이름 변경 완료
             * DB 에 채팅방 이름 변경하기 */
            case R.id.changeTitleDone:
                if(changeRoomTitle.getText().toString().equals("")) {
                    Toast.makeText(this, "채팅방 이름을 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    /* DB update */
                    chatRoomDB.updateRoomName(roomId, changeRoomTitle.getText().toString());
                    intent = new Intent(this, ChattingRoom.class);
                    intent.putExtra("roomId", roomId);
                    intent.putExtra("newRoomFlag", "false");
                    intent.putExtra("roomName", changeRoomTitle.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;

            /* 채팅방 이름 EditText 글자 지우기 */
            case R.id.deleteTitle:
                changeRoomTitle.setText("");
                deleteTitle.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
