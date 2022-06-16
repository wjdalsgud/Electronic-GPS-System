package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapGpsManager;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.address_info.TMapAddressInfo;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

//장기윤 : RoadSearch 전체적인 기능 개발
//조진석 : 충전소 정보 협업
//이승형 : 충전소와 사용자간 거리 계산(distanceKm())
public class RoadSearch extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback {

    // - 리스트 전역변수 설정
    ArrayAdapter<String> adapter;
    ArrayList<String> listItem;
    ArrayList<String> listItemSave;

    // - 전역변수 설정
    int num = -1;
    String strData;
    ArrayList<String> locationData = new ArrayList<>();
    ArrayList<TMapPOIItem> poiItem;
    ArrayList<TMapPoint> par;
    TMapData tmapdata;
    TMapView tmapview;
    ThreadFind threadfind;
    ThreadEndPoint threadendpoint;
    TMapPoint startPoint;
    TMapPoint endPoint;
    TMapGpsManager tMapGPS = null;
    TMapMarkerItem markerItem1 = new TMapMarkerItem();
    Context mContext1;
    TMapAddressInfo ti;
    public static String city = " ";

    boolean seoul = true;
    boolean busan = true;
    boolean daegu = true;
    boolean incheon = true;
    boolean gwangju = true;
    boolean daejeon = true;
    boolean ulsan = true;
    boolean gyeonggi = true;
    boolean gangwon = true;
    boolean chungbuk = true;
    boolean chungnam = true;
    boolean jeonbuk = true;
    boolean jeonnam = true;
    boolean gyeongbuk = true;
    boolean gyeongnam = true;
    boolean jeju = true;

    ArrayList<String> seoul_charge_data = MainPage.seoul_charge_data;
    ArrayList<String> busan_charge_data = MainPage.busan_charge_data;
    ArrayList<String> daegu_charge_data = MainPage.daegu_charge_data;
    ArrayList<String> incheon_charge_data = MainPage.incheon_charge_data;
    ArrayList<String> gwangju_charge_data = MainPage.gwangju_charge_data;
    ArrayList<String> daejeon_charge_data = MainPage.daejeon_charge_data;
    ArrayList<String> ulsan_charge_data = MainPage.ulsan_charge_data;
    ArrayList<String> gyeonggi_charge_data = MainPage.gyeonggi_charge_data;
    ArrayList<String> gangwon_charge_data = MainPage.gangwon_charge_data;
    ArrayList<String> chungbuk_charge_data = MainPage.chungbuk_charge_data;
    ArrayList<String> chungnam_charge_data = MainPage.chungnam_charge_data;
    ArrayList<String> jeonbuk_charge_data = MainPage.jeonbuk_charge_data;
    ArrayList<String> jeonnam_charge_data = MainPage.jeonnam_charge_data;
    ArrayList<String> gyeongbuk_charge_data = MainPage.gyeongbuk_charge_data;
    ArrayList<String> gyeongnam_charge_data = MainPage.gyeongnam_charge_data;
    ArrayList<String> jeju_charge_data = MainPage.jeju_charge_data;

    final ArrayList seoul_alTMapPoint = MainPage.seoul_alTMapPoint;
    final ArrayList busan_alTMapPoint = MainPage.busan_alTMapPoint;
    final ArrayList daegu_alTMapPoint = MainPage.daegu_alTMapPoint;
    final ArrayList incheon_alTMapPoint = MainPage.incheon_alTMapPoint;
    final ArrayList gwangju_alTMapPoint = MainPage.gwangju_alTMapPoint;
    final ArrayList daejeon_alTMapPoint = MainPage.daejeon_alTMapPoint;
    final ArrayList ulsan_alTMapPoint = MainPage.ulsan_alTMapPoint;
    final ArrayList gyeonggi_alTMapPoint = MainPage.gyeonggi_alTMapPoint;
    final ArrayList gangwon_alTMapPoint = MainPage.gangwon_alTMapPoint;
    final ArrayList chungbuk_alTMapPoint = MainPage.chungbuk_alTMapPoint;
    final ArrayList chungnam_alTMapPoint = MainPage.chungnam_alTMapPoint;
    final ArrayList jeonbuk_alTMapPoint = MainPage.jeonbuk_alTMapPoint;
    final ArrayList jeonnam_alTMapPoint = MainPage.jeonnam_alTMapPoint;
    final ArrayList gyeongbuk_alTMapPoint = MainPage.gyeongbuk_alTMapPoint;
    final ArrayList gyeongnam_alTMapPoint = MainPage.gyeongnam_alTMapPoint;
    final ArrayList jeju_alTMapPoint = MainPage.jeju_alTMapPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.road_search);

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
                        return true;
                    case R.id.read:
                        intent = new Intent(getApplicationContext(), info.class);
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

        // - xml 관련
        EditText findLocation = (EditText) findViewById(R.id.findLocation);
        Button findLocationButton = (Button) findViewById(R.id.button);
        Button endPointButton = (Button) findViewById(R.id.button2);
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        ListView listView = (ListView) findViewById(R.id.listview);
        ScrollView scrollView = (ScrollView) findViewById(R.id.scrollView);


        // - 위치 권한 요청
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_DENIED) { //위치 권한 확인
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }


        // - GPS 세팅
        tMapGPS = new TMapGpsManager(this);
        tMapGPS.setMinTime(1000);
        tMapGPS.setMinDistance(10);
        tMapGPS.setProvider(tMapGPS.GPS_PROVIDER);
        tMapGPS.OpenGps();


        // - tmap 세팅
        tmapdata = new TMapData();
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xxa944a250117248b8964e9b8861ccb838"); //이부분은 아까 발급 받은 T-Map API를 입력하면 된다.
        linearLayoutTmap.addView(tmapview);


        // - 리스트 데이터를 넣을 준비
        listItem = new ArrayList<String>();
        listItemSave = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItemSave);
        listView.setAdapter(adapter);


        // - 키보드 표시제어
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        // - 검색버튼 스레드 동작 구현
        threadfind = new ThreadFind();
        findLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tmapview.removeAllMarkerItem();
                if (findLocation.getText().toString().equals("")) {
                } else if (threadfind.getState() == Thread.State.TERMINATED) {
                    System.out.println("스레드 상태 = " + threadfind.getState());
                    threadfind.interrupt();
                    threadfind = new ThreadFind();
                    threadfind.start();
                } else {
                    System.out.println("스레드 상태 = " + threadfind.getState());
                    threadfind.start();
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(listItem);
                listItemSave.clear();
                listSave();
                scrollView.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(findLocationButton.getWindowToken(), 0);
            }
        });

        findLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.setVisibility(View.INVISIBLE);
            }
        });

        // - 도착지 버튼 스레드 동작 구현
        threadendpoint = new ThreadEndPoint();
        endPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (threadendpoint.getState() == Thread.State.TERMINATED) {
                    threadendpoint.interrupt();
                    threadendpoint = new ThreadEndPoint();
                    threadendpoint.start();
                    for (; threadendpoint.getState() != Thread.State.TERMINATED || locationData.size() < 1; ) {
                        try {
                            Thread.sleep(1500);
                            System.out.println("스레드 상태 = " + threadendpoint.getState());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    locationParsing();
                } else {
                    threadendpoint.start();
                    for (; locationData.isEmpty(); ) {
                        try {
                            Thread.sleep(1500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    locationParsing();
                    //MainPage.seoul_charge_data
                }
            }
        });

        markerClick();

        // - 리스트 아이템 동작
        Bitmap bitmap_orange = BitmapFactory.decodeResource(getResources(), R.drawable.markerline_orange);
        Bitmap bitmap_green = BitmapFactory.decodeResource(getResources(), R.drawable.markerline_green);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String data = (String) adapterView.getAdapter().getItem(i);
                markerItem1 = tmapview.getMarkerItemFromID("marker" + i);
                endPoint = markerItem1.getTMapPoint();
                markerItem1.setIcon(bitmap_green);
                tmapview.setCenterPoint(endPoint.getLongitude(), endPoint.getLatitude(), true);
                if (num > -1) {
                    markerItem1 = tmapview.getMarkerItemFromID("marker" + num);
                    markerItem1.setIcon(bitmap_orange);
                }
                num = i;

            }
        });


        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.check);
        tmapview.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
            @Override
            public void onCalloutMarker2ClickEvent(String id, TMapMarkerItem2 markerItem2) {
                MarkerOverlay marker = (MarkerOverlay) markerItem2;
                marker.setIcon(bitmap1);
                marker.setPosition(0.2f, 0.2f);
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(getApplicationContext(), Marker_data.class);
                intent.putExtra("주", marker.labelName);
                intent.putExtra("서브", marker.id);
                intent.putExtra("편의시설", marker.poiString);
                intent.putExtra("개수", "(총 " + marker.total + "개)");
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        listSave();
    }


    public void loadActivity(int num) {
        try {
            Context mContext = this;
            mContext1 = mContext;

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);

            Thread seoul_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Seoul_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            seoul_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");
                            cnt++;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            Thread busan_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Busan_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            busan_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread daegu_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Daegu_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            daegu_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread incheon_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Incheon_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            incheon_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gwangju_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Gwangju_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            gwangju_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread daejeon_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Daejeon_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            daejeon_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread ulsan_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Ulsan_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            ulsan_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gyeonggi_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Gyeonggi_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            gyeonggi_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gangwon_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Gangwon_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            gangwon_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread chungbuk_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Chungbuk_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            chungbuk_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread chungnam_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Chungnam_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");

                            list_data += call + "\n";

                            chungnam_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread jeonbuk_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Jeonbuk_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            jeonbuk_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread jeonnam_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Jeonnam_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            jeonnam_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gyeongbuk_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Gyeongbuk_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            gyeongbuk_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gyeongnam_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Gyeongnam_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            gyeongnam_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread jeju_data_thread = new Thread(() -> {
                try {
                    String charge_api = MainPage.Jeju_api();
                    StringBuffer sb = new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt = 0;
                    String charge_name = "";
                    String data;
                    String name;
                    String id_name;
                    String type;
                    String address;
                    String lat;
                    String lng;
                    String useTime;
                    String bnm;
                    String call;
                    String stat;
                    String update;
                    String output;
                    String method;
                    String list_data;
                    sb.delete(0, start);
                    while (true) {
                        if (stop == 7) {
                            break;
                        } else {
                            start = sb.indexOf("<item>");
                            end = sb.indexOf("</item>");
                            data = sb.substring(start, end); //원본 데이터
                            sb.delete(start, end + 7);

                            start = data.indexOf("<lat>");
                            end = data.indexOf("</lat>");
                            lat = data.substring(start, end);
                            lat = lat.replace("<lat>", "");

                            start = data.indexOf("<lng>");
                            end = data.indexOf("</lng>");
                            lng = data.substring(start, end);
                            lng = lng.replace("<lng>", "");

                            start = data.indexOf("<statUpdDt>");
                            end = data.indexOf("</statUpdDt>");
                            update = data.substring(start, end);
                            update = update.replace("<statUpdDt>", "갱신 시간 : ");

                            start = data.indexOf("<statNm>");
                            end = data.indexOf("</statNm>");
                            name = data.substring(start, end);
                            name = name.replace("<statNm>", "이름 : ");

                            if (charge_name.equals(name)) {
                                continue;
                            } else {
                                charge_name = name;
                                list_data = lat + ",\n"; //위도
                                list_data += lng + ",\n"; //경도
                                list_data += update + ",\n"; //갱신시간
                                list_data += name + ",\n";
                            }

                            start = data.indexOf("<addr>");
                            end = data.indexOf("</addr>");
                            address = data.substring(start, end);
                            address = address.replace("<addr>", "주소 : ");
                            list_data += address + "\n";

                            start = data.indexOf("<bnm>");
                            end = data.indexOf("</bnm>");
                            id_name = data.substring(start, end);
                            id_name = id_name.replace("<bnm>", "기관명 : ");
                            list_data += id_name + "\n";

                            start = data.indexOf("<chgerType>");
                            end = data.indexOf("</chgerType>");
                            type = data.substring(start, end);
                            type = type.replace("<chgerType>", "");
                            if (type.equals("01")) {
                                type = "충전기 타입 : DC차데모";
                                list_data += type + "\n";

                            } else if (type.equals("02")) {
                                type = "충전기 타입 : AC완속";
                                list_data += type + "\n";

                            } else if (type.equals("03")) {
                                type = "충전기 타입 : DC차데모+AC3상";
                                list_data += type + "\n";

                            } else if (type.equals("04")) {
                                type = "충전기 타입 : DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("05")) {
                                type = "충전기 타입 : DC차데모+DC콤보";
                                list_data += type + "\n";

                            } else if (type.equals("06")) {
                                type = "충전기 타입 : DC차데모+AC3상+DC콤보";
                                list_data += type + "\n";

                            } else {
                                type = "충전기 타입 : AC3상";
                                list_data += type + "\n";

                            }

                            start = data.indexOf("<stat>");
                            end = data.indexOf("</stat>");
                            stat = data.substring(start, end);
                            stat = stat.replace("<stat>", "");
                            if (stat.equals("1")) {
                                stat = "충전기 상태 : 통신 이상";
                                list_data += stat + "\n";

                            } else if (stat.equals("2")) {
                                stat = "충전기 상태 : 충전 대기";
                                list_data += stat + "\n";

                            } else if (stat.equals("3")) {
                                stat = "충전기 상태 : 충전 중";
                                list_data += stat + "\n";

                            } else if (stat.equals("4")) {
                                stat = "충전기 상태 : 운영 중지";
                                list_data += stat + "\n";

                            } else if (stat.equals("5")) {
                                stat = "충전기 상태 : 점검 중";
                                list_data += stat + "\n";

                            } else {
                                stat = "충전기 상태 : 상태 미확인";
                                list_data += stat + "\n";

                            }

                            start = data.indexOf("<output>");
                            end = data.indexOf("</output>");
                            output = data.substring(start, end);
                            output = output.replace("<output>", "충전 용량 : ");
                            list_data += output + "\n";

                            start = data.indexOf("<method>");
                            end = data.indexOf("</method>");
                            method = data.substring(start, end);
                            method = method.replace("<method>", "충전 방식 : ");
                            list_data += method + "\n";

                            start = data.indexOf("<useTime>");
                            end = data.indexOf("</useTime>");
                            useTime = data.substring(start, end);
                            useTime = useTime.replace("<useTime>", "이용가능 시간 : ");
                            list_data += useTime + "\n";

                            start = data.indexOf("<busiCall>");
                            end = data.indexOf("</busiCall>");
                            call = data.substring(start, end);
                            call = call.replace("<busiCall>", "연락처 : ");
                            list_data += call + "\n";

                            jeju_charge_data.add(list_data);
                            stop = sb.indexOf("</items>");

                            cnt++;

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            Thread seoul_marker_thread = new Thread(() -> {
                try {
                    for (int i = 0; i < seoul_charge_data.size(); i++) {
                        try {

                            String data = seoul_charge_data.get(i);
                            StringTokenizer st = new StringTokenizer(data, ",");
                            String lan = st.nextToken();
                            String lot = st.nextToken();
                            String time = st.nextToken();
                            String name = st.nextToken();
                            String other = st.nextToken();
                            String price = " ";
                            if (other.contains("환경부")) {
                                price = "완속 : 292.9원  급속 : 309.1원";
                            } else if (other.contains("GS")) {
                                price = "완속 : 259원  급속 : 279원";
                            } else if (other.contains("한국전력")) {
                                price = "완속 : 292.9원  급속 : 309.1원";
                            } else if (other.contains("제주전기자동차서비스")) {
                                price = "완속 : 280원  급속 : 280원";
                            } else if (other.contains("한국전기차충전서비스")) {
                                price = "완속 : 255.7원  급속 : 290원";
                            } else if (other.contains("에스트래픽")) {
                                price = "완속 : 292.9원  급속 : 309.1원";
                            } else if (other.contains("SK")) {
                                price = "완속 : 309.1원  급속 : 309.1원";
                            } else if (other.contains("차지비")) {
                                price = "완속 : 279원  급속 : 279원";
                            } else if (other.contains("에버온")) {
                                price = "완속 : 292.9원  급속 : 309.1원";
                            } else if (other.contains("제주도청")) {
                                price = "완속 : 290원  급속 : 290원";
                            } else if (other.contains("대영채비")) {
                                price = "완속 : 250원  급속 : 265원";
                            } else if (other.contains("지엔텔")) {
                                price = "완속 : 292.9원  급속 : 309.1원";
                            } else if (other.contains("이카플러그")) {
                                price = "완속 : 390원  급속 : 390원";
                            } else if (other.contains("스타코프")) {
                                price = "완속 : 255.7원  급속 : 255.7원";
                            } else if (other.contains("한국전기차인프라기술")) {
                                price = "완속 : 255.7원  급속 : 255.7원";
                            } else if (other.contains("보타리")) {
                                price = "완속 : 300원  급속 : 300원";
                            } else if (other.contains("테슬라")) {
                                price = "완속 : 275원  급속 : 327원";
                            } else if (other.contains("타디스테크놀로지")) {
                                price = "완속 : 290원  급속 : 305원";
                            } else if (other.contains("현대오일뱅크")) {
                                price = "완속 : 292.9원  급속 : 309.1원";
                            } else if (other.contains("클린일렉스")) {
                                price = "완속 : 292.9원  급속 : 292.9원";
                            } else if (other.contains("LG")) {
                                price = "완속 : 240원  급속 : 240원";
                            } else if (other.contains("휴맥스")) {
                                price = "완속 : 253원  급속 : 292.2원";
                            } else if (other.contains("레드이앤지")) {
                                price = "완속 : 255.7원  급속 : 255.7원";
                            } else {
                                price = "\n가격정보가 없습니다";

                            }
                            other += price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                double a = distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot));
                                if(a <= 3) {
                                    System.out.println(a);
                                    seoul_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f, 0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID + i);
                                    markerItem1.setTMapPoint((TMapPoint) seoul_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID + i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread busan_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<busan_charge_data.size();i++){
                        try{

                            String data=busan_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                busan_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) busan_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread daegu_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<daegu_charge_data.size();i++){
                        try{

                            String data=daegu_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                daegu_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) daegu_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread incheon_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<incheon_charge_data.size();i++){
                        try{

                            String data=incheon_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                incheon_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) incheon_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gwangju_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<gwangju_charge_data.size();i++){
                        try{

                            String data=gwangju_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                gwangju_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) gwangju_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread daejeon_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<daejeon_charge_data.size();i++){
                        try{

                            String data=daejeon_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                daejeon_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) daejeon_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread ulsan_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<ulsan_charge_data.size();i++){
                        try{

                            String data=ulsan_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                ulsan_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) ulsan_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gyeonggi_marker_thread = new Thread(() -> {
                try {
                    int number = 0;
                    for(int i=0;i<gyeonggi_charge_data.size();i++){
                        try{

                            String data=gyeonggi_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                double a = distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot));
                                if(a <= 3) {
                                    System.out.println(a);
                                    gyeonggi_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+number);
                                    markerItem1.setTMapPoint((TMapPoint) gyeonggi_alTMapPoint.get(number));// 마커의 좌표 지정
                                    number++;
                                    tmapview.addMarkerItem2(strID+number, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gangwon_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<gangwon_charge_data.size();i++){
                        try{

                            String data=gangwon_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                gangwon_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) gangwon_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread chungbuk_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<chungbuk_charge_data.size();i++){
                        try{

                            String data=chungbuk_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                chungbuk_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) chungbuk_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread chungnam_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<chungnam_charge_data.size();i++){
                        try{

                            String data=chungnam_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;

                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                double a = distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot));
                                if(a <= 2) {
                                    System.out.println(a);
                                    chungnam_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) chungnam_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                            }

                        }catch(Exception e) {
                        }
                    }
                } catch (Exception e) {
                    System.out.println("eeeeeeee");
                }

            });
            Thread jeonbuk_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<jeonbuk_charge_data.size();i++){
                        try{

                            String data=jeonbuk_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                jeonbuk_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) jeonbuk_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread jeonnam_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<jeonnam_charge_data.size();i++){
                        try{

                            String data=jeonnam_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                jeonnam_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) jeonnam_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gyeongbuk_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<gyeongbuk_charge_data.size();i++){
                        try{

                            String data=gyeongbuk_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                gyeongbuk_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) gyeongbuk_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread gyeongnam_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<gyeongnam_charge_data.size();i++){
                        try{

                            String data=gyeongnam_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                gyeongnam_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) gyeongnam_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            Thread jeju_marker_thread = new Thread(() -> {
                try {
                    for(int i=0;i<jeju_charge_data.size();i++){
                        try{

                            String data=jeju_charge_data.get(i);
                            StringTokenizer st= new StringTokenizer(data,",");
                            String lan=st.nextToken();
                            String lot=st.nextToken();
                            String time=st.nextToken();
                            String name=st.nextToken();
                            String other=st.nextToken();
                            String price=" ";
                            if(other.contains("환경부")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("GS")){
                                price="완속 : 259원  급속 : 279원";
                            }else if(other.contains("한국전력")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주전기자동차서비스")){
                                price="완속 : 280원  급속 : 280원";
                            }else if(other.contains("한국전기차충전서비스")){
                                price="완속 : 255.7원  급속 : 290원";
                            }else if(other.contains("에스트래픽")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("SK")){
                                price="완속 : 309.1원  급속 : 309.1원";
                            }else if(other.contains("차지비")){
                                price="완속 : 279원  급속 : 279원";
                            }else if(other.contains("에버온")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("제주도청")){
                                price="완속 : 290원  급속 : 290원";
                            }else if(other.contains("대영채비")){
                                price="완속 : 250원  급속 : 265원";
                            }else if(other.contains("지엔텔")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("이카플러그")){
                                price="완속 : 390원  급속 : 390원";
                            }else if(other.contains("스타코프")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("한국전기차인프라기술")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }else if(other.contains("보타리")){
                                price="완속 : 300원  급속 : 300원";
                            }else if(other.contains("테슬라")){
                                price="완속 : 275원  급속 : 327원";
                            }else if(other.contains("타디스테크놀로지")){
                                price="완속 : 290원  급속 : 305원";
                            }else if(other.contains("현대오일뱅크")){
                                price="완속 : 292.9원  급속 : 309.1원";
                            }else if(other.contains("클린일렉스")){
                                price="완속 : 292.9원  급속 : 292.9원";
                            }else if(other.contains("LG")){
                                price="완속 : 240원  급속 : 240원";
                            }else if(other.contains("휴맥스")){
                                price="완속 : 253원  급속 : 292.2원";
                            }else if(other.contains("레드이앤지")){
                                price="완속 : 255.7원  급속 : 255.7원";
                            }
                            else{
                                price="\n가격정보가 없습니다";

                            }
                            other+=price;
                            try {
                                String strID = "TMapMarkerItem2";
                                MarkerOverlay markerItem1 = new MarkerOverlay(mContext, name, other);
                                jeju_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(distanceKm(par.get(num).getLatitude(), par.get(num).getLongitude(), Double.parseDouble(lan), Double.parseDouble(lot)) <= 1) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) jeju_alTMapPoint.get(i));// 마커의 좌표 지정
                                    tmapview.addMarkerItem2(strID+i, markerItem1);//지도에 마커 추가
                                }
                            } catch (NullPointerException e) {
                                e.toString();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }

                        }catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

            if(city.contains("충청남도")){
                if(chungnam_charge_data.isEmpty() && chungnam == true) {
                    chungnam = false;
                    chungnam_data_thread.start();
                }
                Thread.sleep(100);
                chungnam_marker_thread.start();
            }else if(city.contains("충청북도")){
                if(chungbuk_charge_data.isEmpty() && chungbuk == true) {
                    chungbuk = false;
                    chungbuk_data_thread.start();
                }
                Thread.sleep(100);
                chungbuk_marker_thread.start();
            }else if(city.contains("서울")){
                if (seoul_charge_data.isEmpty() && seoul == true) {
                    seoul = false;
                    seoul_data_thread.start();
                }
                Thread.sleep(100);
                seoul_marker_thread.start();
            }else if(city.contains("인천")){
                if (incheon_charge_data.isEmpty() && incheon == true) {
                    incheon = false;
                    incheon_data_thread.start();
                }
                Thread.sleep(100);
                incheon_marker_thread.start();
            }else if(city.contains("부산")){
                if (busan_charge_data.isEmpty() && busan == true) {
                    busan = false;
                    busan_data_thread.start();
                }
                Thread.sleep(100);
                busan_marker_thread.start();
            }else if(city.contains("대구")){
                if (daegu_charge_data.isEmpty() && daegu == true) {
                    daegu = false;
                    daegu_data_thread.start();
                }
                Thread.sleep(100);
                daegu_marker_thread.start();
            }else if(city.contains("광주")){
                if (gwangju_charge_data.isEmpty() && gwangju == true) {
                    gwangju = false;
                    gwangju_data_thread.start();
                }
                Thread.sleep(100);
                gwangju_marker_thread.start();
            }else if(city.contains("울산")){
                if (ulsan_charge_data.isEmpty() && ulsan == true) {
                    ulsan = false;
                    ulsan_data_thread.start();
                }
                Thread.sleep(100);
                ulsan_marker_thread.start();
            }else if(city.contains("경기")){
                if (gyeonggi_charge_data.isEmpty() && gyeonggi == true) {
                    gyeonggi = false;
                    gyeonggi_data_thread.start();
                }
                Thread.sleep(100);
                gyeonggi_marker_thread.start();
            }else if(city.contains("강원")){
                if (gangwon_charge_data.isEmpty() && gangwon == true) {
                    gangwon = false;
                    gangwon_data_thread.start();
                }
                Thread.sleep(100);
                gangwon_marker_thread.start();
            }else if(city.contains("전라북도")){
                if (jeonbuk_charge_data.isEmpty() && jeonbuk == true) {
                    jeonbuk = false;
                    jeonbuk_data_thread.start();
                }
                Thread.sleep(100);
                jeonbuk_marker_thread.start();
            }else if(city.contains("전라남도")){
                if (jeonnam_charge_data.isEmpty() && jeonnam == true) {
                    jeonnam = false;
                    jeonnam_data_thread.start();
                }
                Thread.sleep(100);
                jeonnam_marker_thread.start();
            }else if(city.contains("경상북도")){
                if (gyeongbuk_charge_data.isEmpty() && gyeongbuk == true) {
                    gyeongbuk = false;
                    gyeongbuk_data_thread.start();
                }
                Thread.sleep(100);
                gyeongbuk_marker_thread.start();
            }else if(city.contains("경상남도")){
                if (gyeongnam_charge_data.isEmpty() && gyeongnam == true) {
                    gyeongnam = false;
                    gyeongnam_data_thread.start();
                }
                Thread.sleep(100);
                gyeongnam_marker_thread.start();
            }else if(city.contains("제주")){
                if (jeju_charge_data.isEmpty() && jeju == true) {
                    jeju = false;
                    jeju_data_thread.start();
                }
                Thread.sleep(100);
                jeju_marker_thread.start();
            }else if(city.contains("대전")){
                if (daejeon_charge_data.isEmpty() && daejeon == true) {
                    daejeon = false;
                    daejeon_data_thread.start();
                }
                Thread.sleep(100);
                daejeon_marker_thread.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double distanceKm(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515 * 1.609344;

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


    // - 마커 클릭시 클릭좌표 저장 구현
    public void markerClick() {
        tmapview.setOnClickListenerCallBack(new TMapView.OnClickListenerCallback() {
            @Override
            public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                if (!arrayList.isEmpty()) {
                    endPoint = arrayList.get(0).getTMapPoint();
                }
                return false;
            }

            @Override
            public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
                return false;
            }
        });
    }

    public void listSave() {
        for (String i : listItem) {
            listItemSave.add(i);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    // - 마커 생성
    public void markerSave(TMapView tmapview, ArrayList poiItem, int i) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.markerline_orange);
        TMapPOIItem item = (TMapPOIItem) poiItem.get(i);
        TMapPoint tMapPoint = item.getPOIPoint();
        markerItem1 = new TMapMarkerItem();
        markerItem1.setTMapPoint(tMapPoint);
        markerItem1.setName(item.getPOIName());
        markerItem1.setCanShowCallout(true);
        markerItem1.setIcon(bitmap);
        markerItem1.setCalloutTitle(item.getPOIName().toString());
        markerItem1.setCalloutSubTitle(item.getPOIAddress().replace("null", ""));
        tmapview.addMarkerItem("marker" + i, markerItem1);
        listItem.add(item.getPOIName().toString());
        if (i == 0) {
            tmapview.setCenterPoint(item.getPOIPoint().getLongitude(), item.getPOIPoint().getLatitude() - 0.006);
            tmapview.setZoomLevel(14);
        }
    }


    // - GPS 현재위치 받아오기
    @Override
    public void onLocationChange(Location location) {
        if (location != null) {
            TMapPoint tMapPointMy = tMapGPS.getLocation();
            double latitude = tMapPointMy.getLatitude();
            double longitude = tMapPointMy.getLongitude();
            tmapview.setLocationPoint(longitude, latitude); // 현재위치로 표시될 좌표의 위도, 경도를 설정
            tmapview.setIconVisibility(true);
            tmapview.setCenterPoint(longitude, latitude, true); // 현재 위치로 이동
            startPoint = tMapPointMy;
        }
    }

    // - 경로내 임의의 좌표 받아와서 주유소 값 뿌리기
    public void locationParsing() {
        double lat = 0;
        double lon = 0;
        par = new ArrayList<>();
        for (int i = 0; i < locationData.size(); i++) {
            String data = locationData.get(i);
            if (data.indexOf(".") == 3 && !data.contains(" ")) {
                StringTokenizer tokenizer = new StringTokenizer(data, ",");
                while (tokenizer.hasMoreTokens()) {
                    String inputData = tokenizer.nextToken();
                    if (Double.parseDouble(inputData) < 100) {
                        lat = Double.parseDouble(inputData);
                        TMapPoint point = new TMapPoint(lat, lon);
                        par.add(point);
                    } else {
                        lon = Double.parseDouble(inputData);
                    }
                }
            } else {

            }

        }
        Thread city_thread = new Thread(() -> {
            try {
                for (int i = 0; i < par.size(); i++) {
                    ti = tmapdata.reverseGeocoding(par.get(i).getLatitude(), par.get(i).getLongitude(), "A03");
                    System.out.println(par.get(i).getLatitude() + " " + par.get(i).getLongitude());
                    System.out.println(ti.strCity_do);
                    RoadSearch.city = ti.strCity_do;
                    if (!par.get(i).equals(null)) {
                        loadActivity(i);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        });
        city_thread.start();
    }

    // - 검색 동작 스레드
    class ThreadFind extends Thread {
        EditText findLocation = (EditText) findViewById(R.id.findLocation);
        @Override
        public void run() {
            poiItem = new ArrayList<>();
            try {
                listItem = new ArrayList<String>();
                strData = findLocation.getText().toString();
                try {
                    poiItem = tmapdata.findAroundNamePOI(startPoint, strData, 4, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(poiItem.isEmpty()) {
                    poiItem = tmapdata.findAllPOI(strData);
                    tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                        @Override
                        public void onFindAllPOI(ArrayList<TMapPOIItem> arrayList) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                markerSave(tmapview, poiItem, i);
                            }
                        }
                    });
                } else {
                    poiItem = tmapdata.findAroundNamePOI(startPoint, strData);
                    tmapdata.findAroundNamePOI(startPoint, strData, new TMapData.FindAroundNamePOIListenerCallback() {
                        @Override
                        public void onFindAroundNamePOI(ArrayList<TMapPOIItem> arrayList) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                markerSave(tmapview, poiItem, i);
                            }
                        }
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    // - 도착지 버튼 동작 스레드
    class ThreadEndPoint extends Thread {
        @Override
        public void run() {
            try {
                TMapPolyLine tMapPolyLine = tmapdata.findPathData(startPoint, endPoint);
                tMapPolyLine.setLineColor(Color.MAGENTA);
                tMapPolyLine.setOutLineColor(Color.MAGENTA);
                tMapPolyLine.setLineWidth(4);
                tmapview.addTMapPolyLine("Line1", tMapPolyLine);
                locationData = new ArrayList<>();

                tmapdata.findPathDataAllType(TMapData.TMapPathType.CAR_PATH, startPoint, endPoint, new TMapData.FindPathDataAllListenerCallback() {
                    @Override
                    public void onFindPathDataAll(Document document) {
                        Element root = document.getDocumentElement();
                        NodeList nodeListPlacemark = root.getElementsByTagName("Placemark");
                        for (int i = 0; i < nodeListPlacemark.getLength(); i++) {
                            NodeList nodeListPlacemarkItem = nodeListPlacemark.item(i).getChildNodes();
                            for (int j = 0; j < nodeListPlacemarkItem.getLength(); j++) {
                                if (nodeListPlacemarkItem.item(j).getNodeName().equals("description")) {
                                }
                                locationData.add(nodeListPlacemarkItem.item(j).getTextContent().trim());
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
