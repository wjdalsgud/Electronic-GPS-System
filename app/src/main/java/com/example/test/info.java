package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



// 정민형 : 사용자가 글을 쓴 게시판 정보를 확인할 수 있는 게시판 구현 read() 메소드 구현
// 이승형 : 리스트에서 제목만 나오게, 클릭시 상세페이지로 이동 구현
public class info extends AppCompatActivity {

    long mNow;
    Date mDate;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");

    TextView txtResult;

    private FirebaseUser firebaseUser;

    private static final String TAG = "info";

    ListView listView;
    ArrayList<String> listItem;
    ArrayAdapter<String> adapter;

    private String getTime() {
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bulletinboard_activity);


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
                        return true;
                    case R.id.menu:
                        intent = new Intent(getApplicationContext(), Collect.class);
                        startActivity(intent);
                        return true;

                }
                return false;
            }
        });

        db = FirebaseFirestore.getInstance();
        listItem = new ArrayList<String>();


        ListView listView = (ListView) findViewById(R.id.listview1);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItem);
        listView.setAdapter(adapter);
        read();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object vo = (Object)parent.getAdapter().getItem(position);
                String contnet;
                db.collection("post").whereEqualTo("title",vo).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                Toast.makeText(info.this, document.getData().get("content").toString(), Toast.LENGTH_LONG).show();
                                adapter.notifyDataSetChanged();

                                String title = document.getData().get("title").toString();
                                String content = document.getData().get("content").toString();
                                String name = document.getData().get("name").toString();
                                String time = document.getData().get("time").toString();

                                Intent intent = new Intent(getApplicationContext(), Post_detail.class);
                                intent.putExtra("title",title);
                                intent.putExtra("content",content);
                                intent.putExtra("name",name);
                                intent.putExtra("time",time);
                                startActivity(intent);

                                System.out.println("테스트1 title:"+title+" content:"+content+" name:"+name+" time:"+time);

                                Log.d(TAG,document.getId() + " =>" + document.getData());

                            }
                        }
                        else {
                        }
                    }
                });

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                //데이터 받기
                String result = data.getStringExtra("result");
                txtResult.setText(result);
            }
        }
    }




    private void read() {
        db.collection("post").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document: task.getResult()) {
                        listItem.add((document.getData().get("title").toString()));
                        adapter.notifyDataSetChanged();

                        Log.d(TAG,document.getId() + " =>" + document.getData());

                    }
                }
                else {
                }
            }
        });
    }
}

