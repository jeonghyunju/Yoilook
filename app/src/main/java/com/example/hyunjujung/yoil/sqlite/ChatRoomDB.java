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

public class ChatRoomDB extends SQLiteOpenHelper {

    /* ChatRoomDB 생성자로 관리할 DB 이름과 버전 정보를 받는다 */
    public ChatRoomDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /* DB 새로 생성할 때 호출되는 함수 */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE chatRooms (idx INTEGER PRIMARY KEY AUTOINCREMENT, roomId TEXT, userName TEXT, roomName TEXT, chatUsers TEXT, chatPCount INTEGER, LatestChat TEXT, LatestTime TEXT, newChatCount INTEGER, userProfile TEXT);");
    }

    /* DB 업그레이드를 위해 버전이 변경될 때 호출되는 함수 */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /* DB select */
    /* 채팅방 리스트 모두 출력 */
    public ArrayList<JSONObject> selectChatRoom() {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<JSONObject> allRoom = new ArrayList<>();

        /* 사용자의 채팅방 리스트를 모두 출력한다 */
        Cursor cursor = database.rawQuery("SELECT * FROM chatRooms ORDER BY LatestTime DESC", null);
        while(cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("idx", cursor.getInt(0));
                jsonObject.put("roomId", cursor.getString(1));
                jsonObject.put("getUserName", cursor.getString(2));
                jsonObject.put("roomName", cursor.getString(3));
                jsonObject.put("chatUsers", cursor.getString(4));
                jsonObject.put("chatPCount", cursor.getInt(5));
                jsonObject.put("LatestChat", cursor.getString(6));
                jsonObject.put("LatestTime", cursor.getString(7));
                jsonObject.put("newChatCount", cursor.getInt(8));
                jsonObject.put("userProfile", cursor.getString(9));

                allRoom.add(jsonObject);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allRoom;
    }
    /* roomId 에 해당하는 채팅방 출력 */
    public JSONObject selectRoomId(String roomId) {
        SQLiteDatabase database = getReadableDatabase();
        JSONObject jsonObject = new JSONObject();
        Cursor cursor = database.rawQuery("SELECT * FROM chatRooms WHERE roomId='" + roomId + "'", null);
        while(cursor.moveToNext()) {
            try{
                jsonObject.put("idx", cursor.getInt(0));
                jsonObject.put("roomId", cursor.getString(1));
                jsonObject.put("getUserName", cursor.getString(2));
                jsonObject.put("roomName", cursor.getString(3));
                jsonObject.put("chatUsers", cursor.getString(4));
                jsonObject.put("chatPCount", cursor.getString(5));
                jsonObject.put("LatestChat", cursor.getString(6));
                jsonObject.put("LatestTime", cursor.getString(7));
                jsonObject.put("newChatCount", cursor.getInt(8));
                jsonObject.put("userProfile", cursor.getString(9));
            }catch (Exception e) {

            }
        }
        return jsonObject;
    }
    /* 존재하는 채팅방 id 모두 가져오기 */
    public String selectChatRoomId() {
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<String> idArray = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT roomId FROM chatRooms", null);
        while(cursor.moveToNext()) {
            idArray.add(cursor.getString(0));
        }
        return idArray.toString();
    }

    /* DB insert */
    public void insertChatRoom(String roomId, String userName, String roomName, String chatUsers, int chatPCount, String LatestChat, String LatestTime, int newChatCount, String userProfile) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("INSERT INTO chatRooms(roomId, userName, roomName, chatUsers, chatPCount, LatestChat, LatestTime, newChatCount, userProfile) VALUES('" + roomId + "', '" + userName + "', '" + roomName + "', '" + chatUsers + "', " + chatPCount + ", '" + LatestChat + "', '" + LatestTime + "', " + newChatCount + ", '" + userProfile + "');");
        database.close();
    }

    /* DB update */
    /* 최신 채팅 내용, 채팅 시간 업데이트 */
    public void updateLatestChat(String roomId, String LatestChat, String LatestTime, int newChatCount) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("UPDATE chatRooms SET LatestChat='" + LatestChat + "', LatestTime='" + LatestTime + "', newChatCount=" + newChatCount + " WHERE roomId='" + roomId + "';");
        database.close();
    }
    /* 채팅방 카운트 업데이트 */
    public void updateChatCount(String roomId, String LatestChat, String LatestTime) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("UPDATE chatRooms SET LatestChat='" + LatestChat + "', LatestTime='" + LatestTime + "', newChatCount=newChatCount+1 WHERE roomId='" + roomId +"';");
        Log.e("카운트 +1", roomId);
        database.close();
    }
    /* 방이름 수정 */
    public void updateRoomName(String roomId, String roomName) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("UPDATE chatRooms SET roomName='" + roomName + "' WHERE roomId='" + roomId + "';");
        database.close();
    }
    /* 기존 채팅방 유저 추가 (방이름 변경 안함)*/
    public void updateUserList(String roomId, String userName, String chatUsers, int chatPCount, String userProfile) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("UPDATE chatRooms SET userName='" + userName + "', chatUsers='" + chatUsers + "', chatPCount=" + chatPCount + ", userProfile='" + userProfile + "' WHERE roomId='" + roomId + "';");
        database.close();
    }
    /* 기존 채팅방 유저 추가 (방이름 변경) */
    public void updateUsers(String roomId, String userName, String roomName, String chatUsers, int chatPCount, String userProfile) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("UPDATE chatRooms SET userName='" + userName + "', roomName='" + roomName + "', chatUsers='" + chatUsers + "', chatPCount=" + chatPCount + ", userProfile='" + userProfile + "' WHERE roomId='" + roomId + "';");
        database.close();
    }

    /* DB delete */
    public void deleteChatRoom(String roomId) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL("DELETE FROM chatRooms WHERE roomId='" + roomId + "';");
        database.close();
    }
}
