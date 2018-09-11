package com.example.hyunjujung.yoil;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.hyunjujung.yoil.apdater.WeatherClothesAdapter;
import com.example.hyunjujung.yoil.apdater.WeatherDayAdapter;
import com.example.hyunjujung.yoil.apis.WeatherApi;
import com.example.hyunjujung.yoil.apis.WeatherRetrofit;
import com.example.hyunjujung.yoil.chatting.Chatting;
import com.example.hyunjujung.yoil.vo.WeatherDayVO;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherClothes extends AppCompatActivity implements LocationListener{
    TextView todayDateTv,
            weather_area_Tv,
            weather_maxTemp_Tv,
            weather_minTemp_Tv,
            weather_nowTemp_Tv,
            weather_sky_Tv,
            weather_codi_comment;
    ImageView weather_codi_imageview;

    RecyclerView weather_cloth_recycle;
    RecyclerView.LayoutManager weatherLayout;
    WeatherClothesAdapter weatherAdapter;
    ArrayList<Integer> weatherArray = new ArrayList<>();

    RecyclerView time_weather_recycle;
    RecyclerView.LayoutManager time_weather_layout;
    WeatherDayAdapter weatherDayAdapter;
    ArrayList<WeatherDayVO> weatherDayArray = new ArrayList<>();
    WeatherDayVO weatherDayVO;

    /* 현재 위치 받아오는 변수 */
    LocationManager locationManager;
    double latitude;
    double longitude;

    String codiComment;

    Intent intent;

    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_clothes);

        todayDateTv = (TextView)findViewById(R.id.todayDateTv);
        weather_area_Tv = (TextView)findViewById(R.id.weather_area_Tv);
        weather_maxTemp_Tv = (TextView)findViewById(R.id.weather_maxTemp_Tv);
        weather_minTemp_Tv = (TextView)findViewById(R.id.weather_minTemp_Tv);
        weather_nowTemp_Tv = (TextView)findViewById(R.id.weather_nowTemp_Tv);
        weather_sky_Tv = (TextView)findViewById(R.id.weather_sky_Tv);
        weather_codi_comment = (TextView)findViewById(R.id.weather_codi_comment);
        weather_codi_imageview = (ImageView)findViewById(R.id.weather_codi_imageview);
        weather_cloth_recycle = (RecyclerView)findViewById(R.id.weather_cloth_recycle);
        time_weather_recycle = (RecyclerView)findViewById(R.id.time_weather_recycle);

        /* 코디 RecyclerView */
        weatherLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        weather_cloth_recycle.setLayoutManager(weatherLayout);
        weatherAdapter = new WeatherClothesAdapter(this, weatherArray);
        weather_cloth_recycle.setAdapter(weatherAdapter);

        /* 날씨 RecyclerView */
        time_weather_layout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        time_weather_recycle.setLayoutManager(time_weather_layout);
        weatherDayAdapter = new WeatherDayAdapter(this, weatherDayArray);
        time_weather_recycle.setAdapter(weatherDayAdapter);

        locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        /* 현재 위치의 날씨를 가져온다 */
        requestLocation();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
        String messageDate = simpleDateFormat.format(calendar.getTime());

        todayDateTv.setText(messageDate);

    }

    @Override
    protected void onStop() {
        super.onStop();
        /* 액티비티가 onstop 되었을때는 locationManager 의 자원을 해제한다 */
        locationManager.removeUpdates(WeatherClothes.this);
    }

    /* locationlistener override method */
    @Override
    public void onLocationChanged(Location location) {
        /* 사용자의 변경된 위치의 위도와 경도를 가져온다 */
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /* 위치 정보 권한 */
    private void requestLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 1, this);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 1, this);

            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            latitude = currentLocation.getLatitude();
            longitude = currentLocation.getLongitude();
            Log.e("현재 위치", "" + latitude + " , " + longitude);
            if(currentLocation != null) {

                /* 현재 날씨 가져온다 */
                WeatherApi weathers = WeatherRetrofit.getWeatherApi();
                Call<JsonObject> wCall = weathers.getHourly(WeatherApi.APPKEY, 1, latitude, longitude);
                wCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        try{
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            JSONObject hourly = (JSONObject)jsonObject.get("weather");
                            JSONArray jsonArray = new JSONArray(hourly.getString("hourly"));
                            jsonObject = (JSONObject)jsonArray.getJSONObject(0).get("grid");
                            weather_area_Tv.setText(jsonObject.getString("county"));
                            jsonObject = (JSONObject)jsonArray.getJSONObject(0).get("temperature");
                            weather_nowTemp_Tv.setText(jsonObject.getString("tc").substring(0, jsonObject.getString("tc").length()-3) + " °C");
                            weather_maxTemp_Tv.setText(jsonObject.getString("tmax").substring(0, jsonObject.getString("tmax").length()-3) + " °C");
                            weather_minTemp_Tv.setText(jsonObject.getString("tmin").substring(0, jsonObject.getString("tmin").length()-3) + " °C");
                            int nowTemp = Integer.parseInt(jsonObject.getString("tc").substring(0, jsonObject.getString("tc").length()-3));

                            /* 현재 기온 */
                            if(nowTemp <= 5.00) {
                                weatherArray.add(R.drawable.padding);
                                weatherArray.add(R.drawable.mittens);
                                weatherArray.add(R.drawable.scarf);
                                weatherArray.add(R.drawable.winterhat);
                                codiComment = "날씨가 추워요~ 따뜻하게 입어요. ";
                            }else if(nowTemp > 5.00 && nowTemp <= 9.00) {
                                weatherArray.add(R.drawable.coat);
                                weatherArray.add(R.drawable.jacket);
                                codiComment = "오늘 날씨는 코트나 가죽자켓이 좋겠어요. ";
                            }else if(nowTemp > 9.00 && nowTemp <= 11.00) {
                                weatherArray.add(R.drawable.trench);
                                weatherArray.add(R.drawable.jacketcoat);
                                weatherArray.add(R.drawable.pullover);
                                codiComment = "가을이 왔어요. 트렌치 코트를 꺼내봐요. ";
                            }else if(nowTemp > 11.00 && nowTemp <= 16.00) {
                                weatherArray.add(R.drawable.mai);
                                weatherArray.add(R.drawable.hudjumper);
                                weatherArray.add(R.drawable.jumper);
                                codiComment = "간절기엔 자켓이 딱이에요. ";
                            }else if(nowTemp > 16.00 && nowTemp <= 19.00) {
                                weatherArray.add(R.drawable.shirt);
                                weatherArray.add(R.drawable.hoodie);
                                weatherArray.add(R.drawable.cottenpants);
                                codiComment = "";
                            }else if(nowTemp > 19.00 && nowTemp <= 22.00) {
                                weatherArray.add(R.drawable.halfshirts);
                                weatherArray.add(R.drawable.autumndress);
                                weatherArray.add(R.drawable.jeans);
                                codiComment = "";
                            }else if(nowTemp > 22.00 && nowTemp <= 26.00) {
                                weatherArray.add(R.drawable.tshirts);
                                weatherArray.add(R.drawable.polo);
                                weatherArray.add(R.drawable.shortpants);
                                codiComment = "나들이 하기 딱 좋은 날씨네요. ";
                            }else {
                                weatherArray.add(R.drawable.basketballjersey);
                                weatherArray.add(R.drawable.dress);
                                weatherArray.add(R.drawable.cap);
                                weatherArray.add(R.drawable.glasses);
                                codiComment = "가벼운 옷차림이 좋겠어요. 자외선을 차단해줄 아이템을 챙기세요. ";
                            }

                            jsonObject = (JSONObject)jsonArray.getJSONObject(0).get("sky");
                            weather_codi_imageview.
                                    setImageResource(R.drawable._01 + setWeatherIcon(jsonObject.getString("code")
                                            .substring(jsonObject.getString("code").length()-2,
                                                    jsonObject.getString("code").length())));
                            weather_sky_Tv.setText(jsonObject.getString("name"));
                            jsonObject = (JSONObject)jsonArray.getJSONObject(0).get("precipitation");

                            /* 현재 날씨 강수 코드 */
                            switch (jsonObject.getString("type")) {
                                /* 비옴 */
                                case "1":
                                    weatherArray.add(R.drawable.umbrella);
                                    codiComment += "비가 올 수 있어요. 우산을 챙기세요!";
                                    break;
                                /* 비, 눈 */
                                case "2":
                                    weatherArray.add(R.drawable.umbrella);
                                    codiComment += "비나 눈이 올 수 있어요. 우산을 챙기세요!";
                                    break;
                                /* 눈 */
                                case "3":
                                    weatherArray.add(R.drawable.umbrella);
                                    codiComment += "눈이 올 수 있어요. 우산을 챙기세요!";
                                    break;
                            }

                            weather_codi_comment.setText(codiComment);
                            weatherAdapter.setclothesArray(weatherArray);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {

                    }
                });

                /* 시간별로 날씨 가져온다 */
                WeatherApi weatherApi = WeatherRetrofit.getWeatherApi();
                Call<JsonObject> weatherCall = weatherApi.getForecast(WeatherApi.APPKEY, 1, latitude, longitude);
                weatherCall.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        int icons = R.drawable._01;
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            JSONObject weather = (JSONObject) jsonObject.get("weather");

                            JSONArray jsonArray = new JSONArray(weather.getString("forecast3days"));

                            JSONObject fcstObject = jsonArray.getJSONObject(0);

                            JSONObject fcst3hour = new JSONObject(fcstObject.getString("fcst3hour"));

                            JSONObject sky = new JSONObject(fcst3hour.getString("sky"));

                            JSONObject temperature = new JSONObject(fcst3hour.getString("temperature"));

                            Log.d("sky",sky.toString()+"..");
                            Log.d("temperature",temperature.toString()+"..");

                            for(int i = 4; i<28 ; i += 3){
                                Log.d("sky ["+i+"]",temperature.getString("temp"+i+"hour"));
                                int times = Integer.parseInt(fcstObject.getString("timeRelease").substring(11, 13)) + i;
                                weatherDayVO = new WeatherDayVO(times,
                                        temperature.getString("temp"+i+"hour").substring(0,
                                                temperature.getString("temp" + i + "hour").length()-3),
                                        icons + setWeatherIcon(sky.getString("code" + i + "hour")
                                                .substring(sky.getString("code" + i + "hour").length()-2,
                                                        sky.getString("code" + i + "hour").length())));
                                weatherDayArray.add(weatherDayVO);
                            }

                            weatherDayAdapter.setWeatherDays(weatherDayArray);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        Log.e("실패", t.getMessage());
                    }
                });

            }
        }
        //locationManager.removeUpdates(WeatherClothes.this);
    }

    public int setWeatherIcon(String skyCode) {
        int skys = Integer.parseInt(skyCode) - 1;
        return skys;
    }

    public void clickMenu(View view) {
        int id = view.getId();
        switch (id) {
            //  홈버튼
            case R.id.home:
                intent = new Intent(this, YoilMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;

            //  favorite 버튼
            case R.id.favorite:
                intent = new Intent(this, MyFavoriteList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;

            //  타임라인에 글쓰기 버튼
            case R.id.addtimeline:
                intent = new Intent(this, WriteTimeline.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;

            //  채팅하기 버튼
            case R.id.chatting:
                intent = new Intent(this, Chatting.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;

            //  내 정보 버튼
            case R.id.myinfo:
                intent = new Intent(this, MyInfo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                break;
        }
    }

}
