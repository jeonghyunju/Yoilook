package com.example.hyunjujung.yoil;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;


public class Addarea_googleMap extends AppCompatActivity implements OnMapReadyCallback, LocationListener{
    GoogleMap mGoogleMap;

    EditText searchareaEt;
    Location currentLocation;

    LatLng currentAREA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addarea_google_map);
        searchareaEt = (EditText)findViewById(R.id.searchareaEt);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        String changeA = intent.getStringExtra("changeArea");
        if(changeA != null) {
            searchareaEt.setText(changeA);
        }else {
            searchareaEt.setText("");
        }

    }

    //  지역 추가하기 버튼
    public void addAreaOk(View view) {
        int id = view.getId();
        switch (id){
            //  floatingButton 눌렀을때
            //  회원가입 화면으로 지역이름 가져가기
            case R.id.addareaFloat:
                String searchArea = searchareaEt.getText().toString();
                if(searchArea.equals("")) {
                    //  지역 이름 입력 안했을때 예외처리
                    //  다이얼로그 띄워주기
                    DialogInterface.OnClickListener addarea = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent gojoin = new Intent(getApplicationContext(), Join.class);
                            gojoin.putExtra("searcharea", "");
                            setResult(Activity.RESULT_OK, gojoin);
                            finish();
                        }
                    };
                    DialogInterface.OnClickListener canceladd = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    };
                    new AlertDialog.Builder(this)
                            .setTitle("알림")
                            .setMessage("지역이 설정되지 않았습니다.")
                            .setPositiveButton("확인", addarea)
                            .setNegativeButton("취소", canceladd)
                            .show();
                }else {
                    //  지역 이름 입력 했을때는 회원가입 화면으로 가져간다
                    Intent gojoin = new Intent(this, Join.class);
                    gojoin.putExtra("searcharea", searchArea);
                    setResult(Activity.RESULT_OK, gojoin);
                    finish();
                    Toast.makeText(this, "해당 지역으로 설정되었습니다", Toast.LENGTH_SHORT).show();
                }
                break;

            //  검색하기 버튼 눌렀을때
            //  edittext에서 검색된 검색명으로 geocoder에서 위도, 경도 가져오기
            //  가져온 위도, 경도 넘겨서 지도에 표시하기
            case R.id.search_area:
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try{
                    List<Address> area = geocoder.getFromLocationName(searchareaEt.getText().toString(), 1);
                    Address addr = area.get(0);
                    Log.d("지역", area.toString());
                    setCurrentLocation(addr.getLatitude(), addr.getLongitude(), addr.getAdminArea(), addr.getFeatureName());
                }catch(Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //  구글맵 처음 띄울때 표시되는 위치
    //  현재는 서울로 맞춰놨음
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Intent getAreaIntent = getIntent();
        String changeArea = getAreaIntent.getStringExtra("changeArea");
        mGoogleMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        if(changeArea != null) {
            Geocoder changeGeo = new Geocoder(getApplicationContext(), Locale.getDefault());
            try{
                List<Address> changeAreaList = changeGeo.getFromLocationName(changeArea, 1);
                Address address = changeAreaList.get(0);
                currentAREA = new LatLng(address.getLatitude(), address.getLongitude());
                markerOptions.position(currentAREA);
                markerOptions.title(address.getAdminArea());
                markerOptions.snippet(address.getFeatureName());
                googleMap.addMarker(markerOptions);
            }catch(Exception e) {
                e.printStackTrace();
            }
        }else {
            currentAREA = new LatLng(37.56, 126.97);
            markerOptions.position(currentAREA);
            markerOptions.title("서울");
            markerOptions.snippet("한국의 수도");
            googleMap.addMarker(markerOptions);
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentAREA));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    //
    @Override
    public void onLocationChanged(Location location) {

    }

    public void setCurrentLocation(Double lat, Double lon, String markerTitle, String markerSnippet){
        //  geocoder에서 받아온 위도 경도로 지도에 표시
        LatLng searchArea = new LatLng(lat, lon);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(searchArea);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mGoogleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(searchArea);
        mGoogleMap.moveCamera(cameraUpdate);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }
}
