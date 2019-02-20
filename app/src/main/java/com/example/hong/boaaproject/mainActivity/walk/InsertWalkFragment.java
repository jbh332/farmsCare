package com.example.hong.boaaproject.mainActivity.walk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.FragmentInsertWalkBinding;
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


public class InsertWalkFragment extends Fragment implements SensorEventListener, SwipeRefreshLayout.OnRefreshListener {

    static int count = 0;
    String str = String.format("%d", count);

    long now;
    Date date;
    SimpleDateFormat sdf;
    int userDataWalk;
    String userDateDay,userDateMonth, userDateYear;
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

    FragmentInsertWalkBinding binding;
    String userCurrentSteps, userGoalSteps, userImgURL;

    //내부DB
    GraphDBHelper graphDBHelper;
    SQLiteDatabase sqlDB;

    String userID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_insert_walk, container, false);

        binding.srl.setOnRefreshListener(this);
        graphDBHelper = new GraphDBHelper(getActivity());

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());
        userID = keepLoginActivity.getUserID();

        new BackgroundTask().execute();

        now = System.currentTimeMillis();
        date = new Date(now);
        sdf = new SimpleDateFormat("yyyy", Locale.KOREA);
        userDateYear = sdf.format(date);
        sdf = new SimpleDateFormat("MM", Locale.KOREA);
        userDateMonth = sdf.format(date);
        sdf = new SimpleDateFormat("dd", Locale.KOREA);
        userDateDay = sdf.format(date);

        settingData();

        binding.tvUpdateGoalSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getStepsGoalChangeDialog();

            }
        });

        // 가속도 센서를 이용한 만보기 기능 구현 [출처: http://pulsebeat.tistory.com/44]
        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);//센서를 사용하기 위해 시스템서비스를 가져와 SensorManager타입으로 저장
        accelerormeterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // 엑셀러로미터 센서(가속도 센서)

        binding.tvWalkView.setClickable(false); // 해당 뷰 클릭 불가능 설정
        binding.tvWalkView.setFocusable(false); // 해당 뷰 포커스 되지 않도록 설정

        return binding.getRoot();
    }

    private void getStepsGoalChangeDialog() {

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        StepsGoalChangeDialog stepsGoalChangeDialog = new StepsGoalChangeDialog(getContext());
        WindowManager.LayoutParams wm = stepsGoalChangeDialog.getWindow().getAttributes();
        wm.copyFrom(stepsGoalChangeDialog.getWindow().getAttributes());
        wm.width = width / 1;
        wm.height = height / 3;

        stepsGoalChangeDialog.show();
    }

    @Override
    public void onRefresh() {

        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;
        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());
        String userID = keepLoginActivity.getUserID();

        @Override
        protected void onPreExecute() {

            target = "http://jbh9730.cafe24.com/GetSteps.php?userID=" + userID;
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

                //TODO 홍래야 아래 주석문이 있으면 서버 내용 호출이 안되더라
                //sqlDB.execSQL("INSERT INTO graph (userID, userDataWalk) VALUES ('"+userID+"',"+ 6000 +");");
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("response");

                JSONObject object = jsonArray.getJSONObject(0);
                userCurrentSteps = object.getString("userCurrentSteps");
                userGoalSteps = object.getString("userGoalSteps");
                userImgURL = object.getString("userImgURL");

                binding.tvCurrentSteps.setText(userCurrentSteps + " 보");
                binding.tvGoalSteps.setText(userGoalSteps + " 보");


                Picasso.with(getContext())
                        .load(userImgURL)
                        .into(binding.ivMyPicture);

                binding.srl.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void settingData(){

        sqlDB = graphDBHelper.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT userDataWalk from graph where userID= '"+userID+"' AND userDateYear='"+userDateYear+"' AND userDateMonth='"+userDateMonth+"' AND userDateDay='"+userDateDay+"';", null);
        cursor.moveToFirst();
        if (!(cursor.getCount() == 0)) {
            userDataWalk = cursor.getInt(0);
            binding.tvCurrentSteps.setText(String.valueOf(userDataWalk) + "보");
            binding.tvWalkView.setText(String.valueOf(userDataWalk));
        }
        cursor.close();
    }

    // 만보기 함수.
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
                z = event.values[DATA_Z];

                speed = Math.abs(x + y + z - lastX - lastY - lastZ) / gabOfTime * 10000;
                //가속도의 벡터값을 이용하여 대략적으로 측정(직선 움직임만 측정 가능하여 정확도 조금 떨어지는 단점. 각속도 측정할 수 있는 자이로 센서와 함께쓰면 보완가능)
                if (speed > SHAKE_THRESHOLD) {  //위에서 설정함 초기값: SHAKE_THRESHOLD = 800; 속도가 800 이상인 경우, 흔듦을 감지하도록 설정
                    count++; // 흔듦을 감지하면 걸음 수 올라가고 그 값을 받아서 화면에 출력
                    str = String.format("%d", count);

                    ((WalkTab) getActivity()).walkData(count); // walkTab에 걸음횟수 전달

                    sqlDB = graphDBHelper.getWritableDatabase();
                    Cursor checkCursor = sqlDB.rawQuery("SELECT userDateMonth, userDateDay, userDataWalk FROM graph WHERE userID='"+ userID+"' and userDateMonth = '"+userDateMonth+"' and userDateDay = '"+userDateDay+"'",null);
                    if (checkCursor.getCount() == 0 ) {
                        sqlDB.execSQL("INSERT INTO graph (userID, userDateYear, userDateMonth, userDateDay, userDataWalk) VALUES ('" + userID + "', '" + userDateYear + "', '" + userDateMonth + "','" + userDateDay + "', " + str + ")");
                        Log.d("DB입력성공", "DB입력성공");
                    } else {
                        sqlDB.execSQL("UPDATE graph SET userDataWalk = "+str+" WHERE userID='"+userID+"' and userDateMonth = '"+userDateMonth+"' and userDateDay = '"+userDateDay+"'");
                        Log.d("DB수정성공", "DB수정성공");
                    }
                    sqlDB.close();
                    settingData();
                    }
                }
                lastX = event.values[DATA_X];
                lastY = event.values[DATA_Y];
                lastZ = event.values[DATA_Z];

            }
        }
    }

