package com.example.projecthelloondo;

import static com.example.projecthelloondo.transLocalPoint.TO_GRID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class qPage extends AppCompatActivity {
    private String nickname;
    private String proflie;
    int tmpValue;
    int wsdValue;
    String  skyValue;
    String  ptyValue;
    String oprationTest1;
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    private gpsTracker GpsTracker;
    private transLocalPoint transLocalPoint;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final String[] REQUIRED_PERMISSIONS = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,

    };

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(qPage.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(qPage.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(qPage.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(qPage.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(qPage.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(qPage.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress(double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(qPage.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qpage);


        //카카오닉네임
        Intent intent = getIntent();
        nickname = intent.getStringExtra("nickname");
        TextView getNickname = findViewById(R.id.getNickName);
        getNickname.setText(nickname+" "+"님 :)");

        //카카오프로필 이미지
        Intent intent1 = getIntent();
        proflie = intent1.getStringExtra("proflie");
        ImageView img = (ImageView)findViewById(R.id.getimg);
        String imgstr = proflie;
        Glide.with(this).load(imgstr).into(img);

        gpsTracker gpsTracker;

        // GPS 체크 로직
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }

        GpsTracker = new gpsTracker(qPage.this);

        double latitude = GpsTracker.latitude;
        double longitude = GpsTracker.longitude;

        String address = getCurrentAddress(latitude, longitude);
        Log.d("adress", "주소는? " + address);

        Log.d("gps", "위도는? " + latitude);
        Log.d("gps", "경도는? " + longitude);
        //GPS END


        //기상청 격자 좌표 변환
        transLocalPoint = new transLocalPoint();
        com.example.projecthelloondo.transLocalPoint.LatXLngY tmp = transLocalPoint.convertGRID_GPS(TO_GRID, latitude, longitude);
        Log.e(">>", "x=" + tmp.x + ",y=" + tmp.y);

        //시간데이터
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat mFormatYDM = new SimpleDateFormat("yyyyMMdd");//현재 날짜 데이터 폼
        String formatYDM = mFormatYDM.format(calendar.getTime());
        System.out.println("Today:" + formatYDM);

        calendar.add(Calendar.DATE, -1);
        formatYDM = mFormatYDM.format(calendar.getTime());
        System.out.println("Yesterday:" + formatYDM);


        // URL 설정.

        String service_key = "Jk%2BdRv01fYdegCya2cQfBvQrfkGtkcz1mA%2FD3dRSTyelyRtbEgEs9%2FDlkBe1p5kijysEBm0PiDFsZpakW%2BuvVQ%3D%3D";
        String num_of_rows = "500";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = formatYDM;
        String base_time = "0500";
        String nx = String.format("%.0f", tmp.x);
        String ny = String.format("%.0f", tmp.y);


        String url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst?" +
                "serviceKey=" + service_key +
                "&numOfRows=" + num_of_rows +
                "&pageNo=" + page_no +
                "&dataType=" + date_type +
                "&base_date=" + base_date +
                "&base_time=" + base_time +
                "&nx=" + nx +
                "&ny=" + ny;

        Log.d("url", url);


        NetworkTask networkTask = new NetworkTask(url, null);
        networkTask.execute();


    }

    public class NetworkTask extends AsyncTask<Void, Void, String> {

        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {

            this.url = url;
            this.values = values;
        }


        @Override
        protected String doInBackground(Void... voids) {
            String result; // 요청 결과를 저장할 변수.
            requestHttpConnection requestHttpURLConnection = new requestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            TextView info = findViewById(R.id.getinpo);

            Log.d("onPostEx", "출력 값:" + s);

            try {
                // response 키를 가지고 데이터를 파싱
                json = new JSONObject(s);
                String response = json.getString("response");

                // response 로 부터 body 찾기
                JSONObject json1 = new JSONObject(response);
                String body = json1.getString("body");

                // body 로 부터 items 찾기
                JSONObject json2 = new JSONObject(body);
                String items = json2.getString("items");


                // items로 부터 itemlist 를 받기
                JSONObject json3 = new JSONObject(items);
                jsonArray = json3.getJSONArray("item");

                // 현재 시간 구하기
                SimpleDateFormat baseTime = new SimpleDateFormat("HH00");

                Date now = new Date();
                String nowTime1 = baseTime.format(now);
                System.out.println(nowTime1);



                for(int i=0; i<jsonArray.length(); i++){
                    json = jsonArray.getJSONObject(i);
                    int fcstValue ;
                    try{
                        fcstValue = json.getInt("fcstValue");
                    } catch(Exception e){
                        continue;
                    };
                    String fcstValue2 = json.getString("fcstValue");
                    String category =json.getString("category");
                    String baseDate = json.getString("baseDate");
                    String fcstDate = json.getString("fcstDate");
                    String fcstTime = json.getString("fcstTime");



                    //현재 온도 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("TMP")&& fcstTime.equals(nowTime1)) {
                        tmpValue = fcstValue;
                        System.out.println(tmpValue);

                    }
                    //현재 풍속 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("WSD")) {
                        wsdValue = fcstValue;
                        System.out.println("풍속:" + wsdValue);

                    }

                    //현재 강수형태 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("PTY")) {
                        ptyValue = fcstValue2;
                        System.out.println("강수형태:" + ptyValue);
                    }

                    ////현재 하늘상태 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("SKY")) {
                        skyValue = fcstValue2;
                        System.out.println("하늘상태:" + skyValue);
                    }


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            double test = Math.pow(Double.parseDouble(String.valueOf(wsdValue)), 0.16);
            BigDecimal operationTest = BigDecimal.valueOf(13.12 + 0.6215 * tmpValue - 11.37 * test + 0.3965 * test * tmpValue);
            BigDecimal operation = operationTest.setScale(0, BigDecimal.ROUND_DOWN);

            System.out.println("최종 체감온도:" + operation);
            oprationTest1 = String.valueOf(operation);
            TextView getinfo = (TextView) findViewById(R.id.getinpo);
            getinfo.setText(oprationTest1+" "+"도");








        }
    }

    @Override
        public  boolean onTouchEvent(MotionEvent event){
            int action = event.getAction();

            if (action == MotionEvent.ACTION_DOWN) {
                // 다음 화면으로 넘어갈 클래스 지정한다.
                Intent intent = new Intent(getApplicationContext(), qPage2.class);
                intent.putExtra("operation",oprationTest1);
                intent.putExtra("nickname",nickname);
                startActivity(intent);  // 다음 화면으로 넘어간다.
        }

            return super.onTouchEvent(event);

        }

}