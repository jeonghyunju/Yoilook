package com.example.hyunjujung.yoil;

import android.app.DatePickerDialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.apdater.DailyCategoryAdapter;
import com.example.hyunjujung.yoil.apdater.DailyTagAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.DailyCategoryVO;
import com.example.hyunjujung.yoil.vo.DailySharedVO;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsertDailyCodi extends AppCompatActivity {
    static final int pickAlbum = 100;
    static final int pickMultiAlbum = 101;

    /* 메인 이미지 절대 경로 저장 변수 */
    static String albumPath = "";

    /* 추가된 이미지 uri 저장된 array */
    ArrayList<Uri> array = new ArrayList<>();

    /* 추가된 이미지 절대경로 저장된 array */
    ArrayList<String> absolutePath = new ArrayList<>();

    /* 서버에 저장할 uri 리스트 */
    ArrayList<Uri> uriList = new ArrayList<>();

    /* 추가된 이미지 DB에 저장할 array (서버경로 저장) */
    static ArrayList<String> subimageArray = new ArrayList<>();

    ImageView dailyMainImg;
    TextView addImgTv, dateTv, addSubimg;
    EditText tagEt;

    Intent intent;

    /* 태그 추가 리사이클러뷰 */
    RecyclerView tagRecycle;
    RecyclerView.LayoutManager tagLayout;
    DailyTagAdapter tagAdapter;
    ArrayList<String> tagArray = new ArrayList<>();

    /* 이미지 추가 리사이클러뷰 */
    RecyclerView categoryRecycle;
    RecyclerView.LayoutManager categoryLayout;
    DailyCategoryAdapter categoryAdapter;

    /* 다중 이미지 저장 arraylist */
    ArrayList<DailyCategoryVO> categoryList = new ArrayList<>();

    /* 데이트 피커 날짜 저장 변수 */
    static int uYear, uMonth, uday;
    static String weekdayS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_daily_codi);

        dailyMainImg = (ImageView)findViewById(R.id.dailyMainImg);
        addImgTv = (TextView)findViewById(R.id.addImgTv);
        dateTv = (TextView)findViewById(R.id.dateTv);
        addSubimg = (TextView)findViewById(R.id.addSubimg);
        tagEt = (EditText)findViewById(R.id.tagEt);

        categoryRecycle = (RecyclerView)findViewById(R.id.categoryRecycle);
        tagRecycle = (RecyclerView)findViewById(R.id.tagRecycle);

        /* 데이트 피커 오늘 날짜 세팅 */
        Calendar cal = Calendar.getInstance();
        uYear = cal.get(Calendar.YEAR);
        uMonth = cal.get(Calendar.MONTH);
        uday = cal.get(Calendar.DAY_OF_MONTH);

        categoryLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecycle.setLayoutManager(categoryLayout);

        tagLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tagRecycle.setLayoutManager(tagLayout);

        /* 임시 저장된 데이터 있으면 임시 저장 데이터 출력한다 */
        SharedPreferences tempDaily = getSharedPreferences("tempSave", MODE_PRIVATE);
        if(tempDaily.getString("tempDaily", null) != null) {
            String dailyTemp = tempDaily.getString("tempDaily", null);
            Gson gson = new Gson();
            DailySharedVO dailySharedVO = gson.fromJson(dailyTemp, DailySharedVO.class);
            ArrayList<String> tagsList = dailySharedVO.getTagArray();
            ArrayList<String> absolArray = dailySharedVO.getAbsoluteArray();
            if(tagsList != null && tagsList.size() > 0) {
                for(String tags : tagsList) {
                    tagArray.add(tags);
                }
                tagAdapter = new DailyTagAdapter(this, tagArray, true);
                tagRecycle.setAdapter(tagAdapter);
            }
            if(absolArray != null && absolArray.size() > 0) {
                absolutePath.clear();
                for(int i=0 ; i<absolArray.size() ; i++) {
                    categoryList.add(new DailyCategoryVO(Uri.parse(absolArray.get(i))));
                    absolutePath.add(absolArray.get(i));
                }
                categoryAdapter = new DailyCategoryAdapter(this, categoryList, absolutePath);
                categoryRecycle.setAdapter(categoryAdapter);
            }
            if(!dailySharedVO.getAlbumPath().equals("")) {
                Glide.with(this).load(dailySharedVO.getAlbumPath()).into(dailyMainImg);
                addImgTv.setText("");
            }
            if(!dailySharedVO.getDateTV().equals("")) {
                dateTv.setText(dailySharedVO.getDateTV());
            }
            Log.d("임시 저장", dailySharedVO.getAlbumPath() + ", " + dailySharedVO.getDateTV() + ", " + dailySharedVO.getTagArray().toString() + ", " + dailySharedVO.getAbsoluteArray().toString());
        }

    }

    /* 메인 사진 추가 */
    public void pickImage(View view) {
        intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, pickAlbum);
    }

    /* 서브 이미지 추가
     * 이미지 추가 하기 누르면 갤러리에서 이미지 다중 선택 하고 선택한 이미지 Recyclerview 에 넣어서 출력한다 */
    public void addSubImage(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent multiIntent = new Intent("android.intent.action.MULTIPLE_PICK");
            multiIntent.setType("image/*");

            /* 삼성 폰일때는 아래와 같이 packageManager에서 확인하고 startActivity 해야한다 */
            PackageManager packageManager = getApplicationContext().getPackageManager();
            List<ResolveInfo> infos = packageManager.queryIntentActivities(multiIntent, 0);
            if(infos.size() > 0) {
                startActivityForResult(Intent.createChooser(multiIntent, "사진은 10개까지 선택가능합니다"), pickMultiAlbum);
            }

        }else {
            Log.d("kitkat under", "..");
        }
    }

    /* dailycodi 추가 완료, 취소 및 데이트 피커 클릭 이벤트 */
    public void clickBtn(View view) {
        int id = view.getId();
        switch (id) {
            /* 추가 완료
             * DB에 넣기 */
            case R.id.dailycodiAdd:
                if(tagArray.size() == 0) {
                    Toast.makeText(this, "태그를 추가해주세요", Toast.LENGTH_SHORT).show();
                }else if(albumPath.equals("")) {
                    Toast.makeText(this, "메인 사진을 추가해주세요", Toast.LENGTH_SHORT).show();
                }else if(dateTv.getText().toString().equals("DATE")) {
                    Toast.makeText(this, "날짜를 설정해주세요", Toast.LENGTH_SHORT).show();
                }else {
                    SharedPreferences tempSave = getSharedPreferences("tempSave", MODE_PRIVATE);
                    //  DB에 저장하고 이동
                    ApiConfig apiConfig = RetrofitCreator.getapiConfig();

                    //  다중 이미지 전송
                    List<MultipartBody.Part> fileparts = new ArrayList<>();
                    if(array.size() != 0) {
                        for(String path : absolutePath) {
                            fileparts.add(prepareFile("attachment[]", path));
                        }
                    }

                    /* 타임피커에서 선택한 날짜에 해당하는 요일 */
                    weekdayS = setDayofWeek(uYear, uMonth, uday);

                    String ymd = "" + uYear + "" + (uMonth+1) + "" + uday;

                    Log.d("어펜드 시키기", subimgAppend(subimageArray));

                    //  사진 외에 데이터 보내기
                    Map<String, RequestBody> maps = new HashMap<>();
                    maps.put("year", toRequestBody(String.valueOf(uYear)));
                    maps.put("month", toRequestBody(String.valueOf(uMonth + 1)));
                    maps.put("days", toRequestBody(String.valueOf(uday)));
                    maps.put("weekday", toRequestBody(weekdayS));
                    maps.put("tags", toRequestBody(tagAppend(tagArray)));
                    maps.put("subimg", toRequestBody(subimgAppend(subimageArray)));
                    maps.put("ymd", toRequestBody(ymd));

                    //  메인 사진 map에 put
                    File mainfile = new File(albumPath);
                    RequestBody mainBody = RequestBody.create(MediaType.parse("image/*"), mainfile);
                    Log.d("파일 네임", albumPath + ", " + mainfile.getName());
                    maps.put("main_file\"; filename=\"" + mainfile.getName() + "\"", mainBody);

                    Call<ServerResponse> calls = apiConfig.insertDailyCodi(maps, fileparts);
                    calls.enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            ServerResponse serverResponse = response.body();
                            Log.d("데일리 코디 등록", serverResponse.getMessage());
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            Log.d("데일리 코디 등록 실패", t.getMessage());
                        }
                    });
                    Log.d("지우고 난 다음에 어레이 개수", "" + absolutePath.size() + ", " + categoryList.size());
                    Toast.makeText(this, "Daily Codi 를 등록하였습니다", Toast.LENGTH_SHORT).show();
                    array.clear();
                    absolutePath.clear();
                    subimageArray.clear();
                    tempSave.edit().clear().commit();
                    finish();
                }

                break;

            /* 추가 취소 */
            case R.id.addDailyCancel:
                if(!albumPath.equals("") || !dateTv.getText().toString().equals("DATE") || tagArray.size() != 0 || absolutePath.size() != 0) {
                    //  작성된 사항이 있을 땐 알림창을 띄운다
                    DialogInterface.OnClickListener cancelOk = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences tempDaily = getSharedPreferences("tempSave", MODE_PRIVATE);
                            if(tempDaily.getString("tempDaily", null) != null) {
                                tempDaily.edit().clear().commit();
                            }
                            absolutePath.clear();
                            array.clear();
                            tagArray.clear();
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
                            .setMessage("저장되지 않은 변경 사항이 있습니다.\n작성을 취소할까요?")
                            .setNegativeButton("아니오", nocancel)
                            .setPositiveButton("예", cancelOk)
                            .show();
                }else {
                    //  작성된 사항이 없을 땐 그냥 종료
                    finish();
                }
                break;

            /* 데이트 피커 */
            case R.id.dateTv:
                new DatePickerDialog(InsertDailyCodi.this, dateSetListener, uYear, uMonth, uday).show();
                break;

            /* 태그 추가하기 */
            case R.id.addTagBtn:
                if(tagEt.getText().toString().equals("")) {
                    Toast.makeText(this, "태그를 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    /* 태그 array 에 작성한 태그를 추가한다 */
                    tagArray.add("#" + tagEt.getText().toString());
                    tagAdapter = new DailyTagAdapter(this, tagArray, true);
                    tagRecycle.setAdapter(tagAdapter);
                    Log.d("태그 어레이 개수", "" + tagArray.size());
                    tagEt.setText("");
                }
                break;
        }
    }

    /* 데이트 피커 다이얼로그 */
    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            uYear = year;
            uMonth = month;
            uday = day;
            dateTv.setText(String.format("%d년 %d월 %d일", uYear, (uMonth + 1), uday));
        }
    };

    /* 서브 이미지 경로 모두 붙이기 */
    public String subimgAppend(ArrayList<String> subimgpath) {
        String append = "";
        for(int i=0 ; i<subimgpath.size() ; i++) {
            if(i == subimgpath.size() - 1) {
                append += subimgpath.get(i);
            }else {
                append += subimgpath.get(i) + "-";
            }
        }
        return append;
    }

    /* 태그 모두 붙이기 */
    public String tagAppend(ArrayList<String> tagList) {
        String tagAppend = "";
        for(int i=0 ; i<tagList.size() ; i++) {
            if(i == tagList.size() - 1) {
                tagAppend += tagList.get(i);
            }else {
                tagAppend += tagList.get(i) + "-";
            }
        }
        return tagAppend;
    }

    /* 선택한 날짜로 선택된 요일 구하기 */
    public String setDayofWeek(int years, int months, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, years);
        calendar.set(Calendar.MONTH, months);
        calendar.set(Calendar.DATE, days);
        String weeks = "";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                weeks = "일";
                break;
            case 2:
                weeks = "월";
                break;
            case 3:
                weeks = "화";
                break;
            case 4:
                weeks = "수";
                break;
            case 5:
                weeks = "목";
                break;
            case 6:
                weeks = "금";
                break;
            case 7:
                weeks = "토";
                break;
        }
        return weeks;
    }

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }

    /* 파일 경로를 통해 multipartbody.part 전송할 준비 */
    private MultipartBody.Part prepareFile(String partName, String path) {
        File filePath = new File(path);
        RequestBody filebody = RequestBody.create(MediaType.parse("image/*"), filePath);
        subimageArray.add(filePath.getName());
        return MultipartBody.Part.createFormData(partName, filePath.getName(), filebody);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                /* 갤러리에서 사진 가져오기 */
                case pickAlbum:
                    SendPicture(data);
                    addImgTv.setVisibility(View.INVISIBLE);
                    break;

                /* 갤러리 이미지 다중 선택 */
                case pickMultiAlbum:
                    /* 삼성폰일때는 getClipData로 다중 선택 된 이미지를 받을 수 없음
                     * 따라서 아래처럼 bundle로 받아야한다 */
                    Bundle extras = data.getExtras();
                    int count = extras.getInt("selectedCount");
                    /* 삼성폰에서 다중 선택 된 이미지 uri 받기 */
                    Object items = extras.getStringArrayList("selectedItems");
                    Log.d("삼성폰", items.toString());
                    array = (ArrayList)items;
                    for(int i=0 ; i<array.size() ; i++) {
                        categoryList.add(new DailyCategoryVO(array.get(i)));

                        /* uri로 파일의 절대경로를 구하고 arraylist에 저장 */
                        absolutePath.add(getRealpathFromUri(array.get(i)));
                        Log.d("절대 경로", getRealpathFromUri(array.get(i)));
                    }
                    /* 받은 uri를 recyclerview에 넣어서 출력한다 */
                    categoryAdapter = new DailyCategoryAdapter(this, categoryList, absolutePath);
                    categoryRecycle.setAdapter(categoryAdapter);
                    Log.d("서브 이미지 경로 개수", "" + categoryList.size() + ", " + absolutePath.size());
                    break;
            }
        }
    }

    /* 갤러리에서 가져온 사진 이미지뷰에 출력 */
    private void SendPicture (Intent data) {
        Uri imgUri = data.getData();
        dailyMainImg.setImageURI(imgUri);
        albumPath = getRealpathFromUri(imgUri);
        Log.d("앨범 이미지 경로", albumPath);
    }

    /* 갤러리에서 가져온 이미지의 URI로 절대 경로 구하는 메소드 */
    public String getRealpathFromUri(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int columIdx = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columIdx);
    }

    /* 백버튼 눌렀을때 임시저장하기 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!albumPath.equals("") || !dateTv.getText().toString().equals("DATE") || tagArray.size() != 0 || absolutePath.size() != 0) {
            //  작성된 사항이 있을때 shared에 저장
            Gson gson = new Gson();
            DailySharedVO dailySharedVO = new DailySharedVO(albumPath, dateTv.getText().toString(), tagArray, absolutePath);
            SharedPreferences dailyShared = getSharedPreferences("tempSave", MODE_PRIVATE);
            dailyShared.edit().putString("tempDaily", gson.toJson(dailySharedVO)).commit();
            Toast.makeText(this, "임시 저장 되었습니다", Toast.LENGTH_SHORT).show();
        }else {
            //  작성된 사항이 없을땐 그냥 종료
            finish();
        }
    }
}
