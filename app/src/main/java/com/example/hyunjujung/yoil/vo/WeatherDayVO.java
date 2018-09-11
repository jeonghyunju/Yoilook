package com.example.hyunjujung.yoil.vo;

/**
 * Created by hyunjujung on 2017. 12. 6..
 */

public class WeatherDayVO {
    private int times;
    private String temps;
    private int weatherIcons;

    public WeatherDayVO() {
    }

    public WeatherDayVO(int times, String temps, int weatherIcons) {
        this.times = times;
        this.temps = temps;
        this.weatherIcons = weatherIcons;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getTemps() {
        return temps;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }

    public int getWeatherIcons() {
        return weatherIcons;
    }

    public void setWeatherIcons(int weatherIcons) {
        this.weatherIcons = weatherIcons;
    }
}
