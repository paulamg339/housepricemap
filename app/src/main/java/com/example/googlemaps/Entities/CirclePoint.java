package com.example.googlemaps.Entities;

import com.google.android.gms.maps.model.LatLng;

public class CirclePoint {

    double price;
    LatLng latlng;

    public CirclePoint(double price, LatLng latlng) {
        this.price = price;
        this.latlng = latlng;
    }

    public double getPrice() {
        return price;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}
