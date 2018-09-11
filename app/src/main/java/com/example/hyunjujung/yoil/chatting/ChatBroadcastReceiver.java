package com.example.hyunjujung.yoil.chatting;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by hyunjujung on 2017. 11. 9..
 */

public class ChatBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            /* 자동로그인 되어있을때만 broadcast 에서 서비스를 start 시켜서 소켓을 연결한다 */
            SharedPreferences autoLogin = context.getSharedPreferences("auto", Context.MODE_PRIVATE);
            if(autoLogin.getString("autoId", null) != null) {
                Intent rIntent = new Intent(context, ChatService.class);
                context.startService(rIntent);
            }
        }
    }
}
