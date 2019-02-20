package com.example.hong.boaaproject.mainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.characterActivity.CharacterFragment;
import com.example.hong.boaaproject.communityActivity.BoardFragment;
import com.example.hong.boaaproject.firstActivity.LoginActivity;
import com.example.hong.boaaproject.menu.Helper;
import com.example.hong.boaaproject.menu.UserInfoUpdateActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("메인", MainFragment.class)
                .add("공유", BoardFragment.class)
                .add("캐릭터", CharacterFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);

    }


    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, 1, 0, "회원 정보 수정");
        menu.add(0, 2, 0, "도움말");
        menu.add(0, 3, 0, "로그아웃");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:

                Intent InfoUpdateIntent = new Intent(MainActivity.this, UserInfoUpdateActivity.class);
                MainActivity.this.startActivity(InfoUpdateIntent);
                finish();
                return true;

            case 2:

                Intent HelperIntent = new Intent(MainActivity.this, Helper.class);
                MainActivity.this.startActivity(HelperIntent);
                finish();
                return true;

            case 3:

                KeepLoginActivity keepLoginActivity = new KeepLoginActivity(MainActivity.this);
                keepLoginActivity.remove();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                //TODO 로그아웃되도록

                return true;
        }
        return false;
    }

    long backPressedTime = 0;
    long FINISH_INTERVAL_TIME = 2000;

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}

