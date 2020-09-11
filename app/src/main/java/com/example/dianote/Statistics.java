package com.example.dianote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;

public class Statistics extends AppCompatActivity {

    private LineChart lineChart;

    private TextView totalChecked, beforeEatChecked, afterEatChecked;
    private TextView lowestPoint, lowestDate, lowestEatTime;
    private TextView highestPoint, highestDate, highestEatTime;
    private TextView avgPoint, beforeAvg, afterAvg;


    DatabaseReference databaseReference;
    private DatabaseReference userRef;
    private StorageReference userPicRef;
    private FirebaseAuth mAuth;
    String currentUserId, downloadImageUrl;
    int i = 0, beforeEat = 0, afterEat = 0;

    LineDataSet lineDataSet1 = new LineDataSet(null, null);
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    LineData lineData;

    String totalCheckedString, beforeEatCheckedString, afterEatCheckedString;
    String maxDate, maxEatTime, minDate, minEatTime;
    String maxPoint, minPoint, avgPointString, avgBeforeString, avgAfterString;
    double max = Integer.MIN_VALUE;
    double min = Integer.MAX_VALUE;
    double sum = 0, avg, avgBefore, avgAfter, sumBefore = 0, sumAfter = 0;

    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        totalChecked = (TextView) findViewById(R.id.totalCheckedId);
        beforeEatChecked = (TextView) findViewById(R.id.checkedBeforeEatId);
        afterEatChecked = (TextView) findViewById(R.id.checkedAfterEatId);
        lowestPoint = (TextView) findViewById(R.id.lowestPointId);
        lowestDate = (TextView) findViewById(R.id.lowestDateId);
        lowestEatTime = (TextView) findViewById(R.id.lowestEatStateId);
        highestPoint = (TextView) findViewById(R.id.highestPointId);
        highestDate = (TextView) findViewById(R.id.highestDateId);
        highestEatTime = (TextView) findViewById(R.id.highestEatStateId);
        avgPoint = (TextView) findViewById(R.id.averagePointId);
        beforeAvg = (TextView) findViewById(R.id.beforeAvgId);
        afterAvg = (TextView) findViewById(R.id.afterAvgId);

        lineChart = (LineChart) findViewById(R.id.lineChartId2);
        lineChart.setDrawBorders(true);
        lineChart.setBorderColor(Color.BLACK);
        lineChart.setBorderWidth(3);

        mAuth = FirebaseAuth.getInstance();

        connected = internetConnectionCheck();

        if(connected == true)
            retrieveData();
        else
            Toast.makeText(Statistics.this, "No internet", Toast.LENGTH_SHORT).show();
    }

    public void retrieveData()
    {
        currentUserId = mAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserData").child(currentUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {  // fetching data from firebase and add to listView
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> dataValues = new ArrayList<Entry> ();

                Log.e("Count " ,""+snapshot.getChildrenCount());
                // get all sub children from children
                for(DataSnapshot dataSnapshot : snapshot.getChildren())
                {
                    DataDetails dataDetails = dataSnapshot.getValue(DataDetails.class);
                    String point = dataDetails.getPoints();
                    double pointValue = Double.parseDouble(point);
                    dataValues.add(new Entry(i, (float) pointValue));
                    i++;

                    if(pointValue < min)
                    {
                        min = pointValue;
                        minDate = dataDetails.getDate();
                        minEatTime = dataDetails.getEatTime();
                    }
                    if(pointValue > max)
                    {
                        max = pointValue;
                        maxDate = dataDetails.getDate();
                        maxEatTime = dataDetails.getEatTime();
                    }

                    sum = sum + pointValue;

                    if(dataDetails.getEatTime().matches("Before eat"))
                    {
                        avgBefore = avgBefore + pointValue;
                        beforeEat++;
                    }

                    if(dataDetails.getEatTime().matches("After eat"))
                    {
                        avgAfter = avgAfter + pointValue;
                        afterEat++;
                    }

                }

                avg = (sum)/(i);
                avgBefore = (avgBefore)/beforeEat;
                avgAfter = (avgAfter)/afterEat;

                totalCheckedString = Integer.toString(i);
                beforeEatCheckedString = Integer.toString(beforeEat);
                afterEatCheckedString = Integer.toString(afterEat);
                maxPoint = Double.toString(max);
                minPoint = Double.toString(min);
                avgPointString = String.format("%.2f", avg); // to show to digits after point and transmit to string
                avgBeforeString = String.format("%.2f", avgBefore); // to show to digits after point and transmit to string
                avgAfterString = String.format("%.2f", avgAfter); // to show to digits after point and transmit to string

                totalChecked.setText(totalCheckedString);
                beforeEatChecked.setText(beforeEatCheckedString);
                afterEatChecked.setText(afterEatCheckedString);
                beforeAvg.setText("Avg : "+  avgBeforeString);
                afterAvg.setText("Avg : "+  avgAfterString);

                if(!minPoint.matches("2.147483647E9"))
                    lowestPoint.setText(minPoint);
                lowestDate.setText(minDate);
                lowestEatTime.setText(minEatTime);

                if(!maxPoint.matches("-2.147483648E9"))
                    highestPoint.setText(maxPoint);
                highestDate.setText(maxDate);
                highestEatTime.setText(maxEatTime);

                avgPoint.setText(avgPointString);

                showGraph(dataValues);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void showGraph(ArrayList<Entry> dataValues)
    {
        lineDataSet1.setValues(dataValues);
        lineDataSet1.setLabel("Diabetes Graph");
        lineDataSet1.setLineWidth(3);
        lineDataSet1.setValueTextSize(10);
        dataSets.clear();
        dataSets.add(lineDataSet1);
        lineData = new LineData(dataSets);
        lineChart.clear();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }

    public boolean internetConnectionCheck()
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }
}