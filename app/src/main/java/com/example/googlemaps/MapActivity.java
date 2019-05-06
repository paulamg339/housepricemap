package com.example.googlemaps;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnCameraMoveStartedListener;
import com.google.android.gms.maps.GoogleMap.OnCameraIdleListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.ZoomControls;

import com.example.googlemaps.Entities.HeatmapPoint;
import com.example.googlemaps.Entities.MarkerPlus;
import com.example.googlemaps.Entities.Postcode;
import com.example.googlemaps.Utilities.Utilities;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.geometry.Point;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.google.maps.android.quadtree.PointQuadTree;
import com.google.maps.android.ui.IconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback,
        OnInfoWindowClickListener, OnCameraIdleListener, OnCameraMoveStartedListener {

    private static final String TAG = "MapActivity";
    private GoogleMap mMap;

    float initialzoom;
    LatLng initialposition;

    boolean moveStarted;

    ConnectionSQLiteHelper conn;

    TextView legend, minIntensity, maxIntensity;

    Bundle bundle;

    LatLngBounds bounds;

    List<Postcode> postcodeUnit;
    List<Postcode> postcodeSector;

    IconGenerator iconFactory;

    boolean markClicked;

    List<Double> dobles;
    Marker m;
    String type;
    String build;
    String duration;


    TextView locationText;

    TextView resultsNotFound;
    TextView optionsDisplay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_map);

        legend = (TextView) findViewById(R.id.legend);
        minIntensity = (TextView) findViewById(R.id.minIntensity);
        maxIntensity = (TextView) findViewById(R.id.maxIntensity);

        locationText = (TextView) findViewById(R.id.locationText);
        optionsDisplay = (TextView) findViewById(R.id.displayOptions);

        resultsNotFound = (TextView) findViewById(R.id.resultsNotFound);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        conn = new ConnectionSQLiteHelper(this, "db_map", null, 1);
        iconFactory = new IconGenerator(this);

        dobles = new ArrayList<>();

        double min = 0.0;
        double max = 0.0;

    }

    /**
     * Sets the initial map properties, like zoom minimum and maximum levels.
     * It sets the style of the map.
     * It sets the camera listeners.
     * It calls the methods getbundles() and location()
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        //Initial map properties
        mMap = googleMap;
        mMap.setMinZoomPreference(8.0f);
        mMap.setMaxZoomPreference(17.0f);

        //Set On Camera
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraIdleListener(this);
        mMap.setOnInfoWindowClickListener(this);

        //Style
        try{
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json
                    ));

            if(!success){
                Log.e("tag", "Style parsing failed");
            }

        } catch (Resources.NotFoundException e){
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        getBundleExtras();
        getLocation();
    }

    /**
     * Gets the strings from the last activity.
     * The strings correspond to the input entered by the user.
     */
    private void getBundleExtras() {
        bundle = this.getIntent().getExtras();
        type = bundle.getString("housetype");
        build = bundle.getString("buildtype");
        duration = bundle.getString("durationtype");
    }

    /**
     * Gets the location that was entered by the user
     * and uses the geocoder to find it.
     * If the address is a postal code, the zoom level is increased.
     * The location name is set at the top of the screen.
     */
    private void getLocation() {

        String location = bundle.getString("searchlocation");
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(location, 1);
        } catch (IOException io) {
            Log.e(TAG, "Geolocate: IOException" + io.getMessage());
        }

        if (list.size() > 0) {
            Address address = list.get(0);
                if (address.getPostalCode() != null) {
                    moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 17.0f);
                    String locationtxt = address.getPostalCode();
                    locationText.setText(locationtxt);
                } else {
                    moveCamera(new LatLng(address.getLatitude(), address.getLongitude()), 14.0f);
                    String locationtxt = address.getLocality();
                    locationText.setText(locationtxt);
                }
            } else{
                Toast.makeText(this, "Location not in database", Toast.LENGTH_SHORT).show();
            }
        }

    /**
     * Move the camera to the position of the location
     * It loads the postcode information
     * It calls the methods of displaying the markers depending on the zoom level.
     * @param point
     * @param zoom
     */
    private void moveCamera(LatLng point, float zoom) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));
        bounds = mMap.getProjection().getVisibleRegion().latLngBounds;

        initialzoom = mMap.getCameraPosition().zoom;
        initialposition = mMap.getCameraPosition().target;

        loadPostcodeSector();
        loadPostcodeUnit();
        if(initialzoom < 15.25f){
            displayPostcodeSector(bounds);
        } else{
            displayPostcodeUnit(bounds);
        }
    }

    /**
     *It loads the postcode markers from the database
     */
    private void loadPostcodeSector(){
        SQLiteDatabase db = conn.getReadableDatabase();

        postcodeSector = new ArrayList<>();

        try {
            //Cursor cursor = db.rawQuery("SELECT avg(price) AS average, substr(postcode, 0, 6), latitude, longitude FROM markerplus WHERE type = ? AND age = ? AND duration = ? GROUP BY substr(postcode, 0, 6)", new String[]{type, build, duration});
            Cursor cursor = db.rawQuery("SELECT avg(price) AS average, substr(postcode, 0, 6), latitude, longitude FROM markerplus GROUP BY substr(postcode, 0, 6)", null);
            while (cursor.moveToNext()) {
                double avgprice = cursor.getDouble(0);
                String postcode = cursor.getString(1);
                double lat = cursor.getDouble(2);
                double lng = cursor.getDouble(3);

                postcodeSector.add(new Postcode(postcode, avgprice, lat, lng));

            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No access to database", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * It loads the postcode markers from the database.
     */
    private void loadPostcodeUnit(){
        SQLiteDatabase db = conn.getReadableDatabase();

        postcodeUnit = new ArrayList<>();

        try {
            //Cursor cursor = db.rawQuery("SELECT avg(price) AS average, postcode, latitude, longitude FROM markerplus WHERE type = ? AND age = ? AND duration = ? GROUP BY postcode", new String[]{type, build, duration});
            Cursor cursor = db.rawQuery("SELECT avg(price) AS average, postcode, latitude, longitude FROM markerplus GROUP BY postcode", null);
            while (cursor.moveToNext()) {
                double avgprice = cursor.getDouble(0);
                String postcode = cursor.getString(1);
                double lat = cursor.getDouble(2);
                double lng = cursor.getDouble(3);

                postcodeUnit.add(new Postcode(postcode, avgprice, lat, lng));
            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No access to database", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * It takes the latitude an dlongitude from the postcode objects and checks
     * whether those coordinates are inside the map boundaries.
     * It also sets the marker information with the price and the snippet
     * with the postcode name if the marker is clicked.
     * It makes an arraylist of prices from the postcodes that are
     * within the map boundaries and gets the minimum price and the maximum price
     * to set the colour legend and also for later then scale the values.
     * @param bounds
     */
    private void displayPostcodeSector(LatLngBounds bounds) {

        double currentZoom = mMap.getCameraPosition().zoom;

        List<Postcode> markersToDisplay = new ArrayList<>();
        List<Double> doubles = new ArrayList<>();
        for (Postcode p : postcodeSector){
            LatLng ll = new LatLng(p.getLatitude(),p.getLongitude());

            if(bounds.contains(ll)){

                double dprice = p.getAvgprice();
                dobles.add(dprice);
                String sprice = "";
                if (dprice > 1000000){
                    sprice = "£" + String.format("%.2f", (dprice/ 1000000)) + "M";
                } else{
                    int iprice = (int) dprice;
                    sprice = "£" + String.valueOf(iprice / 1000) + "K";
                }
                MarkerOptions options = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(sprice)))
                        .position(ll)
                        .title(p.getPostcode())
                        .snippet("Display stats")
                        .anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
                m = mMap.addMarker(options);

                if (currentZoom < 14.0){
                    m.setVisible(false);
                }

                doubles.add(p.getAvgprice());
                markersToDisplay.add(p);
            }
        }

        double minimum = 0.0;
        double maximum = 0.0;
        if (doubles.size() > 0){
            minimum = Collections.min(doubles);
            maximum = Collections.max(doubles);
        }

        if(markersToDisplay.size() > 0){
            resultsNotFound.setText("");
            for (Postcode t : markersToDisplay){
                double normvalue = normalize(t.getAvgprice(), minimum, maximum);
                LatLng position = new LatLng(t.getLatitude(), t.getLongitude());
                createCircleOptions(position, normvalue, true);
            }

            addColorLegend(minimum, maximum);
        }else{
            resultsNotFound.setText("Zoom in to see info");
            minIntensity.setText("");
            maxIntensity.setText("");
            legend.setVisibility(View.INVISIBLE);
        }


    }

    /**
     * It takes the latitude an dlongitude from the postcode objects and checks
     * whether those coordinates are inside the map boundaries.
     * It also sets the marker information with the price and the snippet
     * with the postcode name if the marker is clicked.
     * It makes an arraylist of prices from the postcodes that are
     * within the map boundaries and gets the minimum price and the maximum price
     * to set the colour legend and also for later then scale the values.
     * @param bounds
     */
    private void displayPostcodeUnit(LatLngBounds bounds){

        List<Postcode> markersToDisplay = new ArrayList<>();
        List<Double> doubles = new ArrayList<>();
        double currentZoom = mMap.getCameraPosition().zoom;
        for (Postcode p : postcodeUnit){
            LatLng ll = new LatLng(p.getLatitude(),p.getLongitude());
            if(bounds.contains(ll)){
                double dprice = p.getAvgprice();
                String sprice = "";
                if (dprice > 1000000){
                    sprice = "£" + String.valueOf(dprice / 1000000) + "M";
                } else{
                    int iprice = (int) dprice;
                    sprice = "£" + String.valueOf(iprice / 1000) + "K";
                }
                MarkerOptions options = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(sprice)))
                        .position(ll)
                        .title(p.getPostcode())
                        .snippet("Display stats");
                m = mMap.addMarker(options);
                if (currentZoom < 16.75){
                    m.setVisible(false);
                }
                doubles.add(p.getAvgprice());
                markersToDisplay.add(p);
            }
        }

        double minimum = 0.0;
        double maximum = 0.0;
        if(doubles.size() > 0){
            minimum = Collections.min(doubles);
            maximum = Collections.max(doubles);
        }


        if(markersToDisplay.size() > 0){
            resultsNotFound.setText("");
            for (Postcode t : markersToDisplay){
                double normvalue = normalize(t.getAvgprice(), minimum, maximum);
                LatLng position = new LatLng(t.getLatitude(), t.getLongitude());
                createCircleOptions(position, normvalue, false);
            }
            addColorLegend(minimum, maximum);
        } else{
            resultsNotFound.setText("No results found for this area");
            minIntensity.setText("");
            maxIntensity.setText("");
            legend.setVisibility(View.INVISIBLE);
        }

    }


    /**
     * Scale the values from 0 to 1.
     * @param value
     * @param minimum
     * @param maximum
     * @return
     */
    private double normalize(double value, double minimum, double maximum){
        return (value - maximum) / (minimum - maximum);
    }

    /**
     * It sets the circle options.
     * The radius level is set depending on whether the postcode is a postcode sector
     * or if it is a postcode unit.
     * The colour is determined by the value of the price multiplied by the value 120f,
     * the result is the colour hue.
     * @param ll Latitude and longitude
     * @param price
     * @param sector boolean sector
     */
    private void createCircleOptions(LatLng ll, double price, boolean sector){
        int radiusLevel = 75;
        if(sector){
            radiusLevel = 200;
        } else{
            radiusLevel = 25;
        }


        float x = (float) price;
        int cl = Color.HSVToColor(90, new float[]{(float)x*120f, 1f, 1f});

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(ll)
                .radius(radiusLevel)
                .strokeColor(Color.TRANSPARENT)
                .fillColor(cl));
    }


    /**
     * It changes the screen to the statistics activity
     * @param marker
     */
    @Override
    public void onInfoWindowClick(Marker marker) {
        markClicked = true;
        String postcode = marker.getTitle();
        Intent intentStats = new Intent(MapActivity.this, StatsActivity.class);
        Bundle statsBundle = new Bundle();
        statsBundle.putString("housetype", type);
        statsBundle.putString("buildtype", build);
        statsBundle.putString("durationtype", duration);
        statsBundle.putString("postcode", postcode);
        intentStats.putExtras(statsBundle);
        startActivity(intentStats);

    }


    /**
     * Add the minimum price and maximum price within the map boundaries
     * @param minvalue
     * @param maxvalue
     */
    private void addColorLegend(double minvalue, double maxvalue){

        int mn = (int) minvalue;
        int mx = (int) maxvalue;
        String legendmin = "£" + String.valueOf(mn / 1000) + "K";
        String legendmax = "£" + String.valueOf(mx / 1000) + "K";
        minIntensity.setText(legendmin);
        maxIntensity.setText(legendmax);
    }

    /**
     * It sets the boolean moveStarted to true or false depending on whether
     * the screen was moved by the user or for other reason.
     * @param reason
     */
    @Override
    public void onCameraMoveStarted(int reason){

        if (reason == OnCameraMoveStartedListener.REASON_GESTURE) {
            moveStarted = true;
        } else if(reason == OnCameraMoveStartedListener.REASON_API_ANIMATION){
            moveStarted = false;
        } else if(reason == OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION){
            moveStarted = false;
        }
    }

    /**
     * If the screen has stopped moving. It gets the new position
     * and the new zoom. It calls the method onCameraChange with these new values.
     */
    @Override
    public void onCameraIdle() {
        Log.d("Camera", "The camera has stopped moving.");
        LatLng newpos = mMap.getCameraPosition().target;
        float newz = mMap.getCameraPosition().zoom;
        onCameraChange(newz, newpos);
    }

    /**
     * If the screen has been moved by the user and the new zoom and new position
     * does not equal to the initial position, the initial zoom and new position are set with
     * these new values. The geolocator is used to display the string on where the map is situated.
     * Then the map is clear and the methods for displaying the postcodes are called depending on
     * the zoom level.
     * @param newzoom
     * @param newpos
     */
    private void onCameraChange(float newzoom, LatLng newpos){

        if(moveStarted){
            if ((newzoom != initialzoom) | (newpos !=initialposition)){
                initialzoom = newzoom;
                initialposition = newpos;
                LatLngBounds newbounds = mMap.getProjection().getVisibleRegion().latLngBounds;

                Geocoder geo = new Geocoder(this);
                List<Address> list = new ArrayList<>();
                try {
                    list = geo.getFromLocation(newpos.latitude, newpos.longitude, 1);
                } catch (IOException io) {
                    Log.e(TAG, "Geolocate: IOException" + io.getMessage());
                }
                if (list.size() > 0) {
                    Address address = list.get(0);
                    String locationtxt = address.getLocality();
                    locationText.setText(locationtxt);
                } else{
                    Log.d("Location", "No location available");
                }

                if(mMap != null){
                    mMap.clear();
                    if(newzoom < 15.25){
                        displayPostcodeSector(newbounds);
                    } else{
                        displayPostcodeUnit(newbounds);
                    }
                }
            }
        } else{
            Log.d("Camera", "Camera not started");
        }
    }
    }


