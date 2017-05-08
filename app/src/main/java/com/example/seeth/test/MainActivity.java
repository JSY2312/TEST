package com.example.seeth.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapMarkerItem;
import com.skp.Tmap.TMapPOIItem;
import com.skp.Tmap.TMapPoint;
import com.skp.Tmap.TMapPolyLine;
import com.skp.Tmap.TMapView;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements TMapView.OnLongClickListenerCallback{

    private TMapView tmapview;
    private String mapKey = "795385d9-f3d0-3d51-abe5-bbb0c6c82258";
    private TMapData tmapdata = new TMapData(); //POI검색, 경로검색 등의 지도데이터를 관리하는 클래스
    private Context mContext = null;
    private ArrayList<TMapPoint> passList; // 경유지 좌표를 저장할 리스트 선언

    private String address;
    private Double lat = null;
    private Double lon = null;

    /**  마커의 위치정보를 저장하는 배열(DB연동) **/
    private ArrayList<MapPoint> m_mapPoint = new ArrayList<MapPoint>();

    //마커의 중복을 허용하기 위한 마커의 id
    private ArrayList<String> mArrayMarkerID = new ArrayList<String>();
    private static int mMarkerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_main);

        mContext = this;

        /* 릴레이티브 레이아웃에 지도를 출력 */
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.tmap);
        tmapview = new TMapView(this);

        tmapview.setSKPMapApiKey(mapKey);
        tmapview.setIconVisibility(true);
        tmapview.setZoomLevel(15);
        tmapview.setCompassMode(false);
        tmapview.setTrackingMode(true);
        tmapview.setLocationPoint(128.49894, 35.87436); //현재 내 위치
        tmapview.setCenterPoint(128.49894, 35.87436);   //화면 시작지점
        layout.addView(tmapview);

        TMapPoint start = new TMapPoint(128.49894, 35.87436);
        TMapPoint end = new TMapPoint(127.49851, 34.2154);
        passList = new ArrayList<TMapPoint>(); // 리스트 객체 생성
        // 검색값으로 넘어온 위도, 경도값 리스트에 저장
        TMapPoint s = new TMapPoint(128.49894, 35.87436);
        TMapPoint e = new TMapPoint(128.50814, 35.87436);
        passList.add(new  TMapPoint(127.49851, 34.2154));
        // 자동차 경로탐색 메소드
        tmapdata.findPathDataWithType(TMapData.TMapPathType.CAR_PATH, start, end, passList,0,
                new TMapData.FindPathDataListenerCallback() {
                    @Override
                    public void onFindPathData(TMapPolyLine polyLine) {
                        tmapview.addTMapPath(polyLine);
                    }
                });

    }

    public void addMarker(double Latitude, double Longitude) {//지도에 마커 추가
        TMapPoint poi = new TMapPoint(Latitude, Longitude);
        TMapMarkerItem APmarker = new TMapMarkerItem();
        Bitmap bitmap = null;

        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR);
        int min = now.get(Calendar.MINUTE);
        int sec = now.get(Calendar.SECOND);
        String nowTime = hour + "시 " + min + "분 " + sec + "초";

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.add_marker);
        //add_marker는 지도에 추가할 마커 이미지입니다.
        APmarker.setTMapPoint(poi);
        APmarker.setName("테스트");
        APmarker.setVisible(APmarker.VISIBLE);
        APmarker.setIcon(bitmap);
        APmarker.setCanShowCallout(true); //AP에 풍선뷰 사용 여부
        APmarker.setCalloutTitle("해당 장소의 이름");
        APmarker.setCalloutSubTitle(nowTime);       //풍선뷰 보조메세지
//        tItem.setCalloutLeftImage(bitmap);  //풍선뷰의 왼쪽 이미지 지정 //오른쪽은 RIGHT

        bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.car);
        APmarker.setCalloutRightButtonImage(bitmap);

        String strID = String.format("pmarker%d", mMarkerID++);
        tmapview.addMarkerItem(strID, APmarker);
        mArrayMarkerID.add(strID);

        //풍선뷰 선택할 때 나타나는 이벤트
        tmapview.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem markerItem) {

                lat = markerItem.latitude;
                lon = markerItem.longitude;

                //위도, 경도로 주소 검색하기
                tmapdata.convertGpsToAddress(lat, lon, new TMapData.ConvertGPSToAddressListenerCallback() {
                    @Override
                    public void onConvertToGPSToAddress(String strAddress) {
                        address = strAddress;
                    }
                });
                Toast.makeText(MainActivity.this, "주소 : " + address, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onLongPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint) {
        lat = Math.round(tMapPoint.getLatitude()*10000)/10000.0;     //경도 4자리 반올림
        lon = Math.round(tMapPoint.getLongitude()*10000)/10000.0;     //위도 4자리 반올림
        String add = "경도 : " + lat + "\n위도 : " + lon;

        m_mapPoint.add(new MapPoint(lat, lon));
        for(int i=0; i<m_mapPoint.size(); i++) {
            addMarker(m_mapPoint.get(i).getLatitude(), m_mapPoint.get(i).getLongitude());
        }

        String s = "사이즈 " + m_mapPoint.size();
        Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
    }

}
