package com.example.hyunjujung.yoil;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.SelectVO;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteTimeline extends AppCompatActivity {
    ImageView uploadImg;
    EditText contentEt;
    TextView text1;
    Button questionBtn, infoBtn, etcBtn, writeOkbtn;
    String autoId, noAutoId, contentType;

    //  카메라로 찍어서 크롭한 uri와 앨범에서 가져와서 크롭한 uri
    Uri cameraUri, albumUri;

    //  이미지의 현재 경로
    static String photoPath;

    //  카메라로 찍어서 크롭한 경로와 앨범에서 가져와서 크롭한 경로를 다르게 지정해주기 위해 선언해주는 변수
    Boolean album = false;

    //  현재 로그인 된 아이디 프로필 사진
    String currentProfile;

    static String imageFileName = "";

    static final int pickAlbum = 0;
    static final int pickCamera = 1;
    static final int cropImage = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_timeline);
        uploadImg = (ImageView)findViewById(R.id.uploadImg);
        contentEt = (EditText)findViewById(R.id.contentEt);
        text1 = (TextView)findViewById(R.id.text1);
        questionBtn = (Button)findViewById(R.id.questionBtn);
        infoBtn = (Button)findViewById(R.id.infoBtn);
        etcBtn = (Button)findViewById(R.id.etcBtn);
        writeOkbtn = (Button)findViewById(R.id.writeOkbtn);

        //  글 작성하기
        //  1. 현재 로그인 된 아이디 받아오기
        //  2. 작성한 정보 서버에 저장
        //  3. 이어 작성하기 기능 추가

        SharedPreferences loginID = getSharedPreferences("noAuto", MODE_PRIVATE);
        if(loginID.getString("noAutoid", null).equals("")) {
            //  자동 로그인 일때 저장된 아이디 받기
            SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
            autoId = autoLogin.getString("autoId", null);
            //  현재 로그인 된 아이디 정보 받아서 저장
            ApiConfig apicon = RetrofitCreator.getapiConfig();
            retrofit2.Call<SelectVO> call = apicon.selectmembers(autoId);
            call.enqueue(new Callback<SelectVO>() {
                @Override
                public void onResponse(retrofit2.Call<SelectVO> call, Response<SelectVO> response) {
                    SelectVO selectVO = response.body();
                    //  받아온 이미지 경로 저장
                    currentProfile = selectVO.getProfile();
                }

                @Override
                public void onFailure(retrofit2.Call<SelectVO> call, Throwable t) {

                }
            });
        }else {
            //  자동 로그인 아닐때 저장된 아이디 받기
            noAutoId = loginID.getString("noAutoid", null);
            //  현재 로그인 된 아이디 정보 받아서 저장
            ApiConfig apicon = RetrofitCreator.getapiConfig();
            //RequestBody.create(MediaType.parse("text/plain"), editable.toString());
            retrofit2.Call<SelectVO> call = apicon.selectmembers(noAutoId);
            call.enqueue(new Callback<SelectVO>() {
                @Override
                public void onResponse(retrofit2.Call<SelectVO> call, Response<SelectVO> response) {
                    SelectVO selectVO = response.body();
                    //  받아온 이미지 경로 저장
                    currentProfile = selectVO.getProfile();
                }

                @Override
                public void onFailure(retrofit2.Call<SelectVO> call, Throwable t) {

                }
            });
        }

    }

    public void clickBtn(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.questionBtn:
                //  질문 버튼 눌렀을때 정보, 기타 버튼은 색 연하게
                questionBtn.setBackgroundResource(R.drawable.btn_stroke);
                questionBtn.setTextColor(Color.rgb(119, 128, 173));
                infoBtn.setBackgroundResource(R.drawable.btn_stroke_gray);
                infoBtn.setTextColor(Color.rgb(182, 184, 186));
                etcBtn.setBackgroundResource(R.drawable.btn_stroke_gray);
                etcBtn.setTextColor(Color.rgb(182, 184, 186));
                contentType = questionBtn.getText().toString();
                break;

            case R.id.infoBtn:
                questionBtn.setBackgroundResource(R.drawable.btn_stroke_gray);
                questionBtn.setTextColor(Color.rgb(182, 184, 186));
                infoBtn.setBackgroundResource(R.drawable.btn_stroke);
                infoBtn.setTextColor(Color.rgb(119, 128, 173));
                etcBtn.setBackgroundResource(R.drawable.btn_stroke_gray);
                etcBtn.setTextColor(Color.rgb(182, 184, 186));
                contentType = infoBtn.getText().toString();
                break;

            case R.id.etcBtn:
                questionBtn.setBackgroundResource(R.drawable.btn_stroke_gray);
                questionBtn.setTextColor(Color.rgb(182, 184, 186));
                infoBtn.setBackgroundResource(R.drawable.btn_stroke_gray);
                infoBtn.setTextColor(Color.rgb(182, 184, 186));
                etcBtn.setBackgroundResource(R.drawable.btn_stroke);
                etcBtn.setTextColor(Color.rgb(119, 128, 173));
                contentType = etcBtn.getText().toString();
                break;

            case R.id.uploadImg:
                //  사진 올리기
                text1.setVisibility(View.INVISIBLE);
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
                        text1.setVisibility(View.VISIBLE);
                    }
                };
                //  다이얼로그 띄우기
                new AlertDialog.Builder(this)
                        .setTitle("이미지를 선택하세요")
                        .setPositiveButton("사진촬영", cameraListener)
                        .setNegativeButton("앨범선택", albumListener)
                        .setNeutralButton("취소", cancelListener)
                        .show();
                break;

            case R.id.writeOkbtn:
                //  서버에 글 저장
                /* Retrofit 사용 !!!!!! */
                ApiConfig apiConfig = RetrofitCreator.getapiConfig();

                Map<String, RequestBody> map = new HashMap<>();
                //  전송할 텍스트 Map에 담기
                if(autoId == null) {
                    //  자동로그인 아닐때
                    map.put("writeid", toRequestBody(noAutoId));
                }else {
                    //  자동 로그인 일때
                    map.put("writeid", toRequestBody(autoId));
                }
                map.put("writeprofile", toRequestBody(currentProfile));
                map.put("writedate", toRequestBody(new SimpleDateFormat("yyyy년 MM월 dd일").format(new Date())));
                map.put("writecontent", toRequestBody(contentEt.getText().toString()));
                map.put("writetype", toRequestBody(contentType));

                if(photoPath != null) {
                    File filepath = new File(photoPath);
                    Log.d("사진 경로", photoPath);
                    //  이미지 전송할때
                    RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), filepath);
                    map.put("uploaded_file\"; filename=\"" + imageFileName + "\"", fileBody);
                }

                retrofit2.Call<ServerResponse> responseCall = apiConfig.saveTimeline(map);
                responseCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<ServerResponse> call, Response<ServerResponse> response) {
                        //  서버와 통신 후 받아온 결과물 출력
                        ServerResponse serverResponse = response.body();
                        Log.v("받아옴?", serverResponse.getMessage());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ServerResponse> call, Throwable t) {

                    }
                });
                Toast.makeText(this, "글이 작성되었습니다", Toast.LENGTH_SHORT).show();
                Intent writeTimelineOk = new Intent(this, YoilMain.class);
                startActivity(writeTimelineOk);
                finish();
                break;
        }
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case pickAlbum:
                    //  앨범에서 사진 가져오기
                    //  가져온 사진 경로
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
                case pickCamera:
                    //  카메라로 찍기
                    album = false;
                    //galleryAddPic();
                    cropimage();
                    break;
                case cropImage:
                    //  이미지 크롭
                    galleryAddPic();
                    if(album == false) {
                        //  카메라로 사진찍었을 경우 해당 uri에서 경로를 얻어와 이미지뷰에 넣는다
                        uploadImg.setImageURI(cameraUri);
                    }else{
                        //  앨범에서 사진을 가져왔을 경우 해당 uri에서 경로를 얻어와 이미지뷰에 넣는다
                        uploadImg.setImageURI(albumUri);
                    }
                    break;
            }
        }
    }

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
                    //  getUriForFile의 두번째 인자는 Manifest의 provider에서 auauthorities의 경로와 같아야한다
                    cameraUri = FileProvider.getUriForFile(this, "com.example.hyunjujung.yoil.provider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
                    startActivityForResult(takePictureIntent, pickCamera);
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
        startActivityForResult(albumIntent, pickAlbum);
    }

    //  사진을 찍거나 앨범에서 크롭했을 경우 저장할 파일 위치를 생성한다
    public File createimageFile() throws IOException {
        //  특정 경로와 폴더를 지정하지 않고 메모리 최상 위치에 저장한다
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "temp_" + timestamp + ".jpg";
        File imageFiles = null;
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "yoil");
        if(!storageDir.exists()) {
            //  만약 앨범에 파일이 존재하지 않는다면 폴더를 만들어 저장한다
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
        startActivityForResult(cropIntent, cropImage);
    }

    // 앨범에 크롭한 사진을 업로드한다
    public void galleryAddPic() {
        Intent mediaIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        //  해당 경로에 있는 파일을 객체화 (새로 파일을 만든다)
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaIntent.setData(contentUri);
        sendBroadcast(mediaIntent);
        //Toast.makeText(this, "프로필 사진이 등록되었습니다", Toast.LENGTH_LONG).show();
        //Log.d("절대 경로 : ", photoPath);
    }
}
