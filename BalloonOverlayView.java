package com.example.test;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.StringTokenizer;

public class BalloonOverlayView extends FrameLayout {

    private LinearLayout layout;
    private TextView title;
    private TextView subTitle;
    private TextView star;
    private TextView charge;
    private TextView state;
    private TextView culture;
    public BalloonOverlayView(Context context, String labelName, String id) {

        super(context);

        setPadding(10, 0, 10, 0);
        layout = new LinearLayout(context);
        layout.setVisibility(VISIBLE);

        setupView(context, layout, labelName, id);

        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.NO_GRAVITY;
        addView(layout, params);
    }


    protected void setupView(Context context, final ViewGroup parent, String labelName, String id) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View view = inflater.inflate(R.layout.bubble_popup, parent, true);
        try {
            title = (TextView) view.findViewById(R.id.bubble_title);
            subTitle = (TextView) view.findViewById(R.id.bubble_subtitle);
            star = (TextView) view.findViewById(R.id.bubble_star);
            charge = (TextView) view.findViewById(R.id.bubble_charge);
            state = (TextView) view.findViewById(R.id.bubble_state);
            culture = (TextView) view.findViewById(R.id.bubble_culture);
            labelName = labelName.replace("이름 : ", "");
            StringTokenizer st = new StringTokenizer(id, "\n");
            String address = st.nextToken();
            address = address.replace("주소 : ", "");
            setTitle(labelName);
            setSubTitle(address);
            double a=(2.5+(Math.random()*2.5));
            setStar(String.format("%.1f", a));

            String organization = st.nextToken();
            String type = st.nextToken();

            type = type.replace("충전기 타입 : ", "");
            if (type.equals("")) {
                type = "타입 : 미등록";
            }
            String type2="타입 : "+type;
            setCharge(type2);

            String state1 = st.nextToken();
            state1 = state1.replace("충전기 상태 : ", "");
            if (state1.equals("")) {
                state1 = "상태 : 미등록";
            }

            String state2="상태 : "+state1;
            setState(state2);


        }catch(Exception e){
            e.printStackTrace();
        }



    }

    public void setTitle(String str) {
        title.setText(str);
    }
    public void setStar(String str){
        try{
            star.setText(str+"/5.0");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void setCharge(String str) {
        charge.setText(str);
    }

    public void setState(String str) {
        state.setText(str);
    }

    public void setCulture(String str) {
        culture.setText(str);
    }
    public void appendCulture(String str) {
        culture.append(str);
    }
    public void setSubTitle(String str) {
        subTitle.setText(str);
    }
}