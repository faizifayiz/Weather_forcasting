package com.faizi_faiz.weatherforcasting_tranzmeo;

import android.provider.BaseColumns;

public final class WeatherContract {
    private WeatherContract() {}

    public static class WeatherEntry implements BaseColumns {
        public static final String TABLE_NAME = "weather_forecast";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_LOCATION = "location";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_TEMPERATURE = "temperature";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ICON = "icon";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_FEELS_LIKE = "feels_like";
        public static final String COLUMN_JSON_DATA = "json_data";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_LOCATION + " TEXT, " +
                COLUMN_TIMESTAMP + " TEXT, " +
                COLUMN_TEMPERATURE + " REAL, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_ICON + " TEXT, " +
                COLUMN_HUMIDITY + " INTEGER, " +
                COLUMN_WIND_SPEED + " REAL, " +
                COLUMN_PRESSURE + " INTEGER, " +
                COLUMN_FEELS_LIKE + " REAL, " +
                COLUMN_JSON_DATA + " TEXT" +
                ")";
    }
}