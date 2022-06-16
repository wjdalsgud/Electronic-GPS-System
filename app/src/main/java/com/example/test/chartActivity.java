package com.example.test;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class chartActivity extends AppCompatActivity {

    BarChart barchart;
    PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chart);

        barchart = findViewById(R.id.bar_chart);
        pieChart = findViewById(R.id.pie_chart);

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        for(int i=1; i<10; i++) {
            float value =(float) (i*10.0);

            BarEntry barEntry = new BarEntry(i,value);

            PieEntry pieEntry = new PieEntry(i,value);

            barEntries.add(barEntry);
            pieEntries.add(pieEntry);
        }

        BarDataSet barDataSet = new BarDataSet(barEntries , "Employees");

        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        barDataSet.setDrawValues(false);

        barchart.setData(new BarData(barDataSet));

        barchart.animateY(5000);

        barchart.getDescription().setText("Employee Chart");
        barchart.getDescription().setTextColor(Color.BLUE);

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Student");

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        pieChart.setData(new PieData(pieDataSet));

        pieChart.animateXY(5000,5000);

        pieChart.getDescription().setEnabled(false);
    }
}