package com.example.googlemaps.Utilities;

public class Utilities {

    //Constantes campos tabla marker

    public static final String TABLE_MARKER = "marker";
    public static final String FIELD_PRICE = "price";
    public static final String FIELD_LATITUDE = "latitude";
    public static final String FIELD_LONGITUDE = "longitude";
    public static final String CREATE_TABLE_MARKER="CREATE TABLE "+TABLE_MARKER+"" + " ("+FIELD_PRICE+" DOUBLE," + " "+FIELD_LATITUDE+" DECIMAL(10,8)," + " "+FIELD_LONGITUDE+" DECIMAL(10,8))";
}
