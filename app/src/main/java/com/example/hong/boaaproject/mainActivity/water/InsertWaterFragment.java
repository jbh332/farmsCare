package com.example.hong.boaaproject.mainActivity.water;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.FragmentInsertWaterBinding;
import com.example.hong.boaaproject.mainActivity.GetBoxDialog;
import com.example.hong.boaaproject.mainActivity.GraphDBHelper;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InsertWaterFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FragmentInsertWaterBinding binding;
    String userID, userGoalWater, userImgURL;

    int totalwt, waterprogress;
    double everage;


    long now;
    Date date;
    SimpleDateFormat sdf;
    String userDateDay,userDateMonth, userDateYear;

    //내부DB
    GraphDBHelper graphDBHelper;
    SQLiteDatabase sqlDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_insert_water, container, false);

        //리프레쉬
        binding.srl.setOnRefreshListener(this);

        // 유저 아이디 값 호출
        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());
        userID = keepLoginActivity.getUserID();

        graphDBHelper = new GraphDBHelper(getContext());
        sqlDB = graphDBHelper.getWritableDatabase();
        now = System.currentTimeMillis();
        date = new Date(now);
        sdf = new SimpleDateFormat("yyyy", Locale.KOREA);
        userDateYear = sdf.format(date);
        sdf = new SimpleDateFormat("MM", Locale.KOREA);
        userDateMonth = sdf.format(date);
        sdf = new SimpleDateFormat("dd", Locale.KOREA);
        userDateDay = sdf.format(date);

        // 데이터베이스 유저 값 호출
        new BackgroundTask().execute();

        //목표량 출력
        binding.tvGoalWater.setText(userGoalWater + " mL ");
        settingData();


        binding.btnplus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                                // 더하기(+) 버튼 누를 경우
                new Thread() {                                                                      //수면 변화 효과
                    @Override
                    public void run() {
                        for (int g = 0; g < 5; g++) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalwt = totalwt + 20;
                                    waterprogress = waterprogress + 1;
                                    binding.waveView.setProgress(waterprogress);                                       // wave(수면높이)증가

                                    if (totalwt >= 2000) {
                                        //  binding.rdbx.setImageResource(R.drawable.inventory_open);
                                        //  binding.sbb.setImageResource(R.drawable.sbf);                       // 권장물섭취량(2000ml)가 넘을경우 이미지 변경
                                    } else if (totalwt < 0) {
                                        //   totalwt = 0;
                                        //   waterprogress = 0;
                                    } else {
                                        //  binding.sbb.setImageResource(R.drawable.sb1);                       // 권장 물 섭취량이 아닐경우 원래이미지로 돌아옴
                                        //  binding.rdbx.setImageResource(0);
                                    }


                                    //데이터 프래그먼트에 옮기기
                                    ((WaterTab) getActivity()).waterdata(totalwt);
                                    binding.tvCurrentWater.setText(String.valueOf(totalwt) + " mL ");
                                    inputData(totalwt);
                                }
                            });
                            SystemClock.sleep(40);
                        }
                    }
                }.start();
            }
        });

        binding.btnmin1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        for (int g = 0; g < 5; g++) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalwt = totalwt - 20;
                                    waterprogress = waterprogress - 1;
                                    binding.waveView.setProgress(waterprogress);

                                    if (totalwt >= 2000) {
                                        //    binding.sbb.setImageResource(R.drawable.sbf);
                                        //    binding.rdbx.setImageResource(R.drawable.inventory_open);
                                    } else if (totalwt < 0) {                                            //물 섭취량이 0이하로 떨어질 경우에 0 유지
                                        totalwt = 0;
                                        waterprogress = 0;
                                    } else {
                                        //  binding.sbb.setImageResource(R.drawable.sb1);
                                        //  binding.rdbx.setImageResource(0);
                                    }
                                    binding.tvCurrentWater.setText(String.valueOf(totalwt) + " mL ");
                                    ((WaterTab) getActivity()).waterdata(totalwt);

                                    inputData(totalwt);

                                }
                            });
                            SystemClock.sleep(80);
                        }
                    }
                }.start();
            }
        });

        binding.btnplus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        for (int g = 0; g < 25; g++) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalwt = totalwt + 20;
                                    waterprogress = waterprogress + 1;
                                    binding.waveView.setProgress(waterprogress);

                                    if (totalwt >= 2000) {
                                        // binding.sbb.setImageResource(R.drawable.sbf);
                                        // binding.rdbx.setImageResource(R.drawable.inventory_open);
                                    } else if (totalwt < 0) {
                                        //   totalwt = 0;
                                        //   waterprogress = 0;
                                    } else {
                                        //   binding.sbb.setImageResource(R.drawable.sb1);
                                        // binding.rdbx.setImageResource(0);
                                    }
                                    binding.tvCurrentWater.setText(String.valueOf(totalwt) + " mL ");
                                    ((WaterTab) getActivity()).waterdata(totalwt);

                                    inputData(totalwt);
                                }
                            });
                            SystemClock.sleep(20);
                        }
                    }
                }.start();
            }
        });

        binding.btnmin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        for (int g = 0; g < 25; g++) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    totalwt = totalwt - 20;
                                    waterprogress = waterprogress - 1;
                                    binding.waveView.setProgress(waterprogress);

                                    if (totalwt >= 2000) {
                                        //   binding.sbb.setImageResource(R.drawable.sbf);
                                        //  binding.rdbx.setImageResource(R.drawable.inventory_open);
                                    } else if (totalwt < 0) {
                                        totalwt = 0;
                                        waterprogress = 0;
                                    } else {
                                        // binding.sbb.setImageResource(R.drawable.sb1);
                                        // binding.rdbx.setImageResource(0);
                                    }
                                    binding.tvCurrentWater.setText(String.valueOf(totalwt) + " mL ");
                                    ((WaterTab) getActivity()).waterdata(totalwt);

                                    inputData(totalwt);
                                }
                            });
                            SystemClock.sleep(20);
                        }
                    }
                }.start();

            }


        });


        // 변경 클릭 시 목표 변경 다이얼로그 호출
        binding.tvUpdateGoalWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getWaterGoalChangeDialog();
            }
        });


        return binding.getRoot();
    }

    // 다이얼 로그 생성 클래스
    private void getWaterGoalChangeDialog() {

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        WaterGoalChangeDialog waterGoalChangeDialog = new WaterGoalChangeDialog(getContext());
        WindowManager.LayoutParams wm = waterGoalChangeDialog.getWindow().getAttributes();
        wm.copyFrom(waterGoalChangeDialog.getWindow().getAttributes());
        wm.width = width / 1;
        wm.height = height / 3;

        waterGoalChangeDialog.show();
    }

    @Override
    public void onRefresh() {

        new BackgroundTask().execute();
        completeCheck(userGoalWater, totalwt);
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {

            target = "http://jbh9730.cafe24.com/GetWater.php?userID=" + userID;
        }

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();

                while ((temp = bufferedReader.readLine()) != null) {

                    stringBuilder.append(temp);
                }

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                JSONObject object = jsonArray.getJSONObject(0);
                userGoalWater = object.getString("userGoalWater");
                userImgURL = object.getString("userImgURL");

                binding.tvGoalWater.setText(userGoalWater + " mL ");
                binding.tvCurrentWater.setText(String.valueOf(totalwt) + " mL ");

                everage = (totalwt / (double) 2000) * 100; // (현재섭취량 / (예시)목표섭취량) * 100
                waterprogress = (int) everage ;

                // 초기 수면 세팅
                binding.waveView.setProgress(waterprogress);


                completeCheck(userGoalWater, totalwt);

                Picasso.with(getContext())
                        .load(userImgURL)
                        .into(binding.ivMyPicture);

                binding.srl.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // 목표 달성 체크
    private void completeCheck(String userGoalWater, int totalwt) {

        Log.d("메소드체크", String.valueOf(totalwt));
        if (totalwt >= Integer.parseInt(userGoalWater)) {
            Log.d("달성", String.valueOf(totalwt));
            binding.LL3.setVisibility(View.VISIBLE);
            binding.lottie.playAnimation();
            binding.lottie.loop(true);

            binding.lottie.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getBoxDialog();
                    binding.lottie.loop(false);
                }
            });
        }
    }

    // 아이템 획득 다이얼로그 호출
    private void getBoxDialog() {

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        GetBoxDialog getBoxDialog = new GetBoxDialog(getContext());
        getBoxDialog.setCancelable(false);
        WindowManager.LayoutParams wm = getBoxDialog.getWindow().getAttributes();
        wm.copyFrom(getBoxDialog.getWindow().getAttributes());
        wm.width = width / 1;
        wm.height = height / 3;

        getBoxDialog.show();
    }

    private void settingData(){

        sqlDB = graphDBHelper.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT userDataWater from graph where userID= '"+userID+"' AND userDateYear='"+userDateYear+"' AND userDateMonth='"+userDateMonth+"' AND userDateDay='"+userDateDay+"';", null);
        cursor.moveToFirst();
        if (!(cursor.getCount() == 0)) {
            totalwt = cursor.getInt(0);
            binding.tvCurrentWater.setText(String.valueOf(totalwt) + "mL");
        }
        cursor.close();
    }

    private void inputData(int tot){

        sqlDB = graphDBHelper.getWritableDatabase();
        Cursor checkCursor = sqlDB.rawQuery("SELECT userDateMonth, userDateDay, userDataWater FROM graph WHERE userID='"+ userID+"' and userDateMonth = '"+userDateMonth+"' and userDateDay = '"+userDateDay+"'",null);
        if (checkCursor.getCount() == 0 ) {
            sqlDB.execSQL("INSERT INTO graph (userID, userDateYear, userDateMonth, userDateDay, userDataWater) VALUES ('" + userID + "', '" + userDateYear + "', '" + userDateMonth + "','" + userDateDay + "', " + tot + ")");
            Log.d("DB입력성공", "DB입력성공");
        } else {
            sqlDB.execSQL("UPDATE graph SET userDataWater = "+tot+" WHERE userID='"+userID+"' and userDateMonth = '"+userDateMonth+"' and userDateDay = '"+userDateDay+"'");
            Log.d("DB수정성공", "DB수정성공");
        }
        sqlDB.close();

    }



}