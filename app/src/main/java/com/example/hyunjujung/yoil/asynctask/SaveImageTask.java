package com.example.hyunjujung.yoil.asynctask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.hyunjujung.yoil.ChatAlbumSlide;
import com.example.hyunjujung.yoil.apdater.ChatAlbumAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hyunjujung on 2017. 12. 14..
 */

public class SaveImageTask extends AsyncTask<String, Void, Void> {
    Context context;
    ArrayList<String> imageFileNameArray = new ArrayList<>();
    ChatAlbumAdapter albumAdapter;
    boolean isMultiCheckAlbum = false;

    public SaveImageTask(Context context, ArrayList<String> imageFileNameArray) {
        this.context = context;
        this.imageFileNameArray = imageFileNameArray;
    }

    public SaveImageTask(Context context, ArrayList<String> imageFileNameArray, ChatAlbumAdapter albumAdapter, boolean isMultiCheckAlbum) {
        this.context = context;
        this.imageFileNameArray = imageFileNameArray;
        this.albumAdapter = albumAdapter;
        this.isMultiCheckAlbum = isMultiCheckAlbum;
    }

    @Override
    protected Void doInBackground(String... strings) {
        String imageFileName;
        InputStream input = null;
        for(String temp : imageFileNameArray) {
            Log.e("이미지 이름", temp);
        }

        try {
            for(int i=0 ; i<imageFileNameArray.size() ; i++) {
                Bitmap imagebitmap = null;
                String downUrl = "http://13.124.12.50" + imageFileNameArray.get(i);
                Log.e("downUrl", downUrl);
                input = new java.net.URL(downUrl).openStream();
                imagebitmap = BitmapFactory.decodeStream(input);
                input.close();

                String timestamp = new java.text.SimpleDateFormat("yyMMdHHmmss").format(new Date());
                imageFileName = "yoilook_" + i + "_" + timestamp + ".jpg";
                OutputStream output = null;
                File imageFiles = null;
                File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "yoilook");
                if(!storageDir.exists()) {
                    storageDir.mkdirs();
                }
                imageFiles = new File(storageDir, imageFileName);
                output = new FileOutputStream(imageFiles);
                imagebitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                output.flush();
                output.close();

                /* 갤러리에 이미지 저장 후에 갤러리 refresh 해줘야한다. */
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri uri = Uri.fromFile(imageFiles);
                intent.setData(uri);
                context.sendBroadcast(intent);

                //Toast.makeText(context, "사진이 저장되었습니다." , Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void bitmap) {
        super.onPostExecute(bitmap);
        if(isMultiCheckAlbum) {
            albumAdapter.fileArrayClear(true);
        }
    }
}
