package com.faizi_faiz.weatherforcasting_tranzmeo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.CurrentWeatherResponse;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.GeoResponse;
import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.WeatherResponse;

import java.util.List;

public interface WeatherApiService {
    @GET("forecast")
    Call<WeatherResponse> getForecast(
            @Query("q") String location,
            @Query("appid") String apiKey,
            @Query("units") String units
    );


    @GET("weather") // for current weather
    Call<CurrentWeatherResponse> getCurrentWeather(
            @Query("q") String location,
            @Query("appid") String apiKey,
            @Query("units") String units
    );
    @GET("geo/1.0/direct")
    Call<List<GeoResponse>> getGeoLocation(
            @Query("q") String query,
            @Query("limit") int limit,
            @Query("appid") String apiKey
    );

}
