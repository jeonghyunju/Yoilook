package com.example.hyunjujung.yoil;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.SelectVO;

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

public class UpdateUser extends AppCompatActivity {
    CircleImageView nowUserimg;
    EditText nameEt, idEt, emailEt, phoneEt;
    TextView changeArea;
    RadioGroup changeGen;
    RadioButton changeFemale, changeMale;

    Intent intent;

    //  로그인 아이디 저장 변수
    static String loginid = "";

    //  지역 저장 변수
    static String area = "";

    //  기존 프로필 경로
    static String nowUserProfile = "";

    static final int changeAreaGoogleMap = 0;
    static final int pickCamera = 1;
    static final int pickAlbum = 2;
    static final int cropImage = 3;

    //  카메라로 찍어서 크롭한 uri와 앨범에서 가져와서 크롭한 uri
    Uri cameraUri, albumUri;

    //  이미지의 현재 경로
    static String photoPath;

    //  카메라로 찍어서 크롭한 경로와 앨범에서 가져와서 크롭한 경로를 다르게 지정해주기 위해 선언해주는 변수
    Boolean album = false;

    static String imageFileName = "";

    //  변경된 프로필 이미지 경로
    static String updateImg = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_user);
        nowUserimg = (CircleImageView)findViewById(R.id.nowUserimg);
        nameEt = (EditText)findViewById(R.id.nameEt);
        idEt = (EditText)findViewById(R.id.idEt);
        emailEt = (EditText)findViewById(R.id.emailEt);
        phoneEt = (EditText)findViewById(R.id.phoneEt);
        changeArea = (TextView)findViewById(R.id.changeArea);
        changeGen = (RadioGroup)findViewById(R.id.changeGen);
        changeFemale = (RadioButton)findViewById(R.id.changeFemale);
        changeMale = (RadioButton)findViewById(R.id.changeMale);

        SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
        if(autoLogin.getString("autoId", null) != null) {
            //  자동로그인일 때
            loginid = autoLogin.getString("autoId", null);
        }else {
            //  자동로그인 아닐 때
            SharedPreferences noAutologin = getSharedPreferences("noAuto", MODE_PRIVATE);
            loginid = noAutologin.getString("noAutoid", null);
        }
        idEt.setText(loginid);

        /* Retrofit 사용해서 로그인 된 아이디 정보 모두 받기 */
        ApiConfig apiConfig = RetrofitCreator.getapiConfig();
        Call<SelectVO> call = apiConfig.selectmembers(loginid);
        call.enqueue(new Callback<SelectVO>() {
            @Override
            public void onResponse(Call<SelectVO> call, Response<SelectVO> response) {
                SelectVO selectVO = response.body();
                Glide.with(getApplicationContext()).load("http://13.124.12.50" + selectVO.getProfile()).into(nowUserimg);
                nameEt.setText(selectVO.getUsername());
                emailEt.setText(selectVO.getUseremail());
                phoneEt.setText(selectVO.getUserphone());
                if(selectVO.getUserarea().equals("")) {
                    changeArea.setText("지역 설정                                   +");
                }else {
                    changeArea.setText(selectVO.getUserarea());
                    area = selectVO.getUserarea().substring(1);
                }
                if(selectVO.getUsergender().equals("Female")) {
                    changeFemale.setChecked(true);
                    changeMale.setChecked(false);
                }else if(selectVO.getUsergender().equals("Male")) {
                    changeFemale.setChecked(false);
                    changeMale.setChecked(true);
                }
                nowUserProfile = selectVO.getProfile();
            }

            @Override
            public void onFailure(Call<SelectVO> call, Throwable t) {

            }
        });
    }

    //  프로필 사진 바꾸기
    public void changeProfile(View view) {
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

    //  지역 변경하기
    //  지역이 설정되어 있으면 설정되어 있는 지역명 전달
    public void areaChangeBtn(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.changeArea:
                intent = new Intent(getApplicationContext(), Addarea_googleMap.class);
                intent.putExtra("changeArea", area);
                startActivityForResult(intent, changeAreaGoogleMap);
                break;
        }
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    //  변경 완료, 취소
    public void updateUser(View view) {
        int id = view.getId();
        switch (id) {
            //  변경 완료
            case R.id.changeInfoOk:
                RadioButton selectGender = (RadioButton)findViewById(changeGen.getCheckedRadioButtonId());
                String changeRadioGender = selectGender.getText().toString();
                Log.d("으아",  "ㅎㅎ" + imageFileName);

                /* Retrofit 사용 */
                ApiConfig apicon = RetrofitCreator.getapiConfig();

                //  변경된 데이터 전달
                Map<String, RequestBody> updateUsers = new HashMap<>();

                updateUsers.put("username", toRequestBody(nameEt.getText().toString()));
                updateUsers.put("useremail", toRequestBody(emailEt.getText().toString()));
                updateUsers.put("userphone", toRequestBody(phoneEt.getText().toString()));
                updateUsers.put("usergender", toRequestBody(changeRadioGender));
                if(changeArea.getText().toString().contains("지역 설정")) {
                    changeArea.setText("");
                }
                updateUsers.put("userarea", toRequestBody(changeArea.getText().toString()));
                updateUsers.put("userid", toRequestBody(loginid));
                if(!imageFileName.equals("")) {
                    //  프로필 변경 했을 때만 멀티파트로 이미지 전송
                    File filepath = new File(photoPath);
                    RequestBody uploadFile = RequestBody.create(MediaType.parse("image/*"), filepath);
                    updateUsers.put("uploaded_file\"; filename=\"" + imageFileName + "\"", uploadFile);
                }

                /* DB 업데이트 */
                Call<ServerResponse> updateCall = apicon.updatemembers(updateUsers);
                updateCall.enqueue(new Callback<ServerResponse>() {
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                        ServerResponse s = response.body();
                        if(!s.getMessage().contains("프로필")) {
                            //  프로필 변경 했을 경우에만 shared에 있는 프로필 이미지 변경한다
                            SharedPreferences autoLogin = getSharedPreferences("auto", MODE_PRIVATE);
                            if(autoLogin.getString("autoId", null) != null) {
                                //  자동 로그인 일때
                                SharedPreferences.Editor autoEdit = autoLogin.edit();
                                autoEdit.putString("autoImg", s.getMessage());
                                autoEdit.commit();
                            }else {
                                //  자동 로그인 아닐때
                                SharedPreferences noAuto = getSharedPreferences("noAuto", MODE_PRIVATE);
                                SharedPreferences.Editor noAutoEdit = noAuto.edit();
                                noAutoEdit.putString("noAutoImg", s.getMessage());
                                noAutoEdit.commit();
                            }
                        }
                        Log.d("성공", s.getMessage());
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t) {

                    }
                });

                intent = new Intent(getApplicationContext(), MyInfo.class);
                startActivity(intent);
                break;

            //  변경 취소
            case R.id.changeCancel:
                DialogInterface.OnClickListener cancelOk = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        intent = new Intent(getApplicationContext(), MyInfo.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            Log.d("정보 변경", "변경 안됐음");
        }else {
            switch (requestCode) {
                //  지역 변경
                case changeAreaGoogleMap:
                    if(!data.getStringExtra("searcharea").equals("")) {
                        changeArea.setText("#" + data.getStringExtra("searcharea"));
                    }
                    break;

                case pickAlbum:
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
                    album = false;
                    cropimage();
                    break;

                case cropImage:
                    galleryAddPic();
                    if(album == false) {
                        //  카메라로 사진찍었을 경우 해당 uri에서 경로를 얻어와 이미지뷰에 넣는다
                        nowUserimg.setImageURI(cameraUri);
                    }else{
                        //  앨범에서 사진을 가져왔을 경우 해당 uri에서 경로를 얻어와 이미지뷰에 넣는다
                        nowUserimg.setImageURI(albumUri);
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
                    //  getUriForFile의 두번째 인자는 Manifest 의 provider 에서 authorities 의 경로와 같아야한다
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
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "yoilook");
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
        cropIntent.putExtra("outputX", 80);
        cropIntent.putExtra("outputY", 80);
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
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
        Toast.makeText(this, "프로필 사진이 등록되었습니다", Toast.LENGTH_LONG).show();
        //Log.d("절대 경로 : ", photoPath);
    }
}
