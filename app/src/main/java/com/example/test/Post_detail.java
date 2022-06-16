package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

//이승형 : 게시판 클릭시 상세조회 구현
public class Post_detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);


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
                        intent = new Intent(getApplicationContext(), chart_select.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation:
                        intent = new Intent(getApplicationContext(), RoadSearch.class);
                        startActivity(intent);
                        return true;
                    case R.id.read:
                        intent = new Intent(getApplicationContext(), info.class);
                        startActivity(intent);
                    case R.id.menu:
                        intent = new Intent(getApplicationContext(), Collect.class);
                        startActivity(intent);
                        return true;
                }
                return false;
            }
        });

        Intent intent = getIntent();
        String name = intent.getExtras().getString("name");
        String time = intent.getExtras().getString("time");
        String title = intent.getExtras().getString("title");
        String content = intent.getExtras().getString("content");

        TextView titleV = (TextView)findViewById(R.id.title_tv);
        titleV.setText(title);
        TextView nameV = (TextView)findViewById(R.id.writer_tv);
        nameV.setText(name);
        TextView dateV = (TextView)findViewById(R.id.date_tv);
        dateV.setText(time);
        TextView contentV = (TextView)findViewById(R.id.content_tv);
        contentV.setText(content);

    }
}