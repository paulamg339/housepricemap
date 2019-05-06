package com.example.googlemaps;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.googlemaps.Entities.Postcode;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    BarChart barChart;
    Bundle statsBundle;

    String housetype;
    String buildtype;
    String durationtype;
    String postcode;

    TextView postcodetv;

    ConnectionSQLiteHelper conn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        statsBundle = this.getIntent().getExtras();
        housetype = statsBundle.getString("housetype");
        buildtype = statsBundle.getString("buildtype");
        durationtype = statsBundle.getString("durationtype");
        postcode = statsBundle.getString("postcode");

        postcodetv = (TextView) findViewById(R.id.postcode);
        barChart = (BarChart) findViewById(R.id.bargraph);

        conn = new ConnectionSQLiteHelper(this, "db_map", null, 1);
        loadData();

    }

    private void loadData() {

        postcodetv.setText(postcode);
        SQLiteDatabase db = conn.getReadableDatabase();

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        String sql = "";

        if(postcode.length() < 7){
            sql = "SELECT avg(price), substr(postcode,0,6), type FROM markerplus WHERE substr(postcode, 0, 6) = ? GROUP BY substr(postcode,0,6), type ORDER BY avg(price) DESC";
        } else{
            sql = "SELECT avg(price), postcode, type FROM markerplus WHERE postcode = ? GROUP BY postcode, type ORDER BY avg(price) DESC";
        }

        try {
            Cursor cursor = db.rawQuery(sql, new String[] {postcode});
            //Cursor cursor = db.rawQuery("SELECT avg(price), substr(postcode,0,6), type FROM markerplus WHERE substr(postcode, 0, 6) = ? GROUP BY substr(postcode,0,6), type ORDER BY avg(price) DESC", new String []{postcode});
            while (cursor.moveToNext()) {
                float avgprice = cursor.getFloat(0);
                String postcode = cursor.getString(1);
                String type = cursor.getString(2);
                int index = cursor.getPosition();

                entries.add(new BarEntry(index, avgprice));
                labels.add(type);

            }
            cursor.close();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "No access to database", Toast.LENGTH_LONG).show();
        }

        int d = labels.indexOf("D");
        int s = labels.indexOf("S");
        int t = labels.indexOf("T");
        int f = labels.indexOf("F");

        if (d > -1){
            labels.set(d, "Detached");
        }
        if (s > -1){
            labels.set(s, "Semi");
        }
        if (t > -1){
            labels.set(t, "Terrace");
        }
        if (f > -1){
            labels.set(f, "Flat");
        }

        BarDataSet set = new BarDataSet(entries,"Type of house");
        set.setColors(ColorTemplate.VORDIPLOM_COLORS);

        BarData data = new BarData(set);
        data.setBarWidth(0.9f);
        barChart.setData(data);
        barChart.setFitBars(true);
        barChart.invalidate();
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setTouchEnabled(false);
        barChart.setScaleEnabled(false);

    }

    private void example(){

        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 30f));
        entries.add(new BarEntry(1, 80f));
        entries.add(new BarEntry(2, 60f));
        entries.add(new BarEntry(3f, 50f));
        // gap of 2f
        entries.add(new BarEntry(5f, 70f));
        entries.add(new BarEntry(5f, 70f));
        entries.add(new BarEntry(6f, 60f));

        BarDataSet set = new BarDataSet(entries, "Dates");

        BarData data = new BarData(set);
        data.setBarWidth(0.9f); // set custom bar width
        barChart.setData(data);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars
        barChart.invalidate(); // refresh

    }


}




