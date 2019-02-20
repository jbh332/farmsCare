package com.example.hong.boaaproject.firstActivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivityIndexBinding;
import com.pm10.library.CircleIndicator;

public class IndexActivity extends AppCompatActivity {

    ActivityIndexBinding q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        q = DataBindingUtil.setContentView(this, R.layout.activity_index);

        viewPagerCall(); // 뷰페이져 부르는 함수



        q.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent joinIntent = new Intent(IndexActivity.this, RegisterActivity.class);
                startActivity(joinIntent);
            }
        });


        q.tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IndexActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void viewPagerCall() {

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new com.example.hong.boaaproject.firstActivity.PagerAdapter(getSupportFragmentManager()));

        CircleIndicator circleIndicator = findViewById(R.id.circle_indicator);
        circleIndicator.setupWithViewPager(viewPager);
    }
}
