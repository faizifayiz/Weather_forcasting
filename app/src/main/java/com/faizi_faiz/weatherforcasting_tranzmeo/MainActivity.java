package com.faizi_faiz.weatherforcasting_tranzmeo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.faizi_faiz.weatherforcasting_tranzmeo.Adapter.DailyForecastAdapter;
import com.faizi_faiz.weatherforcasting_tranzmeo.Adapter.HourlyAdapter;
import com.faizi_faiz.weatherforcasting_tranzmeo.LocalDB.DatabaseHelper;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.CurrentWeatherResponse;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.DailyWeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.GeoResponse;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.HourlyWeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.WeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.WeatherResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerHourly, recyclerDaily;
    private DatabaseHelper dbHelper;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> autoCompleteAdapter;

    // Current weather card views
    private TextView tvLocation, tvTemperature, tvCondition, tvHighLow, tvFeelsLike;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private static final String API_KEY = "85c64fd365bf858596de8f52373a2063";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Request location when activity starts
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        initializeViews();
        setupDatabase();
        setupAutoComplete();
        setupButtonClickListener();
        loadSavedLocations();
    }

    private void initializeViews() {
        recyclerHourly = findViewById(R.id.recycler_hourly);
        recyclerDaily = findViewById(R.id.recycler_daily);
        autoCompleteTextView = findViewById(R.id.et_location);

        // Current weather card
        tvLocation = findViewById(R.id.tv_location);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvCondition = findViewById(R.id.tv_condition);
        tvHighLow = findViewById(R.id.tv_high_low);
        tvFeelsLike = findViewById(R.id.tv_feels_like);
    }

    private void setupDatabase() {
        dbHelper = new DatabaseHelper(this);
    }

    private void setupAutoComplete() {
        autoCompleteAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new ArrayList<>());
        autoCompleteTextView.setAdapter(autoCompleteAdapter);
        autoCompleteTextView.setThreshold(1);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    if (isNetworkAvailable()) {
                        fetchOnlineSuggestions(query);
                    } else {
                        fetchOfflineSuggestions(query);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchOnlineSuggestions(String query) {
        WeatherApiService service = GeoApiClient.getClient().create(WeatherApiService.class);
        Call<List<GeoResponse>> call = service.getGeoLocation(query, 5, API_KEY);

        call.enqueue(new Callback<List<GeoResponse>>() {
            @Override
            public void onResponse(Call<List<GeoResponse>> call, Response<List<GeoResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> suggestions = new ArrayList<>();
                    for (GeoResponse geo : response.body()) {
                        String name = geo.getName() +
                                (geo.getState() != null ? ", " + geo.getState() : "") +
                                ", " + geo.getCountry();
                        suggestions.add(name);

                        // ✅ Save to DB for offline use
                        dbHelper.saveLocation(name);
                    }
                    autoCompleteAdapter.clear();
                    autoCompleteAdapter.addAll(suggestions);
                    autoCompleteAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<GeoResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Suggestion error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchOfflineSuggestions(String query) {
        List<String> saved = dbHelper.getSavedLocations();
        List<String> filtered = new ArrayList<>();
        for (String loc : saved) {
            if (loc.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(loc);
            }
        }
        autoCompleteAdapter.clear();
        autoCompleteAdapter.addAll(filtered);
        autoCompleteAdapter.notifyDataSetChanged();
    }


    private void setupButtonClickListener() {
        Button btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(v -> {
            String location = autoCompleteTextView.getText().toString().trim();
            if (!location.isEmpty()) {
                fetchWeatherData(location);
            } else {
                Toast.makeText(MainActivity.this, "Please enter a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSavedLocations() {
        List<String> savedLocations = dbHelper.getSavedLocations();
        if (!savedLocations.isEmpty()) {
            String mostRecent = savedLocations.get(0);
            autoCompleteTextView.setText(mostRecent);
            fetchWeatherData(mostRecent);
        }
    }

    private void fetchWeatherData(String location) {
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "No internet, loading saved data...", Toast.LENGTH_SHORT).show();
            loadWeatherFromDatabase(location);
            return;
        }

        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = service.getForecast(location, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    // ---- Current Weather ----
                    WeatherResponse.ForecastItem firstItem = weatherResponse.getList().get(0);
                    WeatherModel current = new WeatherModel();
                    current.setLocation(weatherResponse.getCity().getName() + "," + weatherResponse.getCity().getCountry());
                    current.setTimestamp(firstItem.getDtTxt());
                    current.setTemperature(firstItem.getMain().getTemp());
                    current.setFeelsLike(firstItem.getMain().getFeelsLike());
                    current.setHumidity(firstItem.getMain().getHumidity());
                    current.setPressure(firstItem.getMain().getPressure());
                    current.setWindSpeed(firstItem.getWind().getSpeed());
                    current.setDescription(firstItem.getWeather().get(0).getDescription());
                    current.setIcon(firstItem.getWeather().get(0).getIcon());

                    updateCurrentWeatherCard(current);

                    // ---- Hourly Weather ----
                    List<HourlyWeatherModel> hourlyList = new ArrayList<>();
                    for (WeatherResponse.ForecastItem item : weatherResponse.getList()) {
                        String time = item.getDtTxt().split(" ")[1].substring(0, 5);
                        String icon = item.getWeather().get(0).getIcon();
                        double temp = item.getMain().getTemp();
                        int precipitation = (int) (item.getPop() * 100);
                        hourlyList.add(new HourlyWeatherModel(time, icon, temp, precipitation));
                    }
                    recyclerHourly.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                    HourlyAdapter hourlyAdapter = new HourlyAdapter(MainActivity.this, hourlyList);
                    recyclerHourly.setAdapter(hourlyAdapter);

                    // ---- Daily Weather ----
                    Map<String, List<WeatherResponse.ForecastItem>> dailyMap = new LinkedHashMap<>();
                    for (WeatherResponse.ForecastItem item : weatherResponse.getList()) {
                        String date = item.getDtTxt().split(" ")[0];
                        dailyMap.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
                    }

                    List<DailyWeatherModel> dailyList = new ArrayList<>();
                    for (Map.Entry<String, List<WeatherResponse.ForecastItem>> entry : dailyMap.entrySet()) {
                        String date = entry.getKey();
                        double minTemp = Double.MAX_VALUE, maxTemp = Double.MIN_VALUE;
                        String icon = entry.getValue().get(0).getWeather().get(0).getIcon();
                        String description = entry.getValue().get(0).getWeather().get(0).getDescription();
                        int humidity = entry.getValue().get(0).getMain().getHumidity();
                        double wind = entry.getValue().get(0).getWind().getSpeed();
                        int pressure = entry.getValue().get(0).getMain().getPressure();

                        List<HourlyWeatherModel> dayHourlyList = new ArrayList<>();
                        for (WeatherResponse.ForecastItem item : entry.getValue()) {
                            double temp = item.getMain().getTemp();
                            if (temp < minTemp) minTemp = temp;
                            if (temp > maxTemp) maxTemp = temp;

                            String time = item.getDtTxt().split(" ")[1].substring(0, 5);
                            dayHourlyList.add(new HourlyWeatherModel(
                                    time,
                                    item.getWeather().get(0).getIcon(),
                                    temp,
                                    (int) (item.getPop() * 100)
                            ));
                        }

                        dailyList.add(new DailyWeatherModel(date, icon, minTemp, maxTemp,
                                description, humidity, wind, pressure, dayHourlyList));
                    }

                    recyclerDaily.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                    DailyForecastAdapter dailyAdapter = new DailyForecastAdapter(MainActivity.this, dailyList);
                    recyclerDaily.setAdapter(dailyAdapter);

                } else {
                    Toast.makeText(MainActivity.this, "API error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCurrentWeatherCard(WeatherModel model) {
        tvLocation.setText(model.getLocation());
        tvTemperature.setText(String.format(Locale.getDefault(), "%.0f°", model.getTemperature()));
        tvCondition.setText(model.getDescription());
        tvFeelsLike.setText("Feels like " + String.format(Locale.getDefault(), "%.0f°", model.getFeelsLike()));
        tvHighLow.setText("↑" + (model.getTemperature() + 2) + "° / ↓" + (model.getTemperature() - 2) + "°");

        // Make card clickable
        View currentCard = findViewById(R.id.current_weather_card);
        currentCard.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, WeatherDetailActivity.class);
            intent.putExtra("weather_data", model);
            startActivity(intent);
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo ni = cm.getActiveNetworkInfo();
            return ni != null && ni.isConnected();
        }
        return false;
    }



    private void loadWeatherFromDatabase(String location) {
        List<WeatherModel> savedData = dbHelper.getWeatherByLocation(location);

        if (savedData != null && !savedData.isEmpty()) {
            WeatherModel current = savedData.get(0); // Most recent record

            updateCurrentWeatherCard(current);

            // Parse hourly + daily data if JSON stored
            if (current.getJsonData() != null) {
                WeatherResponse response = new Gson().fromJson(current.getJsonData(), WeatherResponse.class);

                // ---- Hourly ----
                List<HourlyWeatherModel> hourlyList = new ArrayList<>();
                for (WeatherResponse.ForecastItem item : response.getList()) {
                    String time = item.getDtTxt().split(" ")[1].substring(0, 5);
                    hourlyList.add(new HourlyWeatherModel(
                            time,
                            item.getWeather().get(0).getIcon(),
                            item.getMain().getTemp(),
                            (int) (item.getPop() * 100)
                    ));
                }
                recyclerHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                recyclerHourly.setAdapter(new HourlyAdapter(this, hourlyList));

                // ---- Daily ----
                Map<String, List<WeatherResponse.ForecastItem>> dailyMap = new LinkedHashMap<>();
                for (WeatherResponse.ForecastItem item : response.getList()) {
                    String date = item.getDtTxt().split(" ")[0];
                    dailyMap.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
                }

                List<DailyWeatherModel> dailyList = new ArrayList<>();
                for (Map.Entry<String, List<WeatherResponse.ForecastItem>> entry : dailyMap.entrySet()) {
                    String date = entry.getKey();
                    double minTemp = Double.MAX_VALUE, maxTemp = Double.MIN_VALUE;
                    String icon = entry.getValue().get(0).getWeather().get(0).getIcon();
                    String desc = entry.getValue().get(0).getWeather().get(0).getDescription();
                    int humidity = entry.getValue().get(0).getMain().getHumidity();
                    double wind = entry.getValue().get(0).getWind().getSpeed();
                    int pressure = entry.getValue().get(0).getMain().getPressure();

                    List<HourlyWeatherModel> dayHourlyList = new ArrayList<>();
                    for (WeatherResponse.ForecastItem item : entry.getValue()) {
                        double temp = item.getMain().getTemp();
                        if (temp < minTemp) minTemp = temp;
                        if (temp > maxTemp) maxTemp = temp;

                        String time = item.getDtTxt().split(" ")[1].substring(0, 5);
                        dayHourlyList.add(new HourlyWeatherModel(
                                time,
                                item.getWeather().get(0).getIcon(),
                                temp,
                                (int) (item.getPop() * 100)
                        ));
                    }

                    dailyList.add(new DailyWeatherModel(date, icon, minTemp, maxTemp, desc, humidity, wind, pressure, dayHourlyList));
                }

                recyclerDaily.setLayoutManager(new LinearLayoutManager(this));
                recyclerDaily.setAdapter(new DailyForecastAdapter(this, dailyList));
            }

        } else {
            Toast.makeText(this, "No offline data found for " + location, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission denied. Could not get location.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void fetchWeatherDataByCoordinates(double lat, double lon) {
        WeatherApiService service = ApiClient.getClient().create(WeatherApiService.class);
        Call<WeatherResponse> call = service.getForecastByCoordinates(lat, lon, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // ✅ Pass to your UI handler
                    handleWeatherResponse(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "API error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();

//                        Toast.makeText(this,
//                                "Lat: " + latitude + ", Lon: " + longitude,
//                                Toast.LENGTH_LONG).show();

                        // TODO: Call your weather API with lat & lon instead of city name
                        fetchWeatherDataByCoordinates(latitude, longitude);
                    } else {
                        Toast.makeText(this, "Could not get location. Search manually.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void handleWeatherResponse(WeatherResponse weatherResponse) {
        if (weatherResponse == null || weatherResponse.getList().isEmpty()) return;

        // ---- Current Weather ----
        WeatherResponse.ForecastItem firstItem = weatherResponse.getList().get(0);
        WeatherModel current = new WeatherModel();
        current.setLocation(weatherResponse.getCity().getName() + "," + weatherResponse.getCity().getCountry());
        current.setTimestamp(firstItem.getDtTxt());
        current.setTemperature(firstItem.getMain().getTemp());
        current.setFeelsLike(firstItem.getMain().getFeelsLike());
        current.setHumidity(firstItem.getMain().getHumidity());
        current.setPressure(firstItem.getMain().getPressure());
        current.setWindSpeed(firstItem.getWind().getSpeed());
        current.setDescription(firstItem.getWeather().get(0).getDescription());
        current.setIcon(firstItem.getWeather().get(0).getIcon());

        updateCurrentWeatherCard(current);

        // ---- Hourly ----
        List<HourlyWeatherModel> hourlyList = new ArrayList<>();
        for (WeatherResponse.ForecastItem item : weatherResponse.getList()) {
            String time = item.getDtTxt().split(" ")[1].substring(0, 5);
            hourlyList.add(new HourlyWeatherModel(
                    time,
                    item.getWeather().get(0).getIcon(),
                    item.getMain().getTemp(),
                    (int) (item.getPop() * 100)
            ));
        }
        recyclerHourly.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerHourly.setAdapter(new HourlyAdapter(this, hourlyList));

        // ---- Daily ----
        Map<String, List<WeatherResponse.ForecastItem>> dailyMap = new LinkedHashMap<>();
        for (WeatherResponse.ForecastItem item : weatherResponse.getList()) {
            String date = item.getDtTxt().split(" ")[0];
            dailyMap.computeIfAbsent(date, k -> new ArrayList<>()).add(item);
        }

        List<DailyWeatherModel> dailyList = new ArrayList<>();
        for (Map.Entry<String, List<WeatherResponse.ForecastItem>> entry : dailyMap.entrySet()) {
            String date = entry.getKey();
            double minTemp = Double.MAX_VALUE, maxTemp = Double.MIN_VALUE;
            String icon = entry.getValue().get(0).getWeather().get(0).getIcon();
            String desc = entry.getValue().get(0).getWeather().get(0).getDescription();
            int humidity = entry.getValue().get(0).getMain().getHumidity();
            double wind = entry.getValue().get(0).getWind().getSpeed();
            int pressure = entry.getValue().get(0).getMain().getPressure();

            List<HourlyWeatherModel> dayHourlyList = new ArrayList<>();
            for (WeatherResponse.ForecastItem item : entry.getValue()) {
                double temp = item.getMain().getTemp();
                if (temp < minTemp) minTemp = temp;
                if (temp > maxTemp) maxTemp = temp;

                String time = item.getDtTxt().split(" ")[1].substring(0, 5);
                dayHourlyList.add(new HourlyWeatherModel(
                        time,
                        item.getWeather().get(0).getIcon(),
                        temp,
                        (int) (item.getPop() * 100)
                ));
            }

            dailyList.add(new DailyWeatherModel(date, icon, minTemp, maxTemp, desc, humidity, wind, pressure, dayHourlyList));
        }

        recyclerDaily.setLayoutManager(new LinearLayoutManager(this));
        recyclerDaily.setAdapter(new DailyForecastAdapter(this, dailyList));
    }


}
