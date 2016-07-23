package com.hucloud.fbex.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.hucloud.fbex.chat.R;
/**
 * 해당 파일은 소유권은 신휴창에게 있습니다.
 * 현재 오픈 소스로 공개중인 버전은 AGPL을 따르는 오픈 소스 프로젝트이며,
 * 소스 코드를 수정하셔서 사용하는 경우에는 반드시 동일한 라이센스로 소스 코드를 공개하여야 합니다.
 * 만약 HUCLOUD를 상업적으로 이용하실 경우에는 라이센스를 구매하여 사용하셔야 합니다.
 * email : huttchang@gmail.com
 * 프로젝트명    : fbex
 * 작성 및 소유자 : hucloud
 * 최초 생성일   : 2016. 6. 24.
 */
public class SplashActivity extends AppCompatActivity {

    private FirebaseApp app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        app = FirebaseApp.getInstance();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 2000);
        crash();
    }

    private void crash(){
        // 크래쉬 인스턴스 생성
//        FirebaseCrash c = FirebaseCrash.getInstance(app);
    }

}
