package com.example.googlemaps;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;
    ConnectionSQLiteHelper conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conn = new ConnectionSQLiteHelper(this,"db_map", null,1);

        if (isCorrectVersion()){
            init();
        }
    }

    /**
     * Initializes the button for the next activity.
     * Also, it checks whether the database is empty.
     * If it is empty, the csv data file is loaded into the database.
     */
    private void init(){

        Button btnFilter = (Button) findViewById(R.id.btnFilter);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(MainActivity.this, FilterActivity.class);
                startActivity(intent);
            }
        });

        SQLiteDatabase db = conn.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM markerplus", null);
        Boolean empty;

        if (cursor.moveToFirst())
        {
            empty = false;
            cursor.close();
        } else
        {
            empty = true;
        }

        if(empty){
            CSVToDatabase();
        }

    }


    /**
     * Checks whether the google play services is the correct version
     * @return
     */
    public boolean isCorrectVersion(){

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d(TAG, "Google Play works");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "Incorrect version");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * Reads the database file
     * @return
     */
    private String[] readCSVFile() {
        InputStream inputStream = getResources().openRawResource(R.raw.csvdata);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try{
            int i = inputStream.read();
            while (i !=-1){
                byteArrayOutputStream.write(i);
                i = inputStream.read();
            }
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return byteArrayOutputStream.toString().split("\n");
    }

    /**
     * Inserts the data from the csv file to the database
     */
    private void CSVToDatabase(){
        String[] text = readCSVFile();
        SQLiteDatabase db = conn.getWritableDatabase();
        db.beginTransaction();

        for (int i=0; i< text.length; i++){
            String[] linea = text[i].split(",");
            ContentValues contentValues = new ContentValues();
            contentValues.put("price", linea[0]);
            contentValues.put("postcode", linea[1]);
            contentValues.put("type", linea[2]);
            contentValues.put("age", linea[3]);
            contentValues.put("duration", linea[4]);
            contentValues.put("year", linea[5]);
            contentValues.put("latitude", linea[6]);
            contentValues.put("longitude", linea[7]);
            db.insert("markerplus", null, contentValues);
        }

        Toast.makeText(this, "File inserted" + text.length, Toast.LENGTH_LONG).show();
        db.setTransactionSuccessful();
        db.endTransaction();
    }


    private void deleteRows(){
        SQLiteDatabase db = conn.getWritableDatabase();
        db.execSQL("DELETE FROM markerplus WHERE price LIKE '%price%'");
        db.close();

    }

}
