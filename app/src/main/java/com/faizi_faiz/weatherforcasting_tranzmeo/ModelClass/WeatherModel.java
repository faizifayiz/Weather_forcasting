// WeatherModel.java
package com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass;

import java.io.Serializable;
import java.util.List;

public class WeatherModel implements Serializable {
    private String location;
    private String timestamp;
    private double temperature;
    private String description;
    private String icon;
    private String jsonData;
    private int humidity;
    private double windSpeed;
    private int pressure;
    private double feelsLike;
    private int id;
    private List<HourlyWeatherModel> hourlyList;
    private List<DailyWeatherModel> dailyList;

    // Getters and Setters
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }

    public double getWindSpeed() { return windSpeed; }
    public void setWindSpeed(double windSpeed) { this.windSpeed = windSpeed; }

    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }

    public double getFeelsLike() { return feelsLike; }
    public void setFeelsLike(double feelsLike) { this.feelsLike = feelsLike; }

    public List<HourlyWeatherModel> getHourlyList() { return hourlyList; }
    public void setHourlyList(List<HourlyWeatherModel> hourlyList) { this.hourlyList = hourlyList; }

    public List<DailyWeatherModel> getDailyList() { return dailyList; }
    public void setDailyList(List<DailyWeatherModel> dailyList) { this.dailyList = dailyList; }

    public String getJsonData() { return jsonData; }
    public void setJsonData(String jsonData) { this.jsonData = jsonData; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
}
