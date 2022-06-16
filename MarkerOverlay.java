package com.example.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;
import com.skt.Tmap.poi_item.TMapPOIItem;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.test.culture_find.result;
import javax.xml.parsers.ParserConfigurationException;

public class MarkerOverlay extends TMapMarkerItem2 {

    private DisplayMetrics dm = null;
    public String id;
    public String labelName;
    private Context mContext = null;
    private BalloonOverlayView balloonView = null;
    private int mAnimationCount = 0;
    public int total=0;
    TMapView mMapView;
    Rect rect= new Rect();

    ArrayList<TMapPOIItem> poiItem = new ArrayList<>();
    ArrayList<TMapPOIItem> poiItem1 = new ArrayList<>();
    ArrayList<String> poiString = new ArrayList<>();
    int vus = 0, dud = 0, dh = 0, zk = 0, eh = 0, sh = 0, ekd = 0, ak = 0,dms = 0, pc = 0, qhf = 0, tlr = 0, ans = 0, qor= 0, ty= 0,rhd= 0;

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public Bitmap getIcon() {
        return super.getIcon();
    }

    @Override
    public void setIcon(Bitmap bitmap) {
        super.setIcon(bitmap);
    }

    @Override
    public void setTMapPoint(TMapPoint point) {
        super.setTMapPoint(point);
    }

    @Override
    public TMapPoint getTMapPoint() {
        return super.getTMapPoint();
    }

    @Override
    public void setPosition(float dx, float dy) {
        super.setPosition(dx, dy);
    }

    /**
     * 풍선뷰 영역을 설정한다.
     */
    @Override
    public void setCalloutRect(Rect rect) {
        super.setCalloutRect(rect);
    }

    public MarkerOverlay(Context context, String labelName, String id) {
        this.mContext = context;
        this.id=id;
        this.labelName=labelName;
        dm = new DisplayMetrics();
        WindowManager wmgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wmgr.getDefaultDisplay().getMetrics(dm);

        balloonView = new BalloonOverlayView(mContext, labelName, id);

        balloonView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        balloonView.layout(0, 0, balloonView.getMeasuredWidth(), balloonView.getMeasuredHeight());
        //balloonView.setTitle("커스텀");
        //balloonView.setSubTitle("마커");

    }

    @Override
    public void draw(Canvas canvas, TMapView mapView, boolean showCallout) {
        int x = mapView.getRotatedMapXForPoint(getTMapPoint().getLatitude(), getTMapPoint().getLongitude());
        int y = mapView.getRotatedMapYForPoint(getTMapPoint().getLatitude(), getTMapPoint().getLongitude());



        canvas.save();
        canvas.rotate(-mapView.getRotate(), mapView.getCenterPointX(), mapView.getCenterPointY());

        float xPos = getPositionX();
        float yPos = getPositionY();

        int nPos_x, nPos_y;

        int nMarkerIconWidth = 0;
        int nMarkerIconHeight = 0;
        int marginX = 0;
        int marginY = 0;

        nMarkerIconWidth = getIcon().getWidth();
        nMarkerIconHeight = getIcon().getHeight();

        nPos_x = (int) (xPos * nMarkerIconWidth);
        nPos_y = (int) (yPos * nMarkerIconHeight);

        if(nPos_x == 0) {
            marginX = nMarkerIconWidth / 2;
        } else {
            marginX = nPos_x;
        }

        if(nPos_y == 0) {
            marginY = nMarkerIconHeight / 2;
        } else {
            marginY = nPos_y;
        }

        canvas.translate(x - marginX, y - marginY);
        canvas.drawBitmap(getIcon(), 0, 0, null);
        canvas.restore();

        if (showCallout) {
            canvas.save();
            canvas.rotate(-mapView.getRotate(), mapView.getCenterPointX(), mapView.getCenterPointY());

            balloonView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            int nTempX =  x - balloonView.getMeasuredWidth() / 2;
            int nTempY =  y - marginY - balloonView.getMeasuredHeight();

            canvas.translate(nTempX, nTempY);
            balloonView.draw(canvas);

            // 풍선뷰 영역 설정
            rect.left = nTempX;
            rect.top = nTempY;
            rect.right = rect.left + balloonView.getMeasuredWidth();
            rect.bottom = rect.top + balloonView.getMeasuredHeight();

            setCalloutRect(rect);
            canvas.restore();
        }
    }
    public void setStar(int num){
        try {
            String num1 = Integer.toString(num);
            balloonView.setStar(num1);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public boolean onSingleTapUp(PointF point, TMapView mapView) {
        ArrayList<TMapPOIItem> poiItem;
        String category = result;
        poiItem = getAroundCF(getTMapPoint(), "편의점");//주변 편의시설 리스트
        //int CFnum = 0;
        //CFnum = poiItem.size();
        mapView.showCallOutViewWithMarkerItemID(getID());

      //  System.out.println("마커위치"+String.valueOf(getTMapPoint()));0

        //마커 클릭 시 편의시설 개수 가져오기 (버그로 동작안함)
//                    TMapPoint markerPoint = markerItem2.getTMapPoint();

        //CFnum을 가져오기 전에 출력되버림


        //System.out.println("편의시설 개수" + CFnum);
        //System.out.println(poiItem1.size());




        return false;
    }

    Handler mHandler = null;

    @Override
    public void startAnimation() {
        super.startAnimation();

        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (getAnimationIcons().size() > 0) {
                    if(mAnimationCount >= getAnimationIcons().size())
                        mAnimationCount = 0;

                    setIcon(getAnimationIcons().get(mAnimationCount));
                    mMapView.postInvalidate();
                    mAnimationCount++;
                    mHandler.postDelayed(this, getAniDuration());
                }
            }
        };

        mHandler = new Handler();
        mHandler.post(mRunnable);
    }


    public ArrayList  getAroundCF(TMapPoint tpoint1, String category) {

        Log.d("검색 기준 좌표", String.valueOf(tpoint1));
        TMapData tmapdata = new TMapData();
        ArrayList<TMapPOIItem> poiItem2 = new ArrayList<>();
        poiItem = new ArrayList<>();
        poiString = new ArrayList<>();
        String result = culture_find.result;

        Thread thread = new Thread(() -> {
            if(!result.isEmpty()) {
                try {
                    if (result.contains("영화관")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "영화관", 1, 60);
                        dud = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                dud++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("편의점")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "편의점", 1, 60);
                        vus = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                vus++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("오락실")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "오락실", 1, 60);
                        dh = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                dh++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("카페")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "카페", 1, 60);
                        zk = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                zk++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("도서관")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "도서관", 1, 60);
                        eh = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                eh++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("노래방")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "노래방", 1, 60);
                        sh = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                sh++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("당구장")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "당구장", 1, 60);
                        ekd = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                ekd++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("마트")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "마트", 1, 60);
                        ak = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                ak++;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("은행")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "은행", 1, 60);
                        dms = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                dms++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("피시방")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "피시방", 1, 60);
                        pc = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                pc++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("볼링장")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "볼링장", 1, 60);
                        qhf = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                qhf++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("식당")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "식당", 1, 60);
                        tlr = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                tlr++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("문화시설")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "문화시설", 1, 60);
                        ans = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                ans++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("백화점")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "백화점", 1, 60);
                        qor = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                qor++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("쇼핑센터")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "쇼핑센터", 1, 60);
                        ty = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                ty++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (result.contains("공연장")) {
                        poiItem = tmapdata.findAroundNamePOI(tpoint1, "공연장", 1, 60);
                        rhd = 0;
                        if (null != poiItem) {
                            for (int i = 0; i < poiItem.size(); i++) {
                                TMapPOIItem item = poiItem.get(i);
                                poiString.add(item.getPOIName());
                                Log.d("log", item.getPOIName());
                                rhd++;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    poiItem = tmapdata.findAroundNamePOI(tpoint1, "", 1, 60);
                    if (null != poiItem) {
                        for (int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem item = poiItem.get(i);
                            poiString.add(item.getPOIName());
                            Log.d("log", item.getPOIName());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            int []arr = {dud,vus,dh,zk,eh,sh,ekd,ak,dms,pc,qhf,tlr,ans,qor,ty,rhd};

            int total=dud+vus+dh+zk+eh+sh+ekd+ak+dms+pc+qhf+tlr+ans+qor+ty+rhd;

            for(int i=1; i < arr.length; i++){
                for(int j=i ; j >= 1; j--){

                    if(arr[j] < arr[j-1]){  //한 칸씩 왼쪽으로 이동
                        int tmp = arr[j];
                        arr[j] = arr[j-1];
                        arr[j-1] = tmp;
                    }else break;  //자기보다 작은 데이터를 만나면 그 위치에서 멈춤

                }
            }
            if(arr[15]==0){
                balloonView.setCulture("  문화시설이 없습니다");
            }
            else if(arr[15]==dud) {
                balloonView.setCulture("  영화관");
                dud=0;
            }else if(arr[15]==vus) {
                balloonView.setCulture("  편의점");
                vus=0;
            }else if(arr[15]==dh) {
                balloonView.setCulture("  오락실");
                dh=0;
            }else if(arr[15]==zk) {
                balloonView.setCulture("  카페");
                zk=0;
            }else if(arr[15]==eh) {
                balloonView.setCulture("  도서관");
                eh=0;
            }else if(arr[15]==sh) {
                balloonView.setCulture("  노래방");
                sh=0;
            }else if(arr[15]==ekd) {
                balloonView.setCulture("  당구장");
                ekd=0;
            }else if(arr[15]==ak) {
                balloonView.setCulture("  마트");
                ak=0;
            }else if(arr[15]==dms) {
                balloonView.setCulture("  은행");
                dms=0;
            }else if(arr[15]==pc) {
                balloonView.setCulture("  피시방");
                pc=0;
            }else if(arr[15]==qhf) {
                balloonView.setCulture("  볼링장");
                qhf=0;
            }else if(arr[15]==tlr) {
                balloonView.setCulture("  식당");
                tlr=0;
            }else if(arr[15]==ans) {
                balloonView.setCulture("  문화시설");
                ans=0;
            }else if(arr[15]==qor) {
                balloonView.setCulture("  백화점");
                qor=0;
            }else if(arr[15]==ty) {
                balloonView.setCulture("  쇼핑센터");
                ty=0;
            }else if(arr[15]==rhd) {
                balloonView.setCulture("  공연장");
                rhd=0;
            }

            if(arr[14]==0){
                balloonView.appendCulture(" ");
            }
            else if(arr[14]==dud) {
                balloonView.appendCulture("  영화관");
                dud=0;
            }else if(arr[14]==vus) {
                balloonView.appendCulture("  편의점");
                vus=0;
            }else if(arr[14]==dh) {
                balloonView.appendCulture("  오락실");
                dh=0;
            }else if(arr[14]==zk) {
                balloonView.appendCulture("  카페");
                zk=0;
            }else if(arr[14]==eh) {
                balloonView.appendCulture("  도서관");
                eh=0;
            }else if(arr[14]==sh) {
                balloonView.appendCulture("  노래방");
                sh=0;
            }else if(arr[14]==ekd) {
                balloonView.appendCulture("  당구장");
                ekd=0;
            }else if(arr[14]==ak) {
                balloonView.appendCulture(" 마트");
                ak=0;
            }else if(arr[14]==dms) {
                balloonView.appendCulture(" 은행");
                dms=0;
            }else if(arr[14]==pc) {
                balloonView.appendCulture(" 피시방");
                pc=0;
            }else if(arr[14]==qhf) {
                balloonView.appendCulture(" 볼링장");
                qhf=0;
            }else if(arr[14]==tlr) {
                balloonView.appendCulture(" 식당");
                tlr=0;
            }else if(arr[14]==ans) {
                balloonView.appendCulture(" 문화시설");
                ans=0;
            }else if(arr[14]==qor) {
                balloonView.appendCulture(" 백화점");
                qor=0;
            }else if(arr[14]==ty) {
                balloonView.appendCulture(" 쇼핑센터");
                ty=0;
            }else if(arr[14]==rhd) {
                balloonView.appendCulture(" 공연장");
                rhd=0;
            }

            balloonView.appendCulture(" (총 "+total+"개)");
            setTotal(total);
        });
        thread.start();
        try {
            thread.sleep(1000);
        }catch(Exception e){
            e.printStackTrace();
        }
        return poiItem;
    }

}