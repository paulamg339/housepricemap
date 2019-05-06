package com.example.googlemaps;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.googlemaps.Utilities.Utilities;

public class ConnectionSQLiteHelper extends SQLiteOpenHelper {

    public ConnectionSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table marker (price double primary key, latitude decimal(10.8), longitude decimal(10,8))");
        db.execSQL("create table markerplus (price double, postcode text, type text, age text, duration text,  year integer, latitude decimal(10.8), longitude decimal (10.8))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS marker");
        db.execSQL("DROP TABLE IF EXISTS markerplus");
        onCreate(db);
    }
}
