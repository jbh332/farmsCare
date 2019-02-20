package com.example.hong.boaaproject.mainActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.FragmentMainBinding;
import com.example.hong.boaaproject.mainActivity.calorie.CalorieTab;
import com.example.hong.boaaproject.mainActivity.sleep.SleepTab;
import com.example.hong.boaaproject.mainActivity.walk.WalkTab;
import com.example.hong.boaaproject.mainActivity.water.WaterTab;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainFragment extends android.support.v4.app.Fragment implements SensorEventListener { //SensorEventListener 상속

    FragmentMainBinding a;

    // 만보기
    static int count = 0;
    String str = String.format("%d", count);

    private long lastTime;
    private float speed;
    private float lastX;
    private float lastY;
    private float lastZ;
    private float x, y, z;

    private static final int SHAKE_THRESHOLD = 800;
    private static final int DATA_X = SensorManager.DATA_X;
    private static final int DATA_Y = SensorManager.DATA_Y;
    private static final int DATA_Z = SensorManager.DATA_Z;

    private SensorManager sensorManager;
    private Sensor accelerormeterSensor;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {

        a = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);

        new BackgroundTask().execute();

// 가속도 센서를 이용한 만보기 기능 구현 [출처: http://pulsebeat.tistory.com/44]
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);//센서를 사용하기 위해 시스템서비스를 가져와 SensorManager타입으로 저장
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 엑셀러로미터 센서(가속도 센서)
        a.walkView.setText(str);
        a.walkView.setClickable(false); // 해당 뷰 클릭 불가능 설정
        a.walkView.setFocusable(false); // 해당 뷰 포커스 되지 않도록 설정


        a.LL0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainFragment.this.getActivity(), Register2Activity.class);
                startActivity(intent);

            }
        });

        a.ivUserState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View dlgView = View.inflate(getActivity(), R.layout.dialog_state, null);
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());

                dlg.setNegativeButton("닫기", null);
                dlg.setView(dlgView);
                dlg.show();

            }
        });

        a.ivUserWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userWaterBtnIntent = new Intent(MainFragment.this.getActivity(), WaterTab.class);
                MainFragment.this.startActivity(userWaterBtnIntent);
            }
        });

        a.ivUserSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userSleepBtnIntent = new Intent(MainFragment.this.getActivity(), SleepTab.class);
                MainFragment.this.startActivity(userSleepBtnIntent);
            }
        });

        a.ivUserSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent footsteptsBtnIntent = new Intent(MainFragment.this.getActivity(), WalkTab.class);
                MainFragment.this.startActivity(footsteptsBtnIntent);
            }
        });

        a.ivUserKcal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent userKcalBtnIntent = new Intent(MainFragment.this.getActivity(), CalorieTab.class); // 알아두기. getActivity  https://stackoverflow.com/questions/20241857/android-intent-cannot-resolve-constructor
                MainFragment.this.startActivity(userKcalBtnIntent);


            }
        });

        return a.getRoot();


    }

    // 만보기
    @Override
    public void onStart() {
        super.onStart();
        if (accelerormeterSensor != null)
            sensorManager.registerListener(this, accelerormeterSensor,      //가속도 센서 리스너 등록
                    SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    public void onStop() {          // 화면 꺼짐시 기존코드를 주석처리하여 sensorManger 꺼지지 않도록 유지
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    } //onAccuracyChanged와 onSensorChanged를 오버라이딩 해줘야 함. 정확도와 센서 정보가 변하면 발생하는 함수

    @Override
    public void onSensorChanged(SensorEvent event) { //흔드는 것을 감지. 가속도센서가 변함에 따라 이 함수가 실행
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) { //센서 종류가 가속도 센서일 때, 이벤트를 처리해줘야 함.
            long currentTime = System.currentTimeMillis();
            long gabOfTime = (currentTime - lastTime);
            if (gabOfTime > 100) { //최근 측정시간과 현재시간을 비교하여 0.1초 이상인 경우, 흔듦 감지

                lastTime = currentTime;
                x = event.values[SensorManager.DATA_X];
                y = event.values[SensorManager.DATA_Y];
                z = event.values[SensorManager.DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;
                //가속도의 벡터값을 이용하여 대략적으로 측정(직선 움직임만 측정 가능하여 정확도 조금 떨어지는 단점. 각속도 측정할 수 있는 자이로 센서와 함께쓰면 보완가능)
                if (speed > SHAKE_THRESHOLD) {  //위에서 설정함 초기값: SHAKE_THRESHOLD = 800; 속도가 800 이상인 경우, 흔듦을 감지하도록 설정
                    count++; // 흔듦을 감지하면 걸음 수 올라가고 그 값을 받아서 화면에 출력
                    str = String.format("%d", count);
                    a.walkView.setText(str);
                    int pBarPercent = count % 100;        //Progress Bar 10000보 Max치 설정 -> 100으로 나눠서 100퍼센트까지 적용. 예) Max치 5000 -> count%50으로 수정하면 끝.
                    a.pBar.setProgress(pBarPercent);    //progress 지정

                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];


            }

        }

    }


    class BackgroundTask extends AsyncTask<Void, Void, String> {

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());
        String userID = keepLoginActivity.getUserID();

        String target;

        //변수의 초기화, 네트워크 이전 세팅
        @Override
        protected void onPreExecute() {
            target = "http://jbh9730.cafe24.com/UserInformation.php?userID=" + userID;
        }

        //백그라운드 작업
        @Override
        protected String doInBackground(Void... voids) {


            try {

                URL url = new URL(target);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String temp;
                StringBuilder stringBuilder = new StringBuilder();


                while ((temp = bufferedReader.readLine()) != null) {//버퍼에서 한줄씩 읽으면서 템프에 넣는다

                    stringBuilder.append(temp); //한줄씩 추가
                }

                // 사용종료 close
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return stringBuilder.toString().trim();

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        //백그라운드 작업 끝난 이후 result값으로 리턴
        @Override
        protected void onPostExecute(String result) {

            String userNicName, userHeight, userWeight, userImgURL;

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                JSONObject object = jsonArray.getJSONObject(0);
                userNicName = object.getString("userNicName");
                userHeight = object.getString("userHeight");
                userWeight = object.getString("userWeight");
                userImgURL = object.getString("userImgURL");
                UserInformationModel userInformationModel = new UserInformationModel(userNicName, userHeight, userWeight, userImgURL);

                a.tvNicName.setText(userInformationModel.getUserNicName() + " 님 환영합니다 !");

                if (userHeight.equals("null") || userWeight.equals("null")) {
                    a.tvNull.setText("사용자 정보를 입력해주세요.");
                } else {

                    a.tvHeight.setText(userInformationModel.getUserHeight() + " cm");
                    a.tvWeight.setText(userInformationModel.getUserWeight() + " kg");
                }

                Picasso.with(getActivity())
                        .load(userImgURL)
                        .into(a.ivMyPicture);

                Log.d("JBH : ", userImgURL + " ss");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}





