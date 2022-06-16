package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import javax.annotation.Nullable;

//이승형 : 사용자가 찾고 싶은 문화시설 선택 구현

public class culture_find extends AppCompatActivity {
    public static String result = "도서관;편의점;카페;은행;";  // 결과를 출력할 문자열

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.culture_find);

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
                        intent = new Intent(getApplicationContext(), Collect.class);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });

        final CheckBox cb1 = (CheckBox) findViewById(R.id.checkBox1);
        final CheckBox cb2 = (CheckBox) findViewById(R.id.checkBox2);
        final CheckBox cb3 = (CheckBox) findViewById(R.id.checkBox3);
        final CheckBox cb4 = (CheckBox) findViewById(R.id.checkBox4);
        final CheckBox cb5 = (CheckBox) findViewById(R.id.checkBox5);
        final CheckBox cb15 = (CheckBox) findViewById(R.id.checkBox15);
        final CheckBox cb16 = (CheckBox) findViewById(R.id.checkBox16);
        final CheckBox cb17 = (CheckBox) findViewById(R.id.checkBox17);
        final CheckBox cb18 = (CheckBox) findViewById(R.id.checkBox18);
        final CheckBox cb14 = (CheckBox) findViewById(R.id.checkBox14);
        final CheckBox cb6 = (CheckBox) findViewById(R.id.checkBox6);
        final CheckBox cb13 = (CheckBox) findViewById(R.id.checkBox13);
        final CheckBox cb19 = (CheckBox) findViewById(R.id.checkBox19);
        final CheckBox cb20 = (CheckBox) findViewById(R.id.checkBox20);
        final CheckBox cb21 = (CheckBox) findViewById(R.id.checkBox21);
//findviewbyid 리스트로 구현

        EditText editText1 = (EditText) findViewById(R.id.editText1);
        String strText = "";

        Button b = (Button) findViewById(R.id.button1);
        final TextView tv = (TextView) findViewById(R.id.textView2);
        tv.setText("검색카테고리:" + result);


        b.setOnClickListener(new View.OnClickListener() {

                 @Override
                 public void onClick(View v) {
                     result = "";  // 결과를 출력할 문자열
                     if (cb1.isChecked() == true) result += cb1.getText().toString() + ';';
                     if (cb2.isChecked() == true) result += cb2.getText().toString() + ';';
                     if (cb3.isChecked() == true) result += cb3.getText().toString() + ';';
                     if (cb4.isChecked() == true) result += cb4.getText().toString() + ';';
                     if (cb5.isChecked() == true) result += cb5.getText().toString() + ';';
                     if (cb15.isChecked() == true) result += cb15.getText().toString() + ';';
                     if (cb16.isChecked() == true) result += cb16.getText().toString() + ';';
                     if (cb17.isChecked() == true) result += cb17.getText().toString() + ';';
                     if (cb18.isChecked() == true) result += cb18.getText().toString() + ';';
                     if (cb14.isChecked() == true) result += cb14.getText().toString() + ';';
                     if (cb6.isChecked() == true) result += cb6.getText().toString() + ';';
                     if (cb13.isChecked() == true) result += cb13.getText().toString() + ';';
                     if (cb19.isChecked() == true) result += cb19.getText().toString() + ';';
                     if (cb20.isChecked() == true) result += cb20.getText().toString() + ';';
                     if (cb21.isChecked() == true) result += cb21.getText().toString() + ';';
                     result += editText1.getText().toString();

                     tv.setText("검색카테고리:" + result);

                     Intent intent = new Intent(getApplicationContext(), MainPage.class);
                     startActivity(intent);
                 }
             }
        );
    }

    public String culture_find_result(){
        return result;
    }
}
