package com.example.hyunjujung.yoil;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Callback;
import retrofit2.Response;

public class Join extends AppCompatActivity {
    static final int pickCamera = 0;
    static final int pickAlbum = 1;
    static final int cropImage = 2;
    static final int addAreaGoogleMap = 3;
    static final int ChangeFilter = 4;
    CircleImageView profileImg;
    EditText userName, userId, userPw, confirmPw, userPhone, userEmail;
    TextView addArea, checkidTv, checkpwTv, validatepwTv;
    RadioGroup gendergroup;
    Spinner ageSpinner;

    //  카메라로 찍어서 크롭한 uri와 앨범에서 가져와서 크롭한 uri
    Uri cameraUri, albumUri;

    //  이미지의 현재 경로
    static String photoPath;

    //  카메라로 찍어서 크롭한 경로와 앨범에서 가져와서 크롭한 경로를 다르게 지정해주기 위해 선언해주는 변수
    Boolean album = false;

    static String imageFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);
        userName = (EditText)findViewById(R.id.userName);
        userId = (EditText)findViewById(R.id.userId);
        userPw = (EditText)findViewById(R.id.userPw);
        confirmPw = (EditText)findViewById(R.id.confirmPw);
        userPhone = (EditText)findViewById(R.id.userPhone);
        userEmail = (EditText)findViewById(R.id.userEmail);
        addArea = (TextView)findViewById(R.id.addArea);
        checkidTv = (TextView)findViewById(R.id.checkidTv);
        checkpwTv = (TextView)findViewById(R.id.checkpwTv);
        validatepwTv = (TextView)findViewById(R.id.validatepwTv);
        gendergroup = (RadioGroup)findViewById(R.id.gendergroup);

        profileImg = (CircleImageView) findViewById(R.id.profileImg);

        /* 연령대 스피너 */
        ageSpinner = (Spinner)findViewById(R.id.ageSpinner);
        //  어댑터 만들기
        ArrayAdapter<CharSequence> spinnerAdap =  ArrayAdapter.createFromResource(this, R.array.agegroup, android.R.layout.simple_spinner_item);
        spinnerAdap.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //  어댑터랑 스피너랑 연결
        ageSpinner.setAdapter(spinnerAdap);

        //  아이디 중복 체크
        userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(final CharSequence charSequence, int i, int i1, int i2) {
                ApiConfig apicon = RetrofitCreator.getapiConfig();
                //RequestBody.create(MediaType.parse("text/plain"), editable.toString());
                retrofit2.Call<SelectVO> call = apicon.selectmembers(charSequence.toString());
                call.enqueue(new Callback<SelectVO>() {
                    @Override
                    public void onResponse(retrofit2.Call<SelectVO> call, Response<SelectVO> response) {
                        SelectVO selectVO = response.body();
                        if(selectVO.getUserid() != null) {
                            checkidTv.setText("* 존재하는 아이디입니다");
                            checkidTv.setTextColor(Color.RED);
                        }else if(charSequence.toString().equals("")) {
                            checkidTv.setText("");
                        }else {
                            checkidTv.setText("사용할 수 있는 아이디입니다");
                            checkidTv.setTextColor(Color.GREEN);
                            Log.d("아이디", "아이디 없음!");
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<SelectVO> call, Throwable t) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //  비밀번호 확인
        confirmPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().equals(userPw.getText().toString())){
                    checkpwTv.setText("");
                }else if(editable.toString().equals("")) {
                    checkpwTv.setText("");
                }else {
                    checkpwTv.setText("* 비밀번호가 일치하지 않습니다");
                    checkpwTv.setTextColor(Color.RED);
                }
            }
        });

        //  비밀번호 유효성 체크
        userPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!Pattern.matches("^[a-zA-Z0-9].{6,12}$", editable) && !editable.toString().equals("")) {
                    validatepwTv.setText("* 영문, 숫자 조합으로 6~12자리 사용");
                    validatepwTv.setTextColor(Color.RED);
                }else if(editable.toString().equals("")) {
                    validatepwTv.setText("");
                }else {
                    validatepwTv.setText("");
                }
            }
        });

    }

    //  프로필 이미지 설정하기
    //  앨범에서 가져오거나
    //  사진찍어서 크롭하거나
   public void  setprofileImg(View view) {
        int id = view.getId();
        //checkPermission();
        switch (id) {
            case R.id.profileImg:
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
                break;
        }

    }

    //  회원가입, 취소 버튼 클릭 이벤트
    public void joinAndcancel(View v) {
        int id = v.getId();
        switch (id){
            //  회원가입
            //  1. 빈칸 체크
            //  2. 아이디 중복체크, 이메일 유효성 검사, 패스워드 일치 확인
            //  3. 1,2가 끝났으면 DB에 회원가입 정보 저장하고 로그인 화면으로 돌아간다
            case R.id.joinokBtn:
                String username = userName.getText().toString();
                String userid = userId.getText().toString();
                String userpass = userPw.getText().toString();
                String confirmpass = confirmPw.getText().toString();
                String userphone = userPhone.getText().toString();
                String usermail = userEmail.getText().toString();
                String areaAdd = addArea.getText().toString();
                //  이메일 유효성 체크 변수
                boolean isEmailCheck = checkEmail(usermail);

                if(username.equals("") || userid.equals("") || userpass.equals("") || confirmpass.equals("") || userphone.equals("") || usermail.equals("")) {
                    //  빈칸 체크
                    Toast.makeText(this, "빈 칸을 모두 채워주세요", Toast.LENGTH_SHORT).show();
                }else if(checkidTv.getText().toString().contains("존재하는")){
                    //  아이디 중복 체크
                    Toast.makeText(this, "중복된 아이디입니다", Toast.LENGTH_SHORT).show();
                }else if(!userPw.getText().toString().equals(confirmPw.getText().toString())) {
                    //  비밀번호 확인
                    Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                }else if(validatepwTv.getText().toString().contains("영문")) {
                    //  비밀번호 유효성 검사
                    Toast.makeText(this, "비밀번호 형식을 확인하세요", Toast.LENGTH_SHORT).show();
                }else {
                    if(!userpass.equals(confirmpass)) {
                        //  패스워드 일치 확인하기
                        confirmPw.setText("");
                        Toast.makeText(this, "비밀번호를 확인해주세요", Toast.LENGTH_SHORT).show();
                    }else if(!isEmailCheck) {
                        Toast.makeText(this, "올바른 Email 형식이 아닙니다\nEmail 형식은 다음과 같습니다\nabc@abc.com", Toast.LENGTH_SHORT).show();
                    }else{
                        //  외부 DB에 회원가입 정보 저장하기
                        //  선택된 gender를 가져온다
                        RadioButton selectGender = (RadioButton)findViewById(gendergroup.getCheckedRadioButtonId());
                        String sGender = selectGender.getText().toString();
                        Log.d("젠더 ", sGender);
                        //  스피너에서 선택된 값을 가져온다
                        String ages = ageSpinner.getSelectedItem().toString();
                        Log.d("연령대 ", ages);
                        if(areaAdd.contains("지역 설정")) {
                            addArea.setText("");
                        }

                        /* Retrofit 사용 !!!!!! */
                        ApiConfig apiConfig = RetrofitCreator.getapiConfig();

                        Map<String, RequestBody> map = new HashMap<>();
                        //  전송할 텍스트 Map에 담기
                        map.put("username", toRequestBody(userName.getText().toString()));
                        map.put("userid", toRequestBody(userId.getText().toString()));
                        map.put("userpass", toRequestBody(userPw.getText().toString()));
                        map.put("userphone", toRequestBody(userPhone.getText().toString()));
                        map.put("useremail", toRequestBody(userEmail.getText().toString()));
                        map.put("usergender", toRequestBody(sGender));
                        map.put("userage", toRequestBody(ages));
                        map.put("userarea", toRequestBody(addArea.getText().toString()));

                        File filepath = new File(photoPath);
                        //  이미지 전송할때
                        RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), filepath);
                        map.put("uploaded_file\"; filename=\"" + imageFileName + "\"", fileBody);

                        retrofit2.Call<ServerResponse> responseCall = apiConfig.saveDataBase(map);
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
                        Toast.makeText(this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                        Intent gologin = new Intent(this, MainActivity.class);
                        gologin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(gologin);
                        finish();
                    }
                }
                break;
            //  회원가입 취소
            //  취소하면 메인화면으로 돌아간다
            case R.id.joincancel:
                Intent goMain = new Intent(this, MainActivity.class);
                goMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(goMain);
                finish();
                break;
        }
    }

    //  구글지도에서 지역 추가하기
    public void addAreaClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.addArea:
                Intent goGoogleMap = new Intent(this, Addarea_googleMap.class);
                startActivityForResult(goGoogleMap, addAreaGoogleMap);
                break;
        }
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
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
    public File createimageFile() throws IOException{
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

    /* 이미지 필터 적용 버튼 */
    public void setfilterImage(View view) {
        Intent filterIntent = new Intent(this, ImageFilterSet.class);
        filterIntent.putExtra("filterImage", photoPath);
        startActivityForResult(filterIntent, ChangeFilter);
    }

    //  가져온 사진을 받는다
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            Log.d("onActivityResult", "RESULT_NOT_OK");
        }else {
            switch (requestCode) {

                //  앨범에서 사진을 가져왔을떄
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

                //  카메라로 사진을 찍을때
                case pickCamera:
                    album = false;
                    //galleryAddPic();
                    cropimage();
                    break;

                //  사진 크롭하여 이미지뷰에 넣기
                case cropImage:
                    galleryAddPic();
                    if(album == false) {
                        //  카메라로 사진찍었을 경우 해당 uri에서 경로를 얻어와 이미지뷰에 넣는다
                        profileImg.setImageURI(cameraUri);
                    }else{
                        //  앨범에서 사진을 가져왔을 경우 해당 uri에서 경로를 얻어와 이미지뷰에 넣는다
                        profileImg.setImageURI(albumUri);
                    }
                    break;

                //  구글맵에서 설정한 지역명 가져오기
                case addAreaGoogleMap:
                    if(!data.getStringExtra("searcharea").equals("")) {
                        addArea.setText("#" + data.getStringExtra("searcharea"));
                    }
                    break;

                //  필터 효과 적용하기
                case ChangeFilter:
                    profileImg.setImageURI(Uri.parse(data.getStringExtra("filterImage")));

                    /* 임시 저장된 파일 경로 가져옴 */
                    photoPath = data.getStringExtra("filterImage");
                    if(data.getStringExtra("fileName") != null) {
                        imageFileName = data.getStringExtra("fileName");
                    }
                    break;
            }
        }
    }

    //  이메일 유효성 검사
    public boolean checkEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}

