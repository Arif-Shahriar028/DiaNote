package com.example.dianote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.Map;

public class MpAndroidChart extends AppCompatActivity {

    private LineChart lineChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mp_android_chart);

        lineChart = (LineChart) findViewById(R.id.lineChartId);

        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(), "Data set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        lineChart.setData(data);
        lineChart.invalidate();

    }

    private ArrayList<Entry> dataValues1()
    {
        ArrayList<Entry> dataValues = new ArrayList<Entry> ();
        dataValues.add(new Entry(0, 20));
        dataValues.add(new Entry(1, 24));
        dataValues.add(new Entry(2, 29));
        dataValues.add(new Entry(3, 25));
        dataValues.add(new Entry(4, 30));

        return dataValues;
    }
}