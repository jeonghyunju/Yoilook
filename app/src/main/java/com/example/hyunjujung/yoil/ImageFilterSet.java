package com.example.hyunjujung.yoil;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageFilterSet extends AppCompatActivity {

    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    ImageView set_change_filter_Iv,
            original_image,
            first_filter_image,
            second_filter_image,
            third_filter_image;

    Button doneBtn, cancelFilter;

    /* OpenCV */
    private Mat img_input;
    private Mat img_output_Gray, img_output_Embose, img_output_Water;

    Bitmap bitmapOutput_G, bitmapOutput_E, bitmapOutput_W;
    Bitmap saveBitmap;

    Intent intent;

    /* 넘어온 이미지 */
    Uri imageUri;

    /* 바뀐 이미지 저장 */
    String filterImages;
    String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_filter_set);

        set_change_filter_Iv = (ImageView)findViewById(R.id.set_change_filter_Iv);
        original_image = (ImageView)findViewById(R.id.original_image);
        first_filter_image = (ImageView)findViewById(R.id.first_filter_image);
        second_filter_image = (ImageView)findViewById(R.id.second_filter_image);
        third_filter_image = (ImageView)findViewById(R.id.third_filter_image);

        doneBtn = (Button)findViewById(R.id.doneBtn);
        cancelFilter = (Button)findViewById(R.id.cancelFilter);

        intent = getIntent();
        imageUri = Uri.parse(intent.getStringExtra("filterImage"));
        filterImages = intent.getStringExtra("filterImage");
        set_change_filter_Iv.setImageURI(imageUri);

        /* 원본 이미지 */
        original_image.setImageURI(imageUri);

    }

    @Override
    protected void onResume() {
        super.onResume();

        read_image();
        read_water();
        showFilterImage();
    }

    private void read_image() {
        img_input = new Mat();
        img_output_Gray = new Mat();
        img_output_Embose = new Mat();

        loadImage(intent.getStringExtra("filterImage"), img_input.getNativeObjAddr());
    }

    private void showFilterImage() {
        imageprocessing(img_input.getNativeObjAddr(),
                img_output_Gray.getNativeObjAddr(),
                img_output_Embose.getNativeObjAddr());

        waterprocessing(img_input.getNativeObjAddr(), img_output_Water.getNativeObjAddr());

        /* 필터 효과 넣은 이미지를 이미지뷰에 출력 */
        /* Gray */
        bitmapOutput_G = Bitmap.createBitmap(img_output_Gray.cols(), img_output_Gray.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_Gray, bitmapOutput_G);
        first_filter_image.setImageBitmap(bitmapOutput_G);

        /* Embosing */
        bitmapOutput_E = Bitmap.createBitmap(img_output_Embose.cols(), img_output_Embose.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_Embose, bitmapOutput_E);
        second_filter_image.setImageBitmap(bitmapOutput_E);

        /* water */
        bitmapOutput_W = Bitmap.createBitmap(img_output_Water.cols(), img_output_Water.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_output_Water, bitmapOutput_W);
        third_filter_image.setImageBitmap(bitmapOutput_W);
    }

    /* 반전 이미지 효과 주기 */
    private void read_water() {
        img_input = new Mat();
        img_output_Water = new Mat();

        loadImageWater(intent.getStringExtra("filterImage"), img_input.getNativeObjAddr());
    }

    /* 이미지 필터 메소드 (jni) */
    public native void loadImage(String imageFileName,
                                 long img);
    public native void imageprocessing(long inputImage,
                                       long outputImageGray,
                                       long outputImageEmbose);

    /* 반전 이미지 효과 주는 메소드는 따로 구현했음 */
    public native void loadImageWater(String imageFileName, long imgs);
    public native int waterprocessing(long inputimages, long outputwater);

    /* 이미지 필터 적용 버튼 */
    public void changeImage(View view) {
        int id = view.getId();
        switch (id) {
            /* 필터 적용 취소하고 원본 이미지 보이기 */
            case R.id.original_image:
                set_change_filter_Iv.setImageURI(imageUri);
                filterImages = intent.getStringExtra("filterImage");
                saveBitmap = null;
                break;

            /* 첫번째 필터 적용 */
            case R.id.first_filter_image:
                set_change_filter_Iv.setImageBitmap(bitmapOutput_G);
                saveBitmap = bitmapOutput_G;
                break;

            /* 두번째 필터 적용 */
            case R.id.second_filter_image:
                set_change_filter_Iv.setImageBitmap(bitmapOutput_E);
                saveBitmap = bitmapOutput_E;
                break;

            /* 세번째 필터 적용 */
            case R.id.third_filter_image:
                set_change_filter_Iv.setImageBitmap(bitmapOutput_W);
                saveBitmap = bitmapOutput_W;
                break;
        }
    }

    /* 필터 적용 완료 or 취소 */
    public void changeFilterDone(View view) {
        int id = view.getId();
        switch (id) {
            /* 필터 적용 완료 후 회원가입 창으로 이동
             * 1. 원본 이미지 일때는 saveBitmap 이 null 이다
             * 2. 필터 적용한 이미지일때 */
            case R.id.doneBtn:
                if(saveBitmap != null) {
                    Intent filterImageIntent = new Intent(this, Join.class);
                    filterImageIntent.putExtra("filterImage", saveFilterImage(saveBitmap));
                    filterImageIntent.putExtra("fileName", fileName);
                    setResult(RESULT_OK, filterImageIntent);
                    finish();
                }else {
                    Intent originalImageIntent = new Intent(this, Join.class);
                    originalImageIntent.putExtra("filterImage", intent.getStringExtra("filterImage"));
                    setResult(RESULT_OK, originalImageIntent);
                    finish();
                }
                break;

            /* 필터 적용 취소하면 원본 이미지 Uri 다시 가지고 회원가입 창으로 이동 */
            case R.id.cancelFilter:
                DialogInterface.OnClickListener cancelDon = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent cancelIntent = new Intent(getApplicationContext(), Join.class);
                        cancelIntent.putExtra("filterImage", intent.getStringExtra("filterImage"));
                        setResult(RESULT_OK, cancelIntent);
                        finish();
                    }
                };
                DialogInterface.OnClickListener noCancel = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                };
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("필터 적용을 취소하시겠습니까?")
                        .setNegativeButton("아니오", noCancel)
                        .setPositiveButton("예", cancelDon)
                        .show();

                break;
        }
    }

    /* 필터링 된 이미지 임시 저장 */
    public String saveFilterImage(Bitmap outBitmap) {
        String tempFilterImages;
        File storage = getApplicationContext().getCacheDir();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        fileName = "temp_" + timeStamp + ".jpg";
        File cardfile = new File(storage, fileName);
        try {
            cardfile.createNewFile();
            FileOutputStream out = new FileOutputStream(cardfile);
            outBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
        }catch (Exception e) {
            e.printStackTrace();
        }
        tempFilterImages = cardfile.getAbsolutePath();
        filterImages = cardfile.getAbsolutePath();
        return tempFilterImages;
    }

}
