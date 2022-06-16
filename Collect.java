package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Collect extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collect);

        Button AC_5= (Button)findViewById(R.id.AC_5);
        AC_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AC_5.class);
                startActivity(intent);
            }
        });
        Button AC_3= (Button)findViewById(R.id.AC_3);
        AC_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AC_3.class);
                startActivity(intent);
            }
        });
        Button DC_C= (Button)findViewById(R.id.DC_C);
        DC_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DC_C.class);
                startActivity(intent);
            }
        });
        Button DC_D= (Button)findViewById(R.id.DC_D);
        DC_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DC_D.class);
                startActivity(intent);
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
                        intent = new Intent(getApplicationContext(), chart_select.class);
                        startActivity(intent);
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
                        return true;

                }
                return false;
            }
        });

    }
}