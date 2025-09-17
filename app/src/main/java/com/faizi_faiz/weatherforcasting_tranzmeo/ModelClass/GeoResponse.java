package com.faizi_faiz.weatherforcasting_tranzmeo.ModelClass;

public class GeoResponse {
    private String name;
    private LocalNames local_names;
    private double lat;
    private double lon;
    private String country;
    private String state;

    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLon() { return lon; }
    public String getCountry() { return country; }
    public String getState() { return state; }

    public static class LocalNames {
        private String en;
        public String getEn() { return en; }
    }
}
