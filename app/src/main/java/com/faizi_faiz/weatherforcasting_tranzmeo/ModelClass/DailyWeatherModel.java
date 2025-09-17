package com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass;

import java.io.Serializable;
import java.util.List;

public class DailyWeatherModel implements Serializable {
    private String date;
    private String icon;
    private double minTemp;
    private double maxTemp;
    private String description;
    private int humidity;
    private double windSpeed;
    private int pressure;
    private List<HourlyWeatherModel> hourlyList;

    public DailyWeatherModel(String date, String icon, double minTemp, double maxTemp, String description,
                             int humidity, double windSpeed, int pressure, List<HourlyWeatherModel> hourlyList) {
        this.date = date;
        this.icon = icon;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.description = description;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.hourlyList = hourlyList;
    }

    public String getDate() { return date; }
    public String getIcon() { return icon; }
    public double getMinTemp() { return minTemp; }
    public double getMaxTemp() { return maxTemp; }
    public String getDescription() { return description; }
    public int getHumidity() { return humidity; }
    public double getWindSpeed() { return windSpeed; }
    public int getPressure() { return pressure; }
    public List<HourlyWeatherModel> getHourlyList() { return hourlyList; }
}
