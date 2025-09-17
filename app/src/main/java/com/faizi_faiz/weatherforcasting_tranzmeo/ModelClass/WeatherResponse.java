package com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherResponse {
    private City city;
    private List<ForecastItem> list;

    public City getCity() { return city; }
    public List<ForecastItem> getList() { return list; }

    public static class City {
        private String name;
        private String country;
        public String getName() { return name; }
        public String getCountry() { return country; }
    }

    public static class ForecastItem {
        private long dt;
        private Main main;
        private List<Weather> weather;
        private Wind wind;
        private String dt_txt;

        private double pop;  // probability of precipitation (0.0 to 1.0)


        public long getDt() { return dt; }
        public Main getMain() { return main; }
        public List<Weather> getWeather() { return weather; }
        public Wind getWind() { return wind; }
        public String getDtTxt() { return dt_txt; }

        // Add getter for pop
        public double getPop() { return pop; }
    }

    public static class Main {
        private double temp;
        private double feels_like;
        private int humidity;
        private int pressure;

        public double getTemp() { return temp; }
        public double getFeelsLike() { return feels_like; }
        public int getHumidity() { return humidity; }
        public int getPressure() { return pressure; }
    }

    public static class Weather {
        private String description;
        private String icon;
        public String getDescription() { return description; }
        public String getIcon() { return icon; }
    }

    public static class Wind {
        private double speed;
        public double getSpeed() { return speed; }
    }
}
