package com.example.hong.boaaproject.mainActivity.water;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import org.json.JSONObject;

public class WaterTab extends AppCompatActivity {

    String waterdata, userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_tab);

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(this);
        userID = keepLoginActivity.getUserID();

        Toolbar backtool = findViewById(R.id.toolBar);
        setSupportActionBar(backtool);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("수분 입력", InsertWaterFragment.class)
                .add("수분 그래프", WaterGraphFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);


    }

    //데이터베이스에 넣을 값 적용
    public void waterdata(int twt) {
        waterdata = String.valueOf(twt);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateWater();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {

                updateWater();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateWater() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        // Log.d("JBH WATER : ", totalwt + "ML");
                    } else {
                        //Log.d("JBH WATER : ", "등록안됨");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        InsertWaterRequest insertWaterRequest = new InsertWaterRequest(userID, waterdata, responseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(insertWaterRequest);
    }
}
