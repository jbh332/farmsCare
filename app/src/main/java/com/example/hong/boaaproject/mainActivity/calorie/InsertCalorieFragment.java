package com.example.hong.boaaproject.mainActivity.calorie;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.FragmentInsertCalorieBinding;
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

public class InsertCalorieFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    FragmentInsertCalorieBinding binding;

    String userCurrentCalorie, userGoalCalorie, userImgURL, calorieGiftCheck, userID;
    GraphDBHelper graphDBHelper;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_insert_calorie, container, false);

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());
        userID = keepLoginActivity.getUserID();

        binding.srl.setOnRefreshListener(this);

        // 현재 유저 정보 호출
        new BackgroundTask().execute();

        graphDBHelper = new GraphDBHelper(getActivity());



        // 아래 변경 버튼 클릭 시 목표 변경 다이얼로그 호출
        binding.tvUpdateGoalCalorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getCalorieGoalChangeDialog();

            }
        });

        // 음식 검색
        binding.LL2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchCalorieActivity.class);
                intent.putExtra("userCurrentCalorie", userCurrentCalorie);
                startActivity(intent);
            }
        });


        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        new BackgroundTask().execute();
    }

    @Override
    public void onRefresh() {

        new BackgroundTask().execute();
    }

    // 다이얼 로그 호출
    private void getCalorieGoalChangeDialog() {

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        CalorieGoalChangeDialog calorieGoalChangeDialog = new CalorieGoalChangeDialog(getContext());
        WindowManager.LayoutParams wm = calorieGoalChangeDialog.getWindow().getAttributes();
        wm.copyFrom(calorieGoalChangeDialog.getWindow().getAttributes());
        wm.width = width / 1;
        wm.height = height / 3;

        calorieGoalChangeDialog.show();
    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {

            target = "http://jbh9730.cafe24.com/GetCalorie.php?userID=" + userID;
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
                userCurrentCalorie = object.getString("userCurrentCalorie");
                userGoalCalorie = object.getString("userGoalCalorie");
                userImgURL = object.getString("userImgURL");
                calorieGiftCheck = object.getString("calorieGiftCheck");

                binding.tvCurrentCalorie.setText(userCurrentCalorie + "Kcal");
                binding.tvGoalCalorie.setText(userGoalCalorie + "Kcal");



                // 목표 달성하면
                if (Integer.parseInt(userCurrentCalorie) >= Integer.parseInt(userGoalCalorie)) {

                    binding.LL3.setVisibility(View.VISIBLE);
                    binding.lottie.playAnimation();
                    binding.lottie.loop(true);

                    binding.lottie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (calorieGiftCheck.equals("0")) {
                                getBoxDialog();
                                binding.lottie.loop(false);
                                calorieGiftCheck = "1";
                                calorieGiftCheckUpdate();
                            } else {
                                Toast.makeText(getActivity(), "이미 오늘의 보상을 받으셨습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                Picasso.with(getContext())
                        .load(userImgURL)
                        .into(binding.ivMyPicture);

                binding.srl.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void calorieGiftCheckUpdate() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        //Log.d("JBH GIFT CHECK : ", "WORKED");
                    } else {
                        //Log.d("JBH GIFT CHECK : ", "NOT WORKED");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        CalorieGiftRequest calorieGiftRequest = new CalorieGiftRequest(userID, calorieGiftCheck, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(calorieGiftRequest);
    }

    // 보상 다이얼로그 호출
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

}
