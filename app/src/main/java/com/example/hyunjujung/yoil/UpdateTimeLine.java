package com.example.hyunjujung.yoil;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.TimelineVO;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateTimeLine extends AppCompatActivity {
    CircleImageView myProfileImg;
    ImageView updateImg;
    TextView myIdTv, writeimgNull;
    EditText updateContent;
    Spinner typeSpinner;

    Intent intent;
    TimelineVO timelineVO;
    String[] typeArray = {"질문", "정보", "기타"};

    //  카메라로 찍어서 크롭한 uri와 앨범에서 가져와서 크롭한 uri
    Uri cameraUri, albumUri;

    //  이미지의 현재 경로
    static String photoPath;

    //  카메라로 찍어서 크롭한 경로와 앨범에서 가져와서 크롭한 경로를 다르게 지정해주기 위해 선언해주는 변수
    Boolean album = false;

    static String imageFileName = "";

    static final int pickAlbum = 0;
    static final int pickCamera = 1;
    static final int cropImage = 2;

    static int updateIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_time_line);

        myProfileImg = (CircleImageView)findViewById(R.id.myProfileImg);
        updateImg = (ImageView)findViewById(R.id.updateImg);
        myIdTv = (TextView)findViewById(R.id.myIdTv);
        writeimgNull = (TextView)findViewById(R.id.writeimgNull);
        updateContent = (EditText)findViewById(R.id.updateContent);

        /* 스피너 연결 */
        typeSpinner = (Spinner)findViewById(R.id.typeSpinner);
        ArrayAdapter<CharSequence> spinnerAdap = ArrayAdapter.createFromResource(this, R.array.typegroup, android.R.layout.simple_spinner_item);
        typeSpinner.setAdapter(spinnerAdap);

    }

    @Override
    protected void onResume() {
        super.onResume();
        intent = getIntent();
        updateIdx = intent.getIntExtra("updateIndex", 0);
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<TimelineVO> callt = apiConfig.selectOnetimeline(intent.getIntExtra("updateIndex", 0));
        callt.enqueue(new Callback<TimelineVO>() {
            @Override
            public void onResponse(Call<TimelineVO> call, Response<TimelineVO> response) {
                timelineVO = response.body();
                Glide.with(getApplicationContext()).load("http://13.124.12.50" + timelineVO.getWriteprofile()).into(myProfileImg);
                if(timelineVO.getWriteImg().equals("")) {
                    //  게시글 올릴때 이미지 안올렸으면
                    writeimgNull.setVisibility(View.VISIBLE);
                }else {
                    Glide.with(getApplicationContext()).load("http://13.124.12.50" + timelineVO.getWriteImg()).into(updateImg);
                }
                myIdTv.setText(timelineVO.getWriteid());
                updateContent.setText(timelineVO.getWritecontent());

                /* 글내용 받아오고 edittext 포커스 내용 다음으로 주기 */
                Editable contentText = updateContent.getText();
                Selection.setSelection(contentText, contentText.length());

                /* 스피너에 글 타입 연결하기 */
                for(int i=0 ; i<typeArray.length ; i++) {
                    if(timelineVO.getWritetype().equals(typeArray[i])) {
                        typeSpinner.setSelection(i);
                        break;
                    }
                }
            }

            @Override
            public void onFailure(Call<TimelineVO> call, Throwable t) {

            }
        });
    }

    /* 수정 완료, 취소 버튼 클릭 이벤트 */
    public void updateNcancel(View view) {
        int id = view.getId();
        switch (id) {
            //  수정 완료
            case R.id.updateOkBtn:
                Log.d("인덱스", "" + updateIdx);
                /* DB에 업데이트 */
                ApiConfig apiConfig = RetrofitCreator.getapiConfig();

                //  전송할 데이터 맵에 넣기
                Map<String, RequestBody> map = new HashMap<>();
                map.put("updateIdx", toRequestBody(String.valueOf(updateIdx)));
                map.put("writetype", toRequestBody(typeSpinner.getSelectedItem().toString()));
                map.put("writecontent", toRequestBody(updateContent.getText().toString()));

                //  이미지 수정하거나 올렸을때만 멀티파트로 전송
                if(!imageFileName.equals("")) {
                    File filepath = new File(photoPath);
                    RequestBody uploadfile = RequestBody.create(MediaType.parse("image/*"), filepath);
                    map.put("uploaded_file\"; filename=\"" + imageFileName + "\"", uploadfile);
                }

                Call<ServerResponse> updateCall = apiConfig.updateOnetimeline(map);
                updateCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        Log.d("글 수정", response.body().getMessage());
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {
                        Log.d("글 수정 실패", t.getMessage());
                    }
                });
                Toast.makeText(getApplicationContext(), "글이 수정되었습니다", Toast.LENGTH_SHORT).show();
                finish();
                break;

            //  수정 취소
            case R.id.cancelUpdate:
                DialogInterface.OnClickListener cancelOk = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
                DialogInterface.OnClickListener nocancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("저장되지 않은 변경 사항이 있습니다.\n취소할까요?")
                        .setNegativeButton("아니오", nocancel)
                        .setPositiveButton("예", cancelOk)
                        .show();
                break;
        }
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    /* 이미지 올리기 버튼 클릭 이벤트 */
    public void setUpdateImg(View view) {
        writeimgNull.setVisibility(View.INVISIBLE);
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
                writeimgNull.setVisibility(View.VISIBLE);
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
                        updateImg.setImageURI(cameraUri);
                    }else{
                        //  앨범에서 사진을 가져왔을 경우 해당 uri에서 경로를 얻어와 이미지뷰에 넣는다
                        updateImg.setImageURI(albumUri);
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
