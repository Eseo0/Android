package com.example.projecthelloondo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class  qPage2 extends AppCompatActivity {

    private String nickname;
    private String userConstitution; //사용자 체질 변수
    private int opri; //체감 온도 변수(int)

    //DB에 사용되는 객체 선언
    private  userProflieDatabase userDB;
    private userprofileDao userDao;

    private  tempClothesDatabase clothesDB;
    private tempClothesDao clothesDao;


    //위젯 객체 선언
    private TextView txtQuestion;
    private RadioGroup radioGroup;
    private ImageView btnNext;
    private ImageView imgTop_Hot;
    private ImageView imgBottom_Hot;
    private ImageView imgOuter_Hot;
    private ImageView imgTop_Normal;
    private ImageView imgBottom_Normal;
    private ImageView imgOuter_Normal;
    private ImageView imgTop_Cold;
    private ImageView imgBottom_Cold;
    private ImageView imgOuter_Cold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qpage2);

        //체감온도 받아옴
        Intent intent = getIntent();
        String opr=  intent.getStringExtra("operation");
        opri = Integer.parseInt(opr);
        System.out.println(opr);

        //카카오 닉네임 인텐트로 받아옴
        nickname = intent.getExtras().getString("nickname");


        //Clothes 데이터베이스 생성 시 초기 데이터를 삽입하는 콜백 메서드
        RoomDatabase.Callback ClothesCallback = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                // 초기 데이터 삽입
                //초기 데이터 삽입 (카테고리 추가해서 수정)
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (17, '와플 반팔티,핀턱 와이드 생지 데님,바람막이 블루종', '반팔티,청바지,블루종')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (18, '꽈배기 니트 슬리브리스,우아 와이드 투턱 슬랙스,여름 린넨 100 자켓', '나시,슬랙스,자켓')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (19, '몽글 스트랩 슬리브리스,핀턱 코튼 롱 스커트,클래식 린넨 가디건', '나시,롱스커트,가디건')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (20, '코튼 스트랩 나시 니트,핀턱 와이드 생지 데님,린넨 발레리나 가디건', '나시,청바지,가디건')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (21, '여리 골지 끈나시,써머 파우더 연청 데님,크롭 스트라이프 긴팔 셔츠', '나시,청바지,긴팔 셔츠')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (22, '오가닉 린넨 긴팔 티셔츠,써머 비조 와이드 코튼 슬랙스', '긴팔티,슬랙스')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (23, '스퀘어 퍼프 여리 블라우스,내추럴 여리 뒷밴딩 팬츠', '블라우스,면바지')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (24, '코튼 골드버튼 퍼프 반팔 블라우스,블랙 써머 슬림 팬츠', '반팔 블라우스,면바지')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (25, '크롭 캔디 반팔 셔츠,생지 핀턱 데님 반바지', '반팔 셔츠,반바지')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (26, '탄탄 써머 데일리 반팔티, 코튼 미디 핀턱 스커트', '반팔티,스커트')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (27, '스퀘어넥 스트라이프 반팔티,고퀄 탄탄 코튼 반바지', '반팔티,반바지')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (28, '와플 슬리브리스 니트,고퀄) 데님 스커트', '나시,스커트')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (29, '꽈배기 니트 슬리브리스,핀턱 데일리 데님 반바지', '나시,반바지')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (30, '코튼 스트랩 나시 니트,코튼 숏 반바지', '나시,반바지')");
                db.execSQL("INSERT INTO tempClothes (`temp`, clothes, category) VALUES (31, '여리 골지 끈나시,생지 핀턱 데님 반바지', '나시,반바지')");
            }
        };

        clothesDB = Room.databaseBuilder(getApplicationContext(), tempClothesDatabase.class, "Clothes")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .addCallback(ClothesCallback)
                .build();

        clothesDao = clothesDB.getTempClothesDao(); //인터페이스 객체 할당
        clothesDao.SelectAll();

        //객체 id 연결
        txtQuestion = findViewById(R.id.txtQuestion);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        btnNext = (ImageView) findViewById(R.id.btnNext);
        btnNext.setImageResource(R.drawable.nextbtn);

        imgTop_Hot = (ImageView) findViewById(R.id.imgTop_Hot);
        imgBottom_Hot = (ImageView) findViewById(R.id.imgBottom_Hot);
        imgOuter_Hot = (ImageView) findViewById(R.id.imgOuter_Hot);

        imgTop_Normal = (ImageView) findViewById(R.id.imgTop_Normal);
        imgBottom_Normal = (ImageView) findViewById(R.id.imgBottom_Normal);
        imgOuter_Normal = (ImageView) findViewById(R.id.imgOuter_Normal);

        imgTop_Cold = (ImageView) findViewById(R.id.imgTop_Cold);
        imgBottom_Cold = (ImageView) findViewById(R.id.imgBottom_Cold);
        imgOuter_Cold = (ImageView) findViewById(R.id.imgOuter_Cold);


        txtQuestion.setText(nickname + "님의 어제 옷차림을 고르세요!");


        //선택된 라디오 버튼에 해당하는 타입 정보를 userConstitution 변수에 저장
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                switch (checkedId) {
                    case R.id.rbtnHot:
                        userConstitution = "Hot";
                        break;

                    case R.id.rbtnNormal:
                        userConstitution = "Normal";
                        break;

                    case R.id.rbtnCold:
                        userConstitution = "Cold";
                        break;
                }
            }
        });




        //Next 버튼 클릭시 체질 정보를 userProfile 테이블에 constitution칼럼에 update한 후 종료하고 onePage로 이동한다.
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DB 생성
                userDB = Room.databaseBuilder(getApplicationContext(), userProflieDatabase.class, "Users")
                        .fallbackToDestructiveMigration() //스키마(DB) 버전 변경 가능
                        .allowMainThreadQueries() //Main Thread에서 DB에 IO 가능하게 함
                        .build();

                userDao = userDB.getUserProfileDao();; //인터페이스 객체 할당

                //userProfile 테이블에 constitution 칼럼에 update한다.(파라미터로 사용자 닉네임과 userContitution을 넘긴다)
                switch (userConstitution) {
                    case "Hot":
                        userDao.update_Constitution(userConstitution, nickname);
                        break;

                    case "Normal":
                        userDao.update_Constitution(userConstitution, nickname);
                        break;

                    case "Cold":
                        userDao.update_Constitution(userConstitution, nickname);
                        break;
                }

                //닉네임과 체감온도 넘기고 onePage로 이동 후 종료
                if(view == btnNext){
                    Intent intent = new Intent(qPage2.this, onePage.class);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("operation", opr);
                    startActivity(intent);
                    finish();
                }
            }
        });


        //어제 (int)체감온도에 해당하는 옷차림 키워드를 select 해온다.
        //체감온도 범위 한정(17~31)
        if(opri > 29){
            opri = 29;
        }

        if(opri < 19){
            opri = 19;
        }

        //DB 연결
        clothesDB = Room.databaseBuilder(getApplicationContext(), tempClothesDatabase.class, "Clothes")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        clothesDao = clothesDB.getTempClothesDao(); //인터페이스 객체 할당


        //가져온 키워드를 배열에 split해서 getWebsite메소드에 파라미터로 사용한다.

        //보통 타입
        String clothesNormal = clothesDao.SelectClothes(opri);
        String[] arrClothesNormal = clothesNormal.split(",");
        String topNormal = arrClothesNormal[0];
        String bottomNormal = arrClothesNormal[1];

        //아우터가 없는 온도 고려
        try{
            String outerNormal = arrClothesNormal[2];

            getWebsite(topNormal, imgTop_Normal);
            getWebsite(bottomNormal, imgBottom_Normal);
            getWebsite(outerNormal, imgOuter_Normal);
        } catch (Exception e){
            getWebsite(topNormal, imgTop_Normal);
            getWebsite(bottomNormal, imgBottom_Normal);
        }

        //더위를 타는 타입
        String clothesHot = clothesDao.SelectClothes(opri + 2);
        String[] arrClothesHot = clothesHot.split(",");
        String topHot = arrClothesHot[0];
        String bottomHot = arrClothesHot[1];

        try{
            String outerHot = arrClothesHot[2];
            getWebsite(topHot, imgTop_Hot);
            getWebsite(bottomHot, imgBottom_Hot);
            getWebsite(outerHot, imgOuter_Hot);
        } catch (Exception e){
            getWebsite(topHot, imgTop_Hot);
            getWebsite(bottomHot, imgBottom_Hot);
        }

        //추위를 타는 타입
        String clothesCold = clothesDao.SelectClothes(opri - 2);
        String[] arrClothesCold = clothesCold.split(",");
        String topCold = arrClothesCold[0];
        String bottomCold = arrClothesCold[1];
        try{
            String outerCold = arrClothesCold[2];

            getWebsite(topCold, imgTop_Cold);
            getWebsite(bottomCold, imgBottom_Cold);
            getWebsite(outerCold, imgOuter_Cold);
        } catch (Exception e){
            getWebsite(topCold, imgTop_Cold);
            getWebsite(bottomCold, imgBottom_Cold);
        }
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