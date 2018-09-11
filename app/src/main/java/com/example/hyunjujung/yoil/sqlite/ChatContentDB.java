package com.example.hyunjujung.yoil.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by hyunjujung on 2017. 11. 13..
 */

public class ChatContentDB extends SQLiteOpenHelper{

    public ChatContentDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE chatContents (idx INTEGER PRIMARY KEY AUTOINCREMENT, roomId TEXT, chatUsers TEXT, messageDate TEXT, messages TEXT, sendMUsers TEXT, sendProfile TEXT);");
        Log.d("채팅내용 테이블", "gogogog");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /* DB select */
    public ArrayList<JSONObject> selectChatCon(String roomId) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<JSONObject> allChat = new ArrayList<>();

        /* roomId 에 해당하는 채팅 내용 모두 출력한다 */
        Cursor cursor = database.rawQuery("SELECT * FROM chatContents WHERE roomId = '" + roomId + "'", null);
        while(cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("idx", cursor.getInt(0));
                jsonObject.put("roomId", cursor.getString(1));
                jsonObject.put("chatUsers", cursor.getString(2));
                jsonObject.put("messageDate", cursor.getString(3));
                jsonObject.put("LatestChat", cursor.getString(4));
                jsonObject.put("sendUser", cursor.getString(5));
                jsonObject.put("sendProfile", cursor.getString(6));
            }catch (Exception e) {
                e.printStackTrace();
            }
            allChat.add(jsonObject);
        }
        return allChat;
    }
    /* roomId에 해당하는 앨범 이미지 경로만 모두 불러오기 */
    public ArrayList<String> selectAlbumImage(String roomId) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<String> allAlbum = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT messages FROM chatContents WHERE roomId='" + roomId + "' AND messages LIKE '%/chatImage/%'", null);
        while(cursor.moveToNext()) {
            allAlbum.add(cursor.getString(0));
        }
        return allAlbum;
    }
    /* roomId 와 messages 의 이미지에 해당하는 데이터 모두 불러오기 */
    public ArrayList<JSONObject> selectAlbums(String roomId) {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<JSONObject> albums = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM chatContents WHERE roomId='" + roomId + "' AND messages LIKE '%/chatImage/%'", null);
        while(cursor.moveToNext()) {
            JSONObject albumJson = new JSONObject();
            try {
                albumJson.put("idx", cursor.getInt(0));
                albumJson.put("roomId", cursor.getString(1));
                albumJson.put("chatUsers", cursor.getString(2));
                albumJson.put("messageDate", cursor.getString(3));
                albumJson.put("LatestChat", cursor.getString(4));
                albumJson.put("sendUser", cursor.getString(5));
                albumJson.put("sendProfile", cursor.getString(6));
            }catch (Exception e) {
                e.printStackTrace();
            }
            albums.add(albumJson);
        }
        return albums;
    }

    /* DB insert */
    public void insertChatCon(String roomId, String chatUsers, String messageDate, String messages, String sendMUsers, String sendProfile) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("INSERT INTO chatContents(roomId, chatUsers, messageDate, messages, sendMUsers, sendProfile) VALUES('" + roomId + "', '" + chatUsers + "', '" + messageDate + "', '" + messages + "', '" + sendMUsers + "', '" + sendProfile + "');");
        database.close();
    }

    /* DB update */

    /* DB delete */
    public void deleteChatCon(String roomId) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM chatContents WHERE roomId='" + roomId +"';");
        database.close();
    }
}
