package com.example.googlemaps;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.googlemaps.Entities.MarkerPlus;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FilterActivity extends AppCompatActivity {

    RadioButton btnFlat, btnTerrace, btnSemi, btnDetached,
            btnOld, btnNew, btnLeasehold, btnFreehold, btnAllType, btnAllType2, btnAllType3;
    EditText mSearchText;
    String searchString;
    Button btnGooglemap;
    SeekBar seekbarYear;
    TextView txtYear;

    ConnectionSQLiteHelper conn;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        conn = new ConnectionSQLiteHelper(getApplicationContext(),"db_map", null, 1);

        btnFlat = (RadioButton) findViewById(R.id.btnFlat);
        btnTerrace = (RadioButton) findViewById(R.id.btnTerrace);
        btnSemi = (RadioButton) findViewById(R.id.btnSemi);
        btnDetached = (RadioButton) findViewById(R.id.btnDetached);

        btnOld = (RadioButton) findViewById(R.id.btnOld);
        btnNew = (RadioButton) findViewById(R.id.btnNew);

        btnLeasehold = (RadioButton) findViewById(R.id.btnLeasehold);
        btnFreehold = (RadioButton) findViewById(R.id.btnFreehold);

        //toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSearchText = (EditText) findViewById(R.id.input_search);

        seekbarYear = (SeekBar) findViewById(R.id.yearRange);
        txtYear = (TextView) findViewById(R.id.year);


        seekbarYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtYear.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        initButtons();


    }


    /**
     * Checks if any of the checkboxes is checked.
     * If any checkbox is checked, it stores what kind of house is selected.
     * @return String with the initial of the type of house
     */
    private String checkHouseType(){
        String houseTypeChar = "";

        if(btnFlat.isChecked()){
            houseTypeChar="F";
        } else if(btnTerrace.isChecked()){
            houseTypeChar="T";
        } else if(btnSemi.isChecked()){
            houseTypeChar="S";
        } else if(btnDetached.isChecked()){
            houseTypeChar="D";
        }

        return houseTypeChar;
    }

    /**
     * Checks if any of the checkboxes is checked.
     * If any checkbox is checked, it stores what kind of house is selected.
     * @return String with the initial of the type of build
     */
    private String checkBuildType(){
        String buildTypeChar = "";

        if(btnOld.isChecked()){
            buildTypeChar="N";
        } else if(btnNew.isChecked()){
            buildTypeChar="Y";
        }

        return buildTypeChar;
    }

    /**
     * Checks if any of the checkboxes is checked.
     * If any checkbox is checked, it stores what kind of house is selected.
     * @return String with the initial of the type of tenure of the house
     */
    private String checkDurationType(){
        String durationTypeChar = "";

        if(btnLeasehold.isChecked()){
            durationTypeChar="L";
        } else if(btnFreehold.isChecked()){
            durationTypeChar="F";
        }
        //add checks for when nothing is selected
        return durationTypeChar;
    }

    /**
     * Gets the location from the search box
     * @return name of the location or null
     */
    private String getLocation(){

        searchString = mSearchText.getText().toString();
        Geocoder geocoder = new Geocoder(FilterActivity.this);
        List<Address> list = new ArrayList<>();

        try {
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException io) {
            Log.d("geolocating", "Geolocating error");
        }

        if (list.size() > 0) {
            Address address = list.get(0);
            return searchString;
        } else{
            return null;
        }
    }


    /**
     * Initialize button and checks whether the location entered is valid,
     * if it is not valid, it displays a text in red saying that the location is not valid.
     * Also, it puts the input of the user in a bundle for the next activity.
     *
     */
    private void initButtons(){

        btnGooglemap = (Button) findViewById(R.id.btnGooglemap);
        btnGooglemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                //searchString = mSearchText.getText().toString();
                Bundle bundle = new Bundle();
                Intent intentMap = new Intent(FilterActivity.this, MapActivity.class);

                String location = getLocation();
                if(location == null){
                    mSearchText.setText("");
                    mSearchText.setHintTextColor(Color.RED);
                    mSearchText.setHint("Location entered not valid");
                } else{
                    String houseTypeChar = checkHouseType();
                    String buildTypeChar = checkBuildType();
                    String durationTypeChar = checkDurationType();
                    bundle.putString("housetype", houseTypeChar);
                    bundle.putString("buildtype", buildTypeChar);
                    bundle.putString("durationtype", durationTypeChar);
                    bundle.putString("searchlocation", location);
                    intentMap.putExtras(bundle);
                    startActivity(intentMap);
                }
            }
        });
    }




}
