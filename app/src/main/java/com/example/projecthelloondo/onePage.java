package com.example.projecthelloondo;

import static com.example.projecthelloondo.transLocalPoint.TO_GRID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class onePage extends AppCompatActivity {
    JSONObject json = new JSONObject();
    JSONArray jsonArray = new JSONArray();
    int  tmxValue;
    int  tmpValue;
    ImageView imgTop_User;
    ImageView imgBottom_User;
    ImageView imgOuter_User;
    String wsdValue;
    String  skyValue;
    String  ptyValue;
    String  userinfo; //체감온도
    private gpsTracker GpsTracker;
    private transLocalPoint transLocalPoint;

    private String nickname;

    private int temp;  //기온(int)
    private int windChill; //체감온도(int)
    private String userConstitution; //사용자 체질 변수

    private String clothes; //사용자 맞춤형 옷차림 키워드 변수

    //DB에 사용되는 객체 선언

    //Users DB(userProfile Table)
    private  userProflieDatabase userDB;
    private userprofileDao userDao;

    //Clothes DB(tempClothes Table)
    private  tempClothesDatabase clothesDB;
    private tempClothesDao clothesDao;

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
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(onePage.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(onePage.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(onePage.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(onePage.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(onePage.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(onePage.this, REQUIRED_PERMISSIONS,
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

        AlertDialog.Builder builder = new AlertDialog.Builder(onePage.this);
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
        setContentView(R.layout.activity_one_page);

        Intent intent = getIntent();
        nickname = intent.getStringExtra("nickname");

        TextView kakaonick = findViewById(R.id.userKakao);
        kakaonick.setText(nickname + " 님의 추천 옷차림");

        // GPS 체크 로직
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {

            checkRunTimePermission();
        }

        GpsTracker = new gpsTracker(onePage.this);

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


        // URL 설정.
        String service_key = "Jk%2BdRv01fYdegCya2cQfBvQrfkGtkcz1mA%2FD3dRSTyelyRtbEgEs9%2FDlkBe1p5kijysEBm0PiDFsZpakW%2BuvVQ%3D%3D";
        String num_of_rows = "500";
        String page_no = "1";
        String date_type = "JSON";
        String base_date = formatYDM;
        String base_time = "0800";
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
        protected String doInBackground(Void... params) {
            String result; // 요청 결과를 저장할 변수.
            requestHttpConnection requestHttpURLConnection = new requestHttpConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.

            return result;

        }

        @Override
        protected  void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("onPostEx", "출력 값:" + s);

            List<Integer> listA = null;
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


                for (int i = 0; i < jsonArray.length(); i++) {
                    json = jsonArray.getJSONObject(i);
                    int fcstValue ;
                    try{
                        fcstValue = json.getInt("fcstValue");
                    } catch(Exception e){
                         continue;
                    }
                    String fcstValue2 = json.getString("fcstValue");
                    String category = json.getString("category");
                    String baseDate = json.getString("baseDate");
                    String fcstDate = json.getString("fcstDate");
                    String fcstTime = json.getString("fcstTime");

                    //최대 온도 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("TMX")) {
                        tmxValue = fcstValue;
                        System.out.println("최대값:"+ tmxValue);


                    }
                    //현재 온도 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("TMP")&& fcstTime.equals(nowTime1)) {
                        tmpValue = fcstValue;
                        System.out.println("현재기온:"+ tmpValue+"° ");


                    }

                    //현재 풍속 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("WSD")) {
                        wsdValue = fcstValue2;
                        System.out.println(wsdValue);


                    }

                    //현재 강수형태 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("PTY")) {
                        ptyValue = fcstValue2;
                        System.out.println("강수형태5:" + ptyValue);
                    }

                    ////현재 하늘상태 값 꺼내기
                    if (baseDate.equals(fcstDate) && category.equals("SKY")) {
                        skyValue = fcstValue2;
                        System.out.println("하늘상태6:" + skyValue);
                    }


                }




            } catch (JSONException e) {
                e.printStackTrace();

            }
            //현재 온도
            String test4= String.valueOf(tmpValue);
            TextView current = (TextView) findViewById(R.id.currentinfo);
            current.setText(test4+"°");


            double test = Math.pow(Double.parseDouble(String.valueOf(wsdValue)), 0.16);
            BigDecimal operationTest = BigDecimal.valueOf(13.12 + 0.6215 * tmxValue - 11.37 * test + 0.3965 * test *  tmxValue);
            BigDecimal operation = operationTest.setScale(0, BigDecimal.ROUND_DOWN);
            System.out.println("최종 체감온도:" + operation);


            userinfo = String.valueOf(operation);
            TextView userinfoTest = (TextView)findViewById(R.id.userinfo);
            userinfoTest.setText("체감온도:"+" "+userinfo+ "° /");



            if (skyValue != null) {
                switch (skyValue) {

                    case "1":
                        ImageView clear = (ImageView) findViewById(R.id.sky);
                        clear.setImageResource(R.drawable.clear);
                        TextView clearText = (TextView)findViewById(R.id.sktText);
                        clearText.setText("    "+"맑음");
                        break;
                    case "3":
                        ImageView nebul = (ImageView) findViewById(R.id.sky);
                        nebul.setImageResource(R.drawable.nebulousness);
                        TextView nebulText = (TextView)findViewById(R.id.sktText);
                        nebulText.setText("    "+"구름많음");
                        break;
                    case "4":
                        ImageView cloudy = (ImageView) findViewById(R.id.sky);
                        cloudy.setImageResource(R.drawable.cloudy);
                        TextView cloudyText = (TextView)findViewById(R.id.sktText);
                        cloudyText.setText("    "+"구름");

                }
            }


            if (ptyValue != "0") {
                switch (ptyValue) {

                    case "1":
                        ImageView rain = (ImageView) findViewById(R.id.sky);
                        rain.setImageResource(R.drawable.rain);
                        TextView rainText = (TextView)findViewById(R.id.sktText);
                        rainText.setText("    "+"비");
                        break;

                    case "2":
                        ImageView rainAndSnow = (ImageView) findViewById(R.id.sky);
                        rainAndSnow.setImageResource(R.drawable.rainandsnow);
                        TextView rainAndSnowText = (TextView)findViewById(R.id.sktText);
                        rainAndSnowText.setText("    "+"눈,비");
                        break;

                    case "3":
                        ImageView snow = (ImageView) findViewById(R.id.sky);
                        snow.setImageResource(R.drawable.snow);
                        TextView snowText = (TextView)findViewById(R.id.sktText);
                        snowText.setText("    "+"눈");
                        break;

                    case "4":
                        ImageView showers = (ImageView) findViewById(R.id.sky);
                        showers.setImageResource(R.drawable.showers);
                        TextView showersText = (TextView)findViewById(R.id.sktText);
                        showersText.setText("    "+"소나기");
                }

            }

            windChill = Integer.parseInt(userinfo);
            temp = tmpValue;
            System.out.println("현??:"+windChill);


            //객체 id 연결
            imgTop_User = (ImageView)findViewById(R.id.imgTop_User);
            imgBottom_User = (ImageView)findViewById(R.id.imgBottom_User);
            imgOuter_User = (ImageView)findViewById(R.id.imgOuter_User);
            TextView currentPit = (TextView)findViewById(R.id.currentPit);

            //Users DB 생성
            userDB = Room.databaseBuilder(getApplicationContext(), userProflieDatabase.class, "Users")
                    .fallbackToDestructiveMigration() //스키마(DB) 버전 변경 가능
                    .allowMainThreadQueries() //Main Thread에서 DB에 IO 가능하게 함
                    .build();

            userDao = userDB.getUserProfileDao(); //인터페이스 객체 할당
            userConstitution = userDao.selectConstitution(nickname); //닉네임으로 해당 체질정보 가져오기

            //사용자의 체질 정보에 따라 체감 온도를 변경한다.
            switch (userConstitution) {
                case "Hot":
                    windChill += 2;
                    break;

                case "Normal":
                    break;

                case "Cold":
                    windChill -= 2;
                    break;
            }
            System.out.println("사용자 맞춤형 체감온도 : " + windChill);

            //체감온도, 기온 범위 한정(17~31)
            if(windChill > 29 || temp > 29){
                windChill = 29;
                temp = 29;
            }

            if(windChill < 19 || temp < 19){
                windChill = 19;
                temp = 19;
            }

            //Clothes DB에서 체감온도와 같은 temp 칼럼 값에 해당하는 옷차림 키워드를 가져온다.
            clothesDB = Room.databaseBuilder(getApplicationContext(), tempClothesDatabase.class, "Clothes")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();

            clothesDao = clothesDB.getTempClothesDao(); //인터페이스 객체 할당
            clothes = clothesDao.SelectClothes(windChill); //체감온도에 해당하는 옷차림 키워드 가져오기

            //가져온 키워드를 배열에 split해서 getWebsite메소드에 파라미터로 사용한다.
            clothes = clothesDao.SelectClothes(windChill);
            String[] arrClothes = clothes.split(",");
            String userTop = arrClothes[0];
            String userbottom = arrClothes[1];

            //아우터가 없는 온도 고려
            try{
                String userOuter = arrClothes[2];

                getWebsite(userTop, imgTop_User);
                getWebsite(userbottom, imgBottom_User);
                getWebsite(userOuter, imgOuter_User);
            } catch (Exception e){
                getWebsite(userTop, imgTop_User);
                getWebsite(userbottom, imgBottom_User);
            }

            //Clothes DB에서 체감온도와 같은 temp 칼럼 값에 해당하는 옷차림 카테고리를 가져온다.
            System.out.println("???:"+windChill);
            String category = clothesDao.SelectCategory(windChill);
            System.out.println(category);
            currentPit.setText(category);
        }

        //쇼핑몰 이미지 크롤링
        private void getWebsite(String item, ImageView imgView){

            final StringBuilder builder_img = new StringBuilder();
            new Thread((Runnable)() -> {
                try{
                    //쇼핑몰 사이트 : https://besimple.co.kr/index.html
                    String url = "https://besimple.co.kr/product/search.html?banner_action=&keyword=" + item;

                    //크롤링할 쇼핑몰 주소
                    Document doc = Jsoup.connect(url).get();
                    Elements links_img = doc.select("img[src$=.jpeg]");

                    boolean isEmptyImg = links_img.isEmpty();
                    Log.d("qPage", "img 태그가 들어왔는지 확인" + links_img);

                    //크롤링한 값 모두가 null이 아니면 필요한 값만 가져오기
                    if (isEmptyImg == false)
                    {
                        String imgSrc = links_img.get(0).toString();
                        String[] arr = imgSrc.split("\"");
                        builder_img.append(arr[1]);
                    }
                } catch (IOException e){
                    Log.d("qPage","크롤링해온 값이 들어오지 않음");
                }

                runOnUiThread(() -> {
                    sendImageRequest("https://" + builder_img.toString(), imgView);
                });

            }).start(); //이 이후에 위에 쓰였던 값은 가지고 오는 것이 불가능, 위 코드에 작성했더라도 null로 초기화됨
        }

        //이미지뷰에 이미지를 세팅
        public void sendImageRequest(String url, ImageView imageView) {
            ImageLoadTask task = new ImageLoadTask(url, imageView);
            task.execute();
        }





    }


    @Override
    public  boolean onTouchEvent(MotionEvent event){
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            // 다음 화면으로 넘어갈 클래스 지정한다.
            Intent intent = new Intent(getApplicationContext(), youtudeplayer.class);
            startActivity(intent);  // 다음 화면으로 넘어간다.
        }

        return super.onTouchEvent(event);

    }

}


