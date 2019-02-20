package com.example.hong.boaaproject.mainActivity;

import android.content.Context;
import android.content.SharedPreferences;

public class KeepLoginActivity {

    Context context;
    private String userID;
    SharedPreferences sharedPreferences;

    public void remove() {
        // 로그인 정보 전부 삭제 (로그아웃)
        sharedPreferences.edit().clear().commit();
    }

    public String getUserID() {
        // 로컬 내 저장된 아이디 값 받아옴
        userID = userID = sharedPreferences.getString("userID", "");
        return userID;
    }

    public void setUserID(String userID) {
        // 로컬 내 아이디 값을 userID 라는 태그로 저장
        this.userID = userID;
        sharedPreferences.edit().putString("userID", userID).commit();
    }

    public KeepLoginActivity(Context context) {
        // 로컬 내 값들을 저장할 위치 (userInfo)
        this.context = context;
        sharedPreferences = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }
}
