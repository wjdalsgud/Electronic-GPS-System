package com.example.test;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;

//이승형 : 사용자 gps 정보 받아오기(tMapGPS), 사용자 위치 변경 시 주변 주변 충전소 재검색 후 마커 재생성(onLocationChange()),
//        충전소와 사용자간 거리 계산(distanceKm())
//조진석 : (각 시도별 충전소 API 값 추출 (시도별_api( ) ) 각 시도별 충전소 정보 파싱 ( 시도별_data_thread ),
//         각 시도별 충전소 마커 생성 및 가격 정보 추가 ( 시도별_marker_thread )),
//         클릭 된 마커 정보를 받아와 해당 정보를 충전소 상세정보 XML에 세팅 및 사용자가 확인한 마커 이미지 구분 ( setOnMarkerClickEvent( ) ),
//         뒤로가기 두번 클릭 시 앱 종료 알림창 생성 ( onBackPressed( ))
//장기윤 :  마커근처 편의시설 목록을 Marker_data로 전송 (onCalloutMarker2ClickEvent)

public class MainPage extends AppCompatActivity implements TMapGpsManager.onLocationChangedCallback{

    ProgessDialog dialog;

    private long backKeyPressedTime = 0;
    private Toast toast;
    static int CFnum = 0;
    TMapView tmapview = null;
    TMapGpsManager tMapGPS = null;
    ArrayList<TMapPOIItem> poiItem;
    ArrayList<String> poiString;
    Context mContext1;
    TMapData tmapdata1 = new TMapData();
    TMapAddressInfo ti;
    public static String city=" ";

    static ArrayList<String> seoul_charge_data = new ArrayList<>();
    static ArrayList<String> busan_charge_data = new ArrayList<>();
    static ArrayList<String> daegu_charge_data = new ArrayList<>();
    static ArrayList<String> incheon_charge_data = new ArrayList<>();
    static ArrayList<String> gwangju_charge_data = new ArrayList<>();
    static ArrayList<String> daejeon_charge_data = new ArrayList<>();
    static ArrayList<String> ulsan_charge_data = new ArrayList<>();
    static ArrayList<String> gyeonggi_charge_data = new ArrayList<>();
    static ArrayList<String> gangwon_charge_data = new ArrayList<>();
    static ArrayList<String> chungbuk_charge_data = new ArrayList<>();
    static ArrayList<String> chungnam_charge_data = new ArrayList<>();
    static ArrayList<String> jeonbuk_charge_data = new ArrayList<>();
    static ArrayList<String> jeonnam_charge_data = new ArrayList<>();
    static ArrayList<String> gyeongbuk_charge_data = new ArrayList<>();
    static ArrayList<String> gyeongnam_charge_data = new ArrayList<>();
    static ArrayList<String> jeju_charge_data = new ArrayList<>();

    static final ArrayList seoul_alTMapPoint = new ArrayList();
    static final ArrayList busan_alTMapPoint = new ArrayList();
    static final ArrayList daegu_alTMapPoint = new ArrayList();
    static final ArrayList incheon_alTMapPoint = new ArrayList();
    static final ArrayList gwangju_alTMapPoint = new ArrayList();
    static final ArrayList daejeon_alTMapPoint = new ArrayList();
    static final ArrayList ulsan_alTMapPoint = new ArrayList();
    static final ArrayList gyeonggi_alTMapPoint = new ArrayList();
    static final ArrayList gangwon_alTMapPoint = new ArrayList();
    static final ArrayList chungbuk_alTMapPoint = new ArrayList();
    static final ArrayList chungnam_alTMapPoint = new ArrayList();
    static final ArrayList jeonbuk_alTMapPoint = new ArrayList();
    static final ArrayList jeonnam_alTMapPoint = new ArrayList();
    static final ArrayList gyeongbuk_alTMapPoint = new ArrayList();
    static final ArrayList gyeongnam_alTMapPoint = new ArrayList();
    static final ArrayList jeju_alTMapPoint = new ArrayList();


    // - GPS 현재위치 받아오기
    @Override
    public void onLocationChange(Location location) {
        if (location != null) {
            TMapPoint tMapPointMy = tMapGPS.getLocation();
            double latitude = tMapPointMy.getLatitude();
            double longitude = tMapPointMy.getLongitude();
            Thread city_thread = new Thread(() -> {
                try {
                    ti=tmapdata1.reverseGeocoding(latitude,longitude,"A03");
                    MainPage.city=ti.strCity_do;

                    tmapview.setLocationPoint(longitude, latitude); // 현재위치로 표시될 좌표의 위도, 경도를 설정
                    tmapview.setIconVisibility(true);
                    tmapview.setCenterPoint(longitude, latitude, true); // 현재 위치로 이동
                    loadActivity();

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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialog = new ProgessDialog(this);
        tmapview = new TMapView(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        tmapview.setZoomLevel(17);
        tmapview.setIconVisibility(true);
        tmapview.setMapType(TMapView.MAPTYPE_STANDARD);
        tmapview.setLanguage(TMapView.LANGUAGE_KOREAN);

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

        loadActivity();
    }

    private void loadActivity(){
        try {
            LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
            tmapview.setSKTMapApiKey("l7xx505c5d821c2a41fa91e5823e03392f7e");

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marker);
            Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.check);
            Context mContext=this;
            mContext1 = mContext;

            Thread seoul_data_thread = new Thread(() -> {
                try {
                    String charge_api= Seoul_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Busan_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Daegu_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Incheon_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Gwangju_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Daejeon_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Ulsan_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Gyeonggi_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Gangwon_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Chungbuk_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Chungnam_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Jeonbuk_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Jeonnam_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Gyeongbuk_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Gyeongnam_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    String charge_api= Jeju_api();
                    StringBuffer sb=new StringBuffer();
                    sb.append(charge_api);
                    int a = 7;
                    int start = sb.indexOf("<items>");
                    int end;
                    int stop = 0;
                    int cnt=0;
                    String charge_name="";
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

                            if(charge_name.equals(name)){
                                continue;
                            }else {
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
                    for(int i=0;i<seoul_charge_data.size();i++){
                        try{

                            String data=seoul_charge_data.get(i);
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
                                seoul_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) seoul_alTMapPoint.get(i));// 마커의 좌표 지정
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                gyeonggi_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
                                    markerItem1.setIcon(bitmap);
                                    markerItem1.setPosition(0.2f,0.2f);
                                    markerItem1.getTMapPoint();
                                    markerItem1.setID(strID+i);
                                    markerItem1.setTMapPoint((TMapPoint) gyeonggi_alTMapPoint.get(i));// 마커의 좌표 지정
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                chungnam_alTMapPoint.add(new TMapPoint(Double.parseDouble(lan), Double.parseDouble(lot)));
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                                if(3>distanceKm(tmapview,Double.parseDouble(lan), Double.parseDouble(lot))) {
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
                chungnam_data_thread.start();
                Thread.sleep(2000);
                chungnam_marker_thread.start();
            }else if(city.contains("충청북도")){
                chungbuk_data_thread.start();
                Thread.sleep(2000);
                chungbuk_marker_thread.start();
            }else if(city.contains("서울")){
                seoul_data_thread.start();
                Thread.sleep(2000);
                seoul_marker_thread.start();
            }else if(city.contains("인천")){
                incheon_data_thread.start();
                Thread.sleep(2000);
                incheon_marker_thread.start();
            }else if(city.contains("부산")){
                busan_data_thread.start();
                Thread.sleep(2000);
                busan_marker_thread.start();
            }else if(city.contains("대구")){
                daegu_data_thread.start();
                Thread.sleep(2000);
                daegu_marker_thread.start();
            }else if(city.contains("광주")){
                gwangju_data_thread.start();
                Thread.sleep(2000);
                gwangju_marker_thread.start();
            }else if(city.contains("울산")){
                ulsan_data_thread.start();
                Thread.sleep(2000);
                ulsan_marker_thread.start();
            }else if(city.contains("경기")){
                gyeonggi_data_thread.start();
                Thread.sleep(2000);
                gyeonggi_marker_thread.start();
            }else if(city.contains("강원")){
                gangwon_data_thread.start();
                Thread.sleep(2000);
                gangwon_marker_thread.start();
            }else if(city.contains("전라북도")){
                jeonbuk_data_thread.start();
                Thread.sleep(2000);
                jeonbuk_marker_thread.start();
            }else if(city.contains("전라남도")){
                jeonnam_data_thread.start();
                Thread.sleep(2000);
                jeonnam_marker_thread.start();
            }else if(city.contains("경상북도")){
                gyeongbuk_data_thread.start();
                Thread.sleep(2000);
                gyeongbuk_marker_thread.start();
            }else if(city.contains("경상남도")){
                gyeongnam_data_thread.start();
                Thread.sleep(2000);
                gyeongnam_marker_thread.start();
            }else if(city.contains("제주")){
                jeju_data_thread.start();
                Thread.sleep(2000);
                jeju_marker_thread.start();
            }else if(city.contains("대전")){
                daejeon_data_thread.start();
                Thread.sleep(2000);
                daejeon_marker_thread.start();
            }

            tmapview.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
                @Override
                public void onCalloutMarker2ClickEvent(String id, TMapMarkerItem2 markerItem2) {
                    MarkerOverlay marker = (MarkerOverlay) markerItem2;
                    marker.setIcon(bitmap1);
                    try {
                        Thread.sleep(1500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), Marker_data.class);
                    intent.putExtra("주", marker.labelName);
                    intent.putExtra("서브", marker.id);
                    intent.putExtra("편의시설", marker.poiString);
                    intent.putExtra("개수", "(총 "+marker.total+"개)");
                    startActivity(intent);

                }
            });

            linearLayoutTmap.addView(tmapview);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String Seoul_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("11", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Busan_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("26", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Daegu_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("27", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Incheon_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("28", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Gwangju_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("29", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Daejeon_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("30", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Ulsan_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("31", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Gyeonggi_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("41", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Gangwon_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("42", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Chungbuk_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("43", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Chungnam_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("44", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Jeonbuk_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("45", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Jeonnam_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("46", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Gyeongbuk_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("47", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Gyeongnam_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("48", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }
    public static String Jeju_api() {
        String data = "";
        HttpURLConnection conn = null;
        InputStreamReader isr = null;
        BufferedReader rd = null;
        try{
            StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/B552584/EvCharger/getChargerInfo"); /*URL*/
            urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=BGYZ3GEcNWvV9fWuAwFKo9dMtPwYVng3hWqlYLq9dHcHp5DGZAXeoBQJhD5nRR%2B0XZW8EqPjwkxzm5ph6ZIwqA%3D%3D"); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("2000", "UTF-8")); /*한 페이지 결과 수 (최소 10, 최대 9999)*/
            urlBuilder.append("&" + URLEncoder.encode("zcode","UTF-8") + "=" + URLEncoder.encode("50", "UTF-8")); /*시도 코드 (행정구역코드 앞 2자리)*/
            URL url = new URL(urlBuilder.toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");

            if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            data=sb.toString();
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            if(conn != null){
                try{conn.disconnect();}catch(Exception e){}
            }

            if(isr != null){
                try{isr.close();}catch(Exception e){}
            }

            if(rd != null){
                try{rd.close();}catch(Exception e){}
            }
        }
        return data;
    }



    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(this, "뒤로 가기를 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("정말로 종료하시겠습니까?");
            builder.setTitle("종료 알림창")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setTitle("종료 알림창");
            alert.show();
        }
    }

    //TMapPoint 좌표값을 전달받아 주변 1km의 문화시설 검색 (최대 200개)
    public ArrayList getAroundCF(TMapPoint tpoint1, String category) {

        Log.d("검색 기준 좌표", String.valueOf(tpoint1));
        TMapData tmapdata = new TMapData();
        poiItem = new ArrayList<>();
        poiString = new ArrayList<>();

        Thread thread = new Thread(() -> {

            try {
                poiItem = tmapdata.findAroundNamePOI(tpoint1, "영화관", 1, 60);
                if (null != poiItem) {
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        poiString.add(item.getPOIName());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                poiItem = tmapdata.findAroundNamePOI(tpoint1, "편의점", 1, 60);
                if(null != poiItem){
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        poiString.add(item.getPOIName());
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            try {
                poiItem = tmapdata.findAroundNamePOI(tpoint1, "오락실", 1, 60);
                if(null != poiItem){
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        poiString.add(item.getPOIName());
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            try {
                poiItem = tmapdata.findAroundNamePOI(tpoint1, "카페", 1, 60);
                if(null != poiItem){
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        poiString.add(item.getPOIName());
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            try {
                poiItem = tmapdata.findAroundNamePOI(tpoint1, "도서관", 1, 60);
                if(null != poiItem){
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        poiString.add(item.getPOIName());
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            try {
                poiItem = tmapdata.findAroundNamePOI(tpoint1, "노래방", 1, 60);
                if(null != poiItem){
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        poiString.add(item.getPOIName());
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            try {
                poiItem = tmapdata.findAroundNamePOI(tpoint1, "당구장", 1, 60);
                if(null != poiItem){
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        poiString.add(item.getPOIName());
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
            try {
                poiItem = tmapdata.findAroundNamePOI(tpoint1, "마트", 1, 60);
                if(null != poiItem){
                    for (int i = 0; i < poiItem.size(); i++) {
                        TMapPOIItem item = poiItem.get(i);
                        poiString.add(item.getPOIName());
                    }
                }

            } catch(Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return poiItem;
    }
    private static double distanceKm(TMapView mapView, double lat2, double lon2) {

        TMapPoint tpoint = mapView.getCenterPoint();
        double lat1 = tpoint.getLatitude();
        double lon1 = tpoint.getLongitude();
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


}