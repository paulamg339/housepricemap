package com.example.googlemaps.Entities;

public class Postcode {

    String postcode;
    Double avgprice;
    Double latitude;
    Double longitude;

    public Postcode(String postcode, Double avgprice, Double latitude, Double longitude) {
        this.postcode = postcode;
        this.avgprice = avgprice;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Double getAvgprice() {
        return avgprice;
    }

    public void setAvgprice(Double avgprice) {
        this.avgprice = avgprice;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
