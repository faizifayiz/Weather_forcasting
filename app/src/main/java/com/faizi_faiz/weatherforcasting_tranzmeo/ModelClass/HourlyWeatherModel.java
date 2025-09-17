package com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass;

import java.io.Serializable;

public class HourlyWeatherModel implements Serializable {
    private String time;
    private String icon;
    private double temp;
    private int precipitation;

    public HourlyWeatherModel(String time, String icon, double temp, int precipitation) {
        this.time = time;
        this.icon = icon;
        this.temp = temp;
        this.precipitation = precipitation;
    }

    public String getTime() { return time; }
    public String getIcon() { return icon; }
    public double getTemp() { return temp; }
    public int getPrecipitation() { return precipitation; }
}