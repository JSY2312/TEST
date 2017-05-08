package com.example.seeth.test;

public class MapPoint {
    private double latitude;
    private double longitude;

    public MapPoint(){
        super();
    }

    public MapPoint(double latitude, double longitude) {
        //super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
