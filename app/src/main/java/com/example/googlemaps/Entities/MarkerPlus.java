package com.example.googlemaps.Entities;

public class MarkerPlus {

    private double price;
    private double latitude;
    private double longitude;

    private String type; //String or character
    private String age;
    private String duration;

    private String postcode;

    private int year;
    private int month;

    public MarkerPlus() {
    }

    public MarkerPlus(double price, double latitude, double longitude, String type, String age, String duration, String postcode, int year, int month) {
        this.price = price;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.age = age;
        this.duration = duration;
        this.postcode = postcode;
        this.year = year;
        this.month = month;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }
}
