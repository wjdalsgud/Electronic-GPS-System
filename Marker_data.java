package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.skt.Tmap.poi_item.TMapPOIItem;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Marker_data extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_data);

        TextView nameData = (TextView) findViewById(R.id.nameData);
        TextView addressData = (TextView) findViewById(R.id.addressData);
        TextView numberData = (TextView) findViewById(R.id.numberData);
        TextView capacityData = (TextView) findViewById(R.id.capacityData);
        TextView priceData = (TextView) findViewById(R.id.priceData);
        TextView organizationData = (TextView) findViewById(R.id.organizationData);
        TextView stateData = (TextView) findViewById(R.id.stateData);
        TextView timeData = (TextView) findViewById(R.id.timeData);
        TextView typeData = (TextView) findViewById(R.id.typeData);
        TextView wayData = (TextView) findViewById(R.id.wayData);
        TextView poi = (TextView) findViewById(R.id.poi);

        ListView listView = (ListView) findViewById(R.id.markerListView);
        ArrayAdapter<String> adapter;
        Intent secondIntent = getIntent();
        try {
            String title = secondIntent.getStringExtra("주");
            String subtitle = secondIntent.getStringExtra("서브");

            title = title.replace("이름 : ", "");
            title = title.replace("\n", "");
            if(title.equals("")){
                title="등록된 정보가 없습니다";
            }
            nameData.setText(title);

            StringTokenizer st = new StringTokenizer(subtitle, "\n");
            String address = st.nextToken();
            address = address.replace("주소 : ", "");
            if(address.equals("")){
                address="등록된 정보가 없습니다";
            }
            addressData.setText(address);


            String organization = st.nextToken();
            organization = organization.replace("기관명 : ", "");
            if(organization.equals("")){
                organization="등록된 정보가 없습니다";
            }
            organizationData.setText(organization);


            String type = st.nextToken();
            type = type.replace("충전기 타입 : ", "");
            if(type.equals("")){
                type="등록된 정보가 없습니다";
            }
            typeData.setText(type);


            String state = st.nextToken();
            state = state.replace("충전기 상태 : ", "");
            if(state.equals("")){
                state="등록된 정보가 없습니다";
            }
            stateData.setText(state);


            String capacity = st.nextToken()+"kW";
            capacity = capacity.replace("충전 용량 : ", "");
            if(capacity.equals("kW")){
                capacity="등록된 정보가 없습니다";
            }
            capacityData.setText(capacity);


            String way = st.nextToken();
            way = way.replace("충전 방식 : ", "");
            if(way.equals("")){
                way="등록된 정보가 없습니다";
            }
            wayData.setText(way);


            String time = st.nextToken();
            time = time.replace("이용가능 시간 : ", "");
            if(time.equals("")){
                time="등록된 정보가 없습니다";
            }
            if(time.length()>10){
                String changetime=time.substring(time.indexOf("완"),time.lastIndexOf("원"));
                priceData.setText(changetime);
                time=time.substring(0,9);
                timeData.setText(time);

            }else{
                timeData.setText(time);
            }



            String number = st.nextToken();
            number = number.replace("연락처 : ", "");
            if(number.equals("")){
                number="등록된 정보가 없습니다";
            }
            numberData.setText(number);


            String price = st.nextToken();
            if(price.equals("")){
                price="등록된 정보가 없습니다";
            }
            priceData.setText(price);


        } catch(Exception e){
            Toast.makeText(getApplicationContext(), "해당 충전소의 몇몇 정보가 없습니다", Toast.LENGTH_SHORT).show();

        }
        try{
            String total = secondIntent.getStringExtra("개수");
            poi.append("  "+total);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        try {
            ArrayList<String> ar = secondIntent.getStringArrayListExtra("편의시설");
            ArrayList<String> inputData = new ArrayList<String>();
            adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, inputData);
            listView.setAdapter(adapter);
            for(int i = 0;i < ar.size();i++) {
                inputData.add(ar.get(i));
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}