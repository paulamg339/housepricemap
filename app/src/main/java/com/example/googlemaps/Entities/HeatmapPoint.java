package com.example.googlemaps.Entities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ArrayAdapter;

import com.example.googlemaps.ConnectionSQLiteHelper;

import java.util.ArrayList;

public class HeatmapPoint {

    private Double price;
    private Double latitude;
    private Double longitude;

    public HeatmapPoint() {
    }

    public HeatmapPoint(Double price, Double latitude, Double longitude) {
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getPrice() {
        return price;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


}
