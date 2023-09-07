package com.example.projecthelloondo;

import android.app.Application;
import com.kakao.sdk.common.KakaoSdk;

public class kakaoApplication extends Application {
    @Override
    public void onCreate(){
        super.onCreate();
        KakaoSdk.init(this,"6747dfb30f9b22bac6044b45aff2596b");
    }
}

