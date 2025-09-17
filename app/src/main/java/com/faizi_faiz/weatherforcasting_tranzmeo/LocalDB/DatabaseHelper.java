package com.faizi_faiz.weatherforcasting_tranzmeo.LocalDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass.WeatherModel;
import com.faizi_faiz.weatherforcasting_tranzmeo.WeatherContract;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 1; // Incremented version to handle schema changes
    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_LOCATIONS = "locations";
    private static final String COL_ID = "id";
    private static final String COL_NAME = "name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_NAME + " TEXT UNIQUE)";
        db.execSQL(createTable);
        db.execSQL(WeatherContract.WeatherEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now, just drop and recreate the table
        // In a production app, you would implement proper migration logic
        db.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }

    // Helper method to safely get column index
    private int getColumnIndexSafe(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index == -1) {
            Log.e(TAG, "Column not found: " + columnName);
            throw new IllegalArgumentException("Column not found: " + columnName);
        }
        return index;
    }

    public long insertWeatherData(WeatherModel weather) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WeatherContract.WeatherEntry.COLUMN_LOCATION, weather.getLocation());
        values.put(WeatherContract.WeatherEntry.COLUMN_TIMESTAMP, weather.getTimestamp());
        values.put(WeatherContract.WeatherEntry.COLUMN_TEMPERATURE, weather.getTemperature());
        values.put(WeatherContract.WeatherEntry.COLUMN_DESCRIPTION, weather.getDescription());
        values.put(WeatherContract.WeatherEntry.COLUMN_ICON, weather.getIcon());
        values.put(WeatherContract.WeatherEntry.COLUMN_HUMIDITY, weather.getHumidity());
        values.put(WeatherContract.WeatherEntry.COLUMN_WIND_SPEED, weather.getWindSpeed());
        values.put(WeatherContract.WeatherEntry.COLUMN_PRESSURE, weather.getPressure());
        values.put(WeatherContract.WeatherEntry.COLUMN_FEELS_LIKE, weather.getFeelsLike());
        values.put(WeatherContract.WeatherEntry.COLUMN_JSON_DATA, weather.getJsonData());

        long id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    public List<WeatherModel> getAllWeatherData() {
        List<WeatherModel> weatherList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + WeatherContract.WeatherEntry.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    WeatherModel weather = new WeatherModel();
                    weather.setId(cursor.getInt(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_ID)));
                    weather.setLocation(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_LOCATION)));
                    weather.setTimestamp(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_TIMESTAMP)));
                    weather.setTemperature(cursor.getDouble(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_TEMPERATURE)));
                    weather.setDescription(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_DESCRIPTION)));
                    weather.setIcon(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_ICON)));
                    weather.setHumidity(cursor.getInt(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));
                    weather.setWindSpeed(cursor.getDouble(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)));
                    weather.setPressure(cursor.getInt(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_PRESSURE)));
                    weather.setFeelsLike(cursor.getDouble(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_FEELS_LIKE)));
                    weather.setJsonData(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_JSON_DATA)));

                    weatherList.add(weather);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Error reading database column: " + e.getMessage());
                    // Continue with next record instead of crashing
                }
            } while (cursor.moveToNext());

            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return weatherList;
    }

    public List<String> getSavedLocations() {
        List<String> locations = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT " + WeatherContract.WeatherEntry.COLUMN_LOCATION +
                " FROM " + WeatherContract.WeatherEntry.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    locations.add(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_LOCATION)));
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Error reading location column: " + e.getMessage());
                }
            } while (cursor.moveToNext());

            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return locations;
    }

    public List<WeatherModel> getWeatherByLocation(String location) {
        List<WeatherModel> weatherList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(WeatherContract.WeatherEntry.TABLE_NAME,
                null,
                WeatherContract.WeatherEntry.COLUMN_LOCATION + "=?",
                new String[]{location},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                try {
                    WeatherModel weather = new WeatherModel();
                    weather.setId(cursor.getInt(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_ID)));
                    weather.setLocation(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_LOCATION)));
                    weather.setTimestamp(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_TIMESTAMP)));
                    weather.setTemperature(cursor.getDouble(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_TEMPERATURE)));
                    weather.setDescription(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_DESCRIPTION)));
                    weather.setIcon(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_ICON)));
                    weather.setHumidity(cursor.getInt(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_HUMIDITY)));
                    weather.setWindSpeed(cursor.getDouble(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_WIND_SPEED)));
                    weather.setPressure(cursor.getInt(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_PRESSURE)));
                    weather.setFeelsLike(cursor.getDouble(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_FEELS_LIKE)));
                    weather.setJsonData(cursor.getString(getColumnIndexSafe(cursor, WeatherContract.WeatherEntry.COLUMN_JSON_DATA)));

                    weatherList.add(weather);
                } catch (IllegalArgumentException e) {
                    Log.e(TAG, "Error reading database column: " + e.getMessage());
                    // Continue with next record instead of crashing
                }
            } while (cursor.moveToNext());

            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return weatherList;
    }
    public void saveLocation(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        db.insertWithOnConflict(TABLE_LOCATIONS, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.close();
    }


//    public boolean isTableValid() {
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("PRAGMA table_info(" + WeatherContract.WeatherEntry.TABLE_NAME + ")", null);
//
//        if (cursor != null) {
//            List<String> columns = new ArrayList<>();
//            while (cursor.moveToNext()) {
//                columns.add(cursor.getString(cursor.getColumnIndex("name")));
//            }
//            cursor.close();
//
//            // Check if all required columns exist
//            String[] requiredColumns = {
//                    WeatherContract.WeatherEntry.COLUMN_ID,
//                    WeatherContract.WeatherEntry.COLUMN_LOCATION,
//                    WeatherContract.WeatherEntry.COLUMN_TIMESTAMP,
//                    WeatherContract.WeatherEntry.COLUMN_TEMPERATURE,
//                    WeatherContract.WeatherEntry.COLUMN_DESCRIPTION,
//                    WeatherContract.WeatherEntry.COLUMN_ICON,
//                    WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
//                    WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
//                    WeatherContract.WeatherEntry.COLUMN_PRESSURE,
//                    WeatherContract.WeatherEntry.COLUMN_FEELS_LIKE,
//                    WeatherContract.WeatherEntry.COLUMN_JSON_DATA
//            };
//
//            for (String column : requiredColumns) {
//                if (!columns.contains(column)) {
//                    Log.e(TAG, "Missing column in database: " + column);
//                    return false;
//                }
//            }
//            return true;
//        }
//        return false;
//    }
    public int getDatabaseVersion() {
        return DATABASE_VERSION;
    }
}