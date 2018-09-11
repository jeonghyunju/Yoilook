package com.example.hyunjujung.yoil;

import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hyunjujung.yoil.apdater.DailyTagAdapter;
import com.example.hyunjujung.yoil.apdater.DailyUpdateAdapter;
import com.example.hyunjujung.yoil.apis.ApiConfig;
import com.example.hyunjujung.yoil.apis.RetrofitCreator;
import com.example.hyunjujung.yoil.vo.DailyCodiVO;

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

public class UpdateDailyCodi extends AppCompatActivity {
    ImageView dailyMainImg;
    TextView dateTv;
    EditText tagEt;

    static final int pickAlbum = 0;
    static final int pickMultiAlbum = 1;
    static String albumPath, weekday = "";

    /* 이미지 다중 선택 시에 uri 넣을 리스트 */
    ArrayList<Uri> uriArrays = new ArrayList<>();

    /* 서브이미지 출력할 adapter에 넘겨줄 파일명 리스트 */
    ArrayList<String> uriNfileName = new ArrayList<>();

    /* 태그 리사이클러뷰 */
    RecyclerView tagRecycle;
    RecyclerView.LayoutManager tagLayout;
    DailyTagAdapter tagAdapter;
    ArrayList<String> tagArray = new ArrayList<>();

    /* 서브이미지 리사이클러뷰 */
    RecyclerView categoryRecycle;
    RecyclerView.LayoutManager categoryLayout;
    DailyUpdateAdapter categoryAdapter;
    ArrayList<String> subimgArray = new ArrayList<>();

    Intent intent;

    static DailyCodiVO dailyVo;

    /* 서버에 보내서 수정할 글번호 */
    int idx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_daily_codi);

        dailyMainImg = (ImageView)findViewById(R.id.dailyMainImg);
        dateTv = (TextView)findViewById(R.id.dateTv);
        tagEt = (EditText)findViewById(R.id.tagEt);
        tagRecycle = (RecyclerView)findViewById(R.id.tagRecycle);
        categoryRecycle = (RecyclerView)findViewById(R.id.categoryRecycle);

        tagLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        tagRecycle.setLayoutManager(tagLayout);

        categoryLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecycle.setLayoutManager(categoryLayout);

        intent = getIntent();
        String ymd = intent.getStringExtra("todayDate");

        /* 수정할 데이터 받아오기 */
        ApiConfig apicon = RetrofitCreator.getapiConfig();
        Call<DailyCodiVO> calls = apicon.selectDailycodi(ymd);
        calls.enqueue(new Callback<DailyCodiVO>() {
            @Override
            public void onResponse(Call<DailyCodiVO> call, Response<DailyCodiVO> response) {
                dailyVo = response.body();
                String[] subImages = dailyVo.getSubimage().split("-");
                String[] tags = dailyVo.getTag().split("-");

                /* 서브이미지 경로들을 받아와서 arraylist 에 넣어준다 */
                for(int i=0 ; i<subImages.length ; i++) {
                    subimgArray.add(subImages[i]);
                    uriNfileName.add("*" + subImages[i]);
                }
                categoryAdapter = new DailyUpdateAdapter(getApplicationContext(), uriNfileName, subimgArray);
                categoryRecycle.setAdapter(categoryAdapter);

                /* 태그를 모두 받아와서 arraylist 에 넣어준다 */
                for(int i=0 ; i<tags.length ; i++) {
                    tagArray.add(tags[i]);
                }
                tagAdapter = new DailyTagAdapter(getApplicationContext(), tagArray, true);
                tagRecycle.setAdapter(tagAdapter);

                /* 메인 이미지 출력 */
                Glide.with(getApplicationContext()).load("http://13.124.12.50" + dailyVo.getMainimage()).into(dailyMainImg);
                idx = dailyVo.getIdx();
                dateTv.setText(dailyVo.getYear() + "년 " + dailyVo.getMonth() + "월 " + dailyVo.getDay() + "일");
            }

            @Override
            public void onFailure(Call<DailyCodiVO> call, Throwable t) {

            }
        });
    }

    /* 메인 사진 변경 */
    public void pickImage(View view) {
        intent = new Intent(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, pickAlbum);
    }

    /* 서브 이미지 변경
     * 이미지 추가 하기 누르면 갤러리에서 이미지 다중 선택하고 선택한 이미지 recyclerview 에 넣어서 출력한다 */
    public void addSubImage(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent multiIntent = new Intent("android.intent.action.MULTIPLE_PICK");
            multiIntent.setType("image/*");

            /* 삼성폰 일때는 이미지 다중 선택 시에 다르게 접근해야 한다 */
            PackageManager packageManager = getApplicationContext().getPackageManager();
            List<ResolveInfo> infos = packageManager.queryIntentActivities(multiIntent, 0);
            if(infos.size() > 0) {
                startActivityForResult(Intent.createChooser(multiIntent, "사진은 10개까지 선택 가능합니다"), pickMultiAlbum);
            }
        }
    }

    public void clickBtn(View view) {
        int id = view.getId();
        switch (id) {
            /* 태그 추가하기 */
            case R.id.addTagBtn:
                if(tagEt.getText().toString().equals("")) {
                    Toast.makeText(this, "태그를 입력하세요", Toast.LENGTH_SHORT).show();
                }else {
                    /* 태그 array 에 작성한 태그를 추가한다 */
                    tagArray.add("#" + tagEt.getText().toString());
                    tagAdapter = new DailyTagAdapter(this, tagArray, true);
                    tagRecycle.setAdapter(tagAdapter);
                    tagEt.setText("");
                }
                break;

            /* 수정 완료 */
            case R.id.dailycodiUpdate:
                /* 서브 이미지 수정했을때, 수정 안했을때 나눠서 보내기 */
                ApiConfig apiConfig = RetrofitCreator.getapiConfig();
                Map<String, RequestBody> maps = new HashMap<>();
                maps.put("idx", toRequestBody(String.valueOf(idx)));
                maps.put("tags", toRequestBody(tagAppend(tagArray)));
                maps.put("subimg", toRequestBody(subimgAppend(subimgArray)));

                /* 메인 이미지 변경 했을때 */
                if(albumPath != null) {
                    File mainFile = new File(albumPath);
                    RequestBody uploadMain = RequestBody.create(MediaType.parse("image/*"), mainFile);
                    maps.put("uploaded_file\"; filename=\"" + mainFile.getName() + "\"", uploadMain);
                }

                if(uriArrays.size() > 0) {
                    /* 서브 이미지 추가 했을때 */
                    List<MultipartBody.Part> subFileParts = new ArrayList<>();
                    for(int i=0 ; i<uriArrays.size() ; i++) {
                        subFileParts.add(prepareFile("attachment[]", getRealpathFromUri(uriArrays.get(i))));
                    }
                    Call<ServerResponse> callSub = apiConfig.updateDailyCodi1(maps, subFileParts);
                    callSub.enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            Log.d("daily codi 이미지 추가 수정", response.body().getMessage());
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            Log.d("이미지 추가 수정 실패", t.getMessage());
                        }
                    });
                    Log.d("서브이미지 추가 어레이", subimgArray.toString());

                }else {
                    /* 서브 이미지 추가 안했을때 */
                    Call<ServerResponse> callnoSub = apiConfig.updateDailyCodi2(maps);
                    callnoSub.enqueue(new Callback<ServerResponse>() {
                        @Override
                        public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                            Log.d("daily codi 수정", response.body().getMessage());
                        }

                        @Override
                        public void onFailure(Call<ServerResponse> call, Throwable t) {
                            Log.d("수정 실패", t.getMessage());
                        }
                    });
                    Log.d("서브이미지 노추가, 삭제 어레이", subimgArray.toString());
                }
                Toast.makeText(this, "수정이 완료되었습니다", Toast.LENGTH_SHORT).show();
                finish();
                break;

            /* 수정 취소 */
            case R.id.updateDailyCancel:
                //  작성된 사항이 있을 땐 알림창을 띄운다
                DialogInterface.OnClickListener cancelOk = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                        .setMessage("수정을 취소하시겠습니까?")
                        .setNegativeButton("아니오", nocancel)
                        .setPositiveButton("예", cancelOk)
                        .show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                /* 갤러리에서 메인 사진 가져오기 */
                case pickAlbum:
                    SendPicture(data);
                    break;

                /* 갤러리 이미지 다중 선택 */
                case pickMultiAlbum:
                    /* 삼성폰일때는 getClipData로 다중 선택 된 이미지를 받을 수 없음
                     * 따라서 아래처럼 bundle로 받아야한다 */
                    Bundle extras = data.getExtras();
                    /* 삼성폰에서 다중 선택 된 이미지 uri 받기 */
                    Object items = extras.getStringArrayList("selectedItems");
                    uriArrays = (ArrayList)items;
                    for(int i=0 ; i<uriArrays.size() ; i++) {
                        File fileNames = new File(getRealpathFromUri(uriArrays.get(i)));
                        subimgArray.add(fileNames.getName());
                        uriNfileName.add("#" + uriArrays.get(i));
                    }
                    categoryAdapter = new DailyUpdateAdapter(this, uriNfileName, subimgArray);
                    categoryRecycle.setAdapter(categoryAdapter);
                    Log.d("서브 이미지 이름 리스트", subimgArray.toString());
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

    /* 파일 경로를 통해 multipartbody.part 전송할 준비 */
    private MultipartBody.Part prepareFile(String partName, String path) {
        File filePath = new File(path);
        RequestBody filebody = RequestBody.create(MediaType.parse("image/*"), filePath);
        return MultipartBody.Part.createFormData(partName, filePath.getName(), filebody);
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

    public static RequestBody toRequestBody(String value) {
        RequestBody body = RequestBody.create(MediaType.parse("text/plain"), value);
        return body;
    }
}
