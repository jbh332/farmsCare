package com.example.hong.boaaproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.hong.boaaproject.firstActivity.IndexActivity;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.example.hong.boaaproject.mainActivity.MainActivity;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final KeepLoginActivity keepLoginActivity = new KeepLoginActivity(SplashActivity.this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                // 기기 내에 로그인 저장 정보 있으면 MainActivity 로 이동, 없으면 IndexActivity 부터 시작.
                if (keepLoginActivity.getUserID().length() == 0) {

                    Intent nextAct = new Intent(SplashActivity.this, IndexActivity.class);
                    startActivity(nextAct);
                    finish();

                } else {
                    Intent nextAct = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(nextAct);
                    finish();
                }

            }
        }, 3000); // 3초 뒤에 다음 화면으로 넘어감.

    }
}
