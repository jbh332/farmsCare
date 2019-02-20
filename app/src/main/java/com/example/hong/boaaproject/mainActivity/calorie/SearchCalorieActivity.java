package com.example.hong.boaaproject.mainActivity.calorie;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivitySearchCalorieBinding;
import com.example.hong.boaaproject.mainActivity.GraphDBHelper;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class SearchCalorieActivity extends AppCompatActivity {

    ActivitySearchCalorieBinding binding;
    RecyclerView.LayoutManager manager;
    List<CalorieModel> calorieModels;
    CalorieRecyclerAdapter calorieRecyclerAdapter;
    InputMethodManager inputMethodManager;

    String foodName, foodCalorie, userCurrentCalorie;
    String userID;

    GraphDBHelper graphDBHelper;
    SQLiteDatabase sqlDB;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_calorie);

        binding.recycleView.setHasFixedSize(true);
        manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.recycleView.setLayoutManager(manager);
        calorieModels = new ArrayList<>();

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getApplicationContext());
        userID = keepLoginActivity.getUserID();

        graphDBHelper = new GraphDBHelper(getApplicationContext());
        sqlDB = graphDBHelper.getReadableDatabase();

        // 키보드 숨기기 위한 호출
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // 이전 화면에서 현재 칼로리 섭취량 호출
        Intent intent = getIntent();
        userCurrentCalorie = intent.getStringExtra("userCurrentCalorie");

        // 음식 검색버튼 클릭 시
        binding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 로딩시작
                binding.rotateloading.start();
                Toast.makeText(getApplicationContext(), "서버 상황에 따라 검색 시간이 다소 소요될 수 있습니다.", Toast.LENGTH_LONG).show();

                // 키보드 닫기
                hideKeyBoard();

                // 리사이클러뷰 검색 목록 갱신
                calorieModels.clear();

                // API 호출 파트
                String serviceUrl =
                        "http://openapi.foodsafetykorea.go.kr/api/4b05e015a9004e25960c/I0750/xml/1";

                String str = binding.etSearch.getText().toString();
                String encodestr = null;

                try {
                    encodestr = new String(str.getBytes());
                    URLEncoder.encode(encodestr, "euc-kr");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String NUM = "3";
                String strUrl = serviceUrl + "/" + NUM + "/" + "DESC_KOR=" + encodestr;
                Log.d("###", strUrl);

                DownloadWebpageTask task = new DownloadWebpageTask();
                task.execute(strUrl); // strUrl 값으로 XML 다운받아옴
            }
        });

    }
    // XML 다운
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                return (String) downloadUrl((String) strings[0]);

            } catch (IOException e) {
                return "다운로드 실패";
            }
        }

        // parser로 XML 데이터 검색
        protected void onPostExecute(String result) {

            try {

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();

                xpp.setInput(new StringReader(result));
                int eventType = xpp.getEventType();
                boolean bSet = false;
                boolean aSet = false;

                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_DOCUMENT) {

                    } else if (eventType == XmlPullParser.START_TAG) {

                        String tag_name = xpp.getName();

                        if (tag_name.equals("DESC_KOR")) {

                            bSet = true;
                        }
                        if (tag_name.equals("NUTR_CONT1")) {

                            aSet = true;
                        }

                    } else if (eventType == XmlPullParser.TEXT) {

                        if (bSet) {

                            // 음식 이름 정보 획득
                            foodName = xpp.getText();
                            bSet = false;

                            // 어댑터에 출력할 정보 송신
                            CalorieModel calorieModel = new CalorieModel(foodCalorie, foodName, userCurrentCalorie);


                            calorieModels.add(calorieModel);

                        } else if (aSet) {

                            // 음식 칼로리 정보 획득
                            foodCalorie = xpp.getText();
//                            sqlDB = graphDBHelper.getWritableDatabase();
//                            sqlDB.execSQL("UPDATE graph SET userDataCalorie = "+foodCalorie+"  WHERE userID = '"+userID+"' ;");
//                            Log.d("칼로리DB수정성공", "DB수정성공");
//                            sqlDB.close();

                            aSet = false;
                        }

                    } else if (eventType == XmlPullParser.END_TAG) {
                        binding.rotateloading.stop();
                    }

                    eventType = xpp.next();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            calorieRecyclerAdapter = new CalorieRecyclerAdapter(getApplicationContext(), calorieModels);
            binding.recycleView.setAdapter(calorieRecyclerAdapter);
        }

        private String downloadUrl(String myurl) throws IOException {

            HttpURLConnection conn = null;
            try {

                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                BufferedInputStream buf = new BufferedInputStream(conn.getInputStream());
                BufferedReader bufreader = new BufferedReader(new InputStreamReader(buf, "utf-8"));
                String line;
                String page = "";
                while ((line = bufreader.readLine()) != null) {
                    page += line;
                }
                return page;
            } finally {
                conn.disconnect();
            }
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void hideKeyBoard() {
        inputMethodManager.hideSoftInputFromWindow(binding.etSearch.getWindowToken(), 0);
    }
}


