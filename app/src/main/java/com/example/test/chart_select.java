package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

// 장기윤 : 급속과 완속의 그래프를 버튼에 따라서 보여줄수 있는 클래스 구현
//          fastActivity와 slowActivity를 합쳐서 한 화면에 보여줌
//          도넛그래프 대신 막대그래프를 채택함
public class chart_select extends Activity {

    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceStae) {
        super.onCreate(savedInstanceStae);
        setContentView(R.layout.chart_select);
        barChart = findViewById(R.id.barChart);
        TextView t1 = findViewById(R.id.t1);
        TextView t2 = findViewById(R.id.t2);
        TextView t3 = findViewById(R.id.t3);
        TextView t4 = findViewById(R.id.t4);
        TextView t5 = findViewById(R.id.t5);
        TextView t6 = findViewById(R.id.t6);
        TextView t7 = findViewById(R.id.t7);
        TextView t8 = findViewById(R.id.t8);

        ArrayList<Integer> data = new ArrayList<>();
        ArrayList<String> name = new ArrayList<>();

        data.add(240);
        data.add(279);
        data.add(292);
        data.add(309);
        data.add(309);
        data.add(309);
        data.add(309);
        data.add(327);

        name.add("LG");
        name.add("GS");
        name.add("휴맥스");
        name.add("환경부");
        name.add("현대오일뱅크");
        name.add("SK");
        name.add("한국전력");
        name.add("테슬라");

        t1.setText("LG");
        t2.setText("GS");
        t3.setText("휴맥스");
        t4.setText("환경부");
        t5.setText("현대오일뱅크");
        t6.setText("SK");
        t7.setText("한국전력");
        t8.setText("테슬라");

        BarChartGraph(data, name);
        barChart.setTouchEnabled(false);

        Button fast_button = findViewById(R.id.fast_Button);
        Button slow_button = findViewById(R.id.slow_Button);
        fast_button.setBackgroundColor(Color.parseColor("#5A6773"));
        fast_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fast_button.setBackgroundColor(Color.parseColor("#5A6773"));
                slow_button.setBackgroundColor(Color.parseColor("#778899"));
                data.clear();
                data.add(240);
                data.add(279);
                data.add(292);
                data.add(309);
                data.add(309);
                data.add(309);
                data.add(309);
                data.add(327);

                name.clear();
                name.add("LG");
                name.add("GS");
                name.add("휴맥스");
                name.add("환경부");
                name.add("현대오일뱅크");
                name.add("SK");
                name.add("한국전력");
                name.add("테슬라");

                t1.setText("LG");
                t2.setText("GS");
                t3.setText("휴맥스");
                t4.setText("환경부");
                t5.setText("현대오일뱅크");
                t6.setText("SK");
                t7.setText("한국전력");
                t8.setText("테슬라");

                BarChartGraph(data, name);
                barChart.setTouchEnabled(false);
            }
        });
        slow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slow_button.setBackgroundColor(Color.parseColor("#5A6773"));
                fast_button.setBackgroundColor(Color.parseColor("#778899"));
                data.clear();
                data.add(240);
                data.add(253);
                data.add(255);
                data.add(259);
                data.add(275);
                data.add(292);
                data.add(292);
                data.add(309);

                name.clear();
                name.add("LG");
                name.add("휴맥스");
                name.add("한국전력");
                name.add("GS");
                name.add("테슬라");
                name.add("환경부");
                name.add("현대오일뱅크");
                name.add("SK");

                t1.setText("LG");
                t2.setText("휴맥스");
                t3.setText("한국전력");
                t4.setText("GS");
                t5.setText("테슬라");
                t6.setText("환경부");
                t7.setText("현대오일뱅크");
                t8.setText("SK");

                BarChartGraph(data, name);
                barChart.setTouchEnabled(false);
            }
        });
        BottomNavigationView bottom = findViewById(R.id.bottom_menu);
        bottom.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()) {
                    case R.id.find_culture:
                        intent = new Intent(getApplicationContext(), culture_find.class);
                        startActivity(intent);
                        return true;
                    case R.id.chart:
                        return true;
                    case R.id.navigation:
                        intent = new Intent(getApplicationContext(), RoadSearch.class);
                        startActivity(intent);
                        return true;
                    case R.id.read:
                        intent = new Intent(getApplicationContext(),info.class);
                        startActivity(intent);
                        return true;
                    case R.id.menu:
                        intent = new Intent(getApplicationContext(), Collect.class);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });
    }
    private void BarChartGraph(ArrayList<Integer> valList, ArrayList<String> label) {
        // BarChart 메소드


        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < valList.size(); i++) {
            entries.add(new BarEntry((i + 1), (Integer) valList.get(i), label));
        }

        BarDataSet depenses = new BarDataSet(entries,""); // 변수로 받아서 넣어줘도 됨
        BarData data = new BarData(depenses); // 라이브러리 v3.x 사용하면 에러 발생함
        data.setValueTextSize(13);

        depenses.setColors(ColorTemplate.COLORFUL_COLORS); //

        barChart.setData(data);
        barChart.animateXY(1000, 1000);
        barChart.invalidate();

    }
}

