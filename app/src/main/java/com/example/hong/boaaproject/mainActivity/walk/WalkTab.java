package com.example.hong.boaaproject.mainActivity.walk;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

public class WalkTab extends AppCompatActivity {

    String userID, walkData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk_tab);

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(this);
        userID = keepLoginActivity.getUserID();

        Toolbar backtool = findViewById(R.id.toolBar);
        setSupportActionBar(backtool);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("걸음 입력", InsertWalkFragment.class)
                .add("걸음 그래프", WalkGraphFragment.class)
                .create());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewPagerTab);
        viewPagerTab.setViewPager(viewPager);
    }

    public void walkData(int twt) {
        walkData = String.valueOf(twt);
    }

    @Override
    public void onBackPressed() {
        updateSteps();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                updateSteps();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateSteps() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                         Log.d("JBH WALK : ", walkData);
                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        InsertWalkRequest insertWalkRequest = new InsertWalkRequest(userID, walkData, responseListener);
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(insertWalkRequest);
    }

}
