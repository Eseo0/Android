package com.example.projecthelloondo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Dao;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ipsec.ike.IkeSessionCallback;
import android.os.Bundle;
import android.os.UserManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;


import kakao.a.e;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;


public class titlePage extends AppCompatActivity {
    private static final String TAG = "유저";
    private View joinBtn;
    private View loginBtn;
    private View logoutBtn;
    private  userProflieDatabase db;
    private userprofileDao userDao;
    private userProflie proflie = new userProflie();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        joinBtn = findViewById(R.id.join);
        loginBtn = findViewById(R.id.login);
        logoutBtn = findViewById(R.id.logout);
        Log.d("getKeyHash", " " + getKeyHash(titlePage.this));


        //데이터베이스 삭제
        getApplicationContext().deleteDatabase("Users");
        getApplicationContext().deleteDatabase("Clothes");

        //카카오가 설치 되어 있는지 확인하는 메서드 또한 카카오에서 제공 콜백 객체를 이용함
        Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                //이때 토큰이 전달이 되면 로그인이 성공한 것 토큰이 전달되지 않았다면 로그인실패
                if (oAuthToken != null) {
                    Log.i(TAG, oAuthToken.getAccessToken() + " " + oAuthToken.getRefreshTokenExpiresAt());
                    updateKakaoJoinUi();


                }
                if (throwable != null) {
                    Log.w(TAG, "invoke" + throwable.getLocalizedMessage());

                }

                return null;
            }
        };

        //로그인버튼
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(titlePage.this)){
                    UserApiClient.getInstance().loginWithKakaoTalk(titlePage.this, callback);

                }else{
                    UserApiClient.getInstance().loginWithKakaoTalk(titlePage.this, callback);
                    updateKakaoLoginUi();
                }
            }

        });


        //로그아웃버튼
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserApiClient.getInstance().unlink(new Function1<Throwable, Unit>() {
                    @Override
                    public Unit invoke(Throwable throwable) {
                        updateKakaoLoginUi();
                        return null;
                    }
                });
            }

        });


        //회원가입 버튼
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(titlePage.this)){
                    UserApiClient.getInstance().loginWithKakaoTalk(titlePage.this, callback);


                }else{
                    UserApiClient.getInstance().loginWithKakaoAccount(titlePage.this, callback);


                }

            }


        });



    }

    //로그인 성공시
    public void  updateKakaoLoginUi(){
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {


                if(user != null){
                    Intent intent = new Intent(titlePage.this, onePage.class);
                    intent.putExtra("nickname",user.getKakaoAccount().getProfile().getNickname());
                    startActivity(intent);
                }

                if(throwable != null){
                    Log.w(TAG, "invoke" + throwable.getLocalizedMessage());
                }

                return null;
            }
        });

    }

            public  void updateKakaoJoinUi(){
                UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {

                    @Override
                    public Unit invoke(User user, Throwable throwable) {

                        if(user != null){


                            Log.i(TAG, "id" + user.getId()); // 유저의 고유 아이디를 불러옵니다.
                            Log.i(TAG, "nickname" + user.getKakaoAccount().getProfile().getNickname());
                            Log.i(TAG, "userImage" + user.getKakaoAccount().getProfile().getProfileImageUrl());
                            Log.i(TAG, "gender" + user.getKakaoAccount().getGender());
                            Log.i(TAG, "ago_range" + user.getKakaoAccount().getAgeRange());

                            proflie.setKakao_id(String.valueOf(user.getId()));
                            proflie.setNickname(String.valueOf(user.getKakaoAccount().getProfile().getNickname()));
                            proflie.setGender(String.valueOf(user.getKakaoAccount().getGender()));
                            proflie.setAge_range(String.valueOf(user.getKakaoAccount().getAgeRange()));
                            proflie.setProflieImg(String.valueOf(user.getKakaoAccount().getProfile().getProfileImageUrl()));

                            db= Room.databaseBuilder(getApplicationContext(),userProflieDatabase.class,"Users")
                                    .fallbackToDestructiveMigration()
                                    .allowMainThreadQueries()
                                    .build();


                            userDao = db.getUserProfileDao();


                            if(userDao.getkakaoid().contains(String.valueOf(user.getId()))) {

                                Toast.makeText(getApplicationContext(), "있는 정보입니다", Toast.LENGTH_SHORT).show();
                            }

                            else{
                                userDao.insert(proflie);
                                Intent intent = new Intent(titlePage.this, qPage.class);
                                intent.putExtra("nickname",String.valueOf(user.getKakaoAccount().getProfile().getNickname()));
                                intent.putExtra("proflie",String.valueOf(user.getKakaoAccount().getProfile().getProfileImageUrl()));
                                startActivity(intent);

                            }







                        }





                        return null;
                    }
                });


            }







    //키 해시값을 받는다.
    public static String getKeyHash(final Context context){
        PackageManager pm = context.getPackageManager();
        try{
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if(packageInfo == null)
                return  null;
            for(Signature signature : packageInfo.signatures){
                try{
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return  android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
                }catch (NoSuchAlgorithmException e){
                    e.printStackTrace();
                }

            }
        }catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
        }
        return  null;
    }

}