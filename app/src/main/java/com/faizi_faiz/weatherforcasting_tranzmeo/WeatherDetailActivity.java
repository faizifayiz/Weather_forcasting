package com.faizi_faiz.weatherforcasting_tranzmeo;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.faizi_faiz.weatherforcasting_tranzmeo.Adapter.DailyForecastAdapter;
import com.faizi_faiz.weatherforcasting_tranzmeo.Adapter.HourlyAdapter;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.DailyWeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.WeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.R;

import java.util.Locale;

public class WeatherDetailActivity extends AppCompatActivity {

    private ViewGroup rootLayout;

    private TextView tvLocation, tvDateTime, tvTemperature, tvDescription,
            tvFeelsLike, tvHumidity, tvWindSpeed, tvPressure;
    private ImageView ivWeatherIcon;
    private RecyclerView recyclerHourly, recyclerDaily;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_detail);

        rootLayout = findViewById(R.id.root_detail);

        // Init views
        ivWeatherIcon = findViewById(R.id.iv_weather_icon_detail);
        tvLocation = findViewById(R.id.tv_location_detail);
        tvDateTime = findViewById(R.id.tv_date_time_detail);
        tvTemperature = findViewById(R.id.tv_temperature_detail);
        tvDescription = findViewById(R.id.tv_description_detail);
        tvFeelsLike = findViewById(R.id.tv_feels_like_detail);
        tvHumidity = findViewById(R.id.tv_humidity_detail);
        tvWindSpeed = findViewById(R.id.tv_wind_speed_detail);
        tvPressure = findViewById(R.id.tv_pressure_detail);

        recyclerHourly = findViewById(R.id.recycler_hourly);
        recyclerDaily = findViewById(R.id.recycler_daily);

        // Case 1: From main weather
        WeatherModel weather = (WeatherModel) getIntent().getSerializableExtra("weather_data");

        // Case 2: From daily forecast
        DailyWeatherModel dailyWeather = (DailyWeatherModel) getIntent().getSerializableExtra("daily_weather");

        if (weather != null) {
            showWeatherModelDetails(weather);
        } else if (dailyWeather != null) {
            showDailyWeatherDetails(dailyWeather);
        }
    }

    private void showWeatherModelDetails(WeatherModel weather) {
        // Hourly
        if (weather.getHourlyList() != null) {
            recyclerHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            HourlyAdapter hourlyAdapter = new HourlyAdapter(this, weather.getHourlyList());
            recyclerHourly.setAdapter(hourlyAdapter);
        }

        // Daily
        if (weather.getDailyList() != null) {
            recyclerDaily.setLayoutManager(new LinearLayoutManager(this));
            DailyForecastAdapter dailyAdapter = new DailyForecastAdapter(this, weather.getDailyList());
            recyclerDaily.setAdapter(dailyAdapter);
        }

        // Background
        setWeatherBackground(weather.getIcon());

        // ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(weather.getLocation());
        }

        // Icon
        String iconUrl = "https://openweathermap.org/img/wn/" + weather.getIcon() + "@4x.png";
        Glide.with(this)
                .load(iconUrl)
                .placeholder(R.drawable.ic_baseline_wb_sunny_24)
                .into(ivWeatherIcon);

        // Details
        tvLocation.setText(safeText(weather.getLocation()));
        tvDateTime.setText(safeText(weather.getTimestamp()));
        tvTemperature.setText(String.format(Locale.getDefault(), "%.1f°C", weather.getTemperature()));
        tvDescription.setText(safeText(weather.getDescription()));
        tvFeelsLike.setText(String.format(Locale.getDefault(), "Feels like: %.1f°C", weather.getFeelsLike()));
        tvHumidity.setText(String.format(Locale.getDefault(), "Humidity: %d%%", weather.getHumidity()));
        tvWindSpeed.setText(String.format(Locale.getDefault(), "Wind Speed: %.1f m/s", weather.getWindSpeed()));
        tvPressure.setText(String.format(Locale.getDefault(), "Pressure: %d hPa", weather.getPressure()));
    }

    private void showDailyWeatherDetails(DailyWeatherModel weather) {
        // Background
        setWeatherBackground(weather.getIcon());

        // ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Daily Forecast");
        }

        // Icon
        String iconUrl = "https://openweathermap.org/img/wn/" + weather.getIcon() + "@4x.png";
        Glide.with(this)
                .load(iconUrl)
                .placeholder(R.drawable.ic_baseline_wb_sunny_24)
                .into(ivWeatherIcon);

        // Details
        tvLocation.setText(weather.getDate());
        tvDateTime.setText(""); // Daily forecast usually doesn't need timestamp
        tvTemperature.setText(String.format(Locale.getDefault(), "↑%.0f° / ↓%.0f°", weather.getMaxTemp(), weather.getMinTemp()));
        tvDescription.setText(safeText(weather.getDescription()));
        tvFeelsLike.setText(""); // Not needed for daily
        tvHumidity.setText("Humidity: " + weather.getHumidity() + "%");
        tvWindSpeed.setText("Wind: " + weather.getWindSpeed() + " m/s");
        tvPressure.setText("Pressure: " + weather.getPressure() + " hPa");

        // Hourly forecast for that day
        if (weather.getHourlyList() != null) {
            recyclerHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            HourlyAdapter hourlyAdapter = new HourlyAdapter(this, weather.getHourlyList());
            recyclerHourly.setAdapter(hourlyAdapter);
        }

        // Hide daily list because we are already in one day's details
        recyclerDaily.setVisibility(RecyclerView.GONE);
    }

    private void setWeatherBackground(String iconCode) {
        if (iconCode == null) {
            rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_clear_sky));
            return;
        }

        switch (iconCode) {
            case "01d": case "01n":
                rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_clear_sky));
                break;
            case "02d": case "02n":
            case "03d": case "03n":
                rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_few_clouds));
                break;
            case "04d": case "04n":
                rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_cloudy));
                break;
            case "09d": case "09n":
            case "10d": case "10n":
                rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_rain));
                break;
            case "11d": case "11n":
                rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_thunderstorm));
                break;
            case "13d": case "13n":
                rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_snow));
                break;
            case "50d": case "50n":
                rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_mist));
                break;
            default:
                rootLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_clear_sky));
                break;
        }
    }

    private String safeText(String text) {
        return text != null ? text : "";
    }
}
