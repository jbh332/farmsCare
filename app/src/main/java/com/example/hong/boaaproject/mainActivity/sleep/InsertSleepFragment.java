package com.example.hong.boaaproject.mainActivity.sleep;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class InsertSleepFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    Point size;
    long now;
    Date date;
    SimpleDateFormat sdf;
    double[] clockDegree;
    PointF centerPoint, init;
    PointF[] numberPoint;
    PointF clicked1Point, clicked2Point;

    int userDataSleep;

    ImageView ivClock;
    int[] clickedNum;
    int sleepTime;
    TextView testNumResult;
    TextView[] tvTime;
    int radius = 300;
    int[] displaySize;
    float currentAngle = 0;

    String userID, userGoalSleep, userCurrentSleep, userImgURL, sleepGiftCheck;
    TextView tvUpdateGoalSleep, tvCurrentSleep, tvGoalSleep;
    CircleImageView civMyPicture;
    ConstraintLayout LL3;
    LottieAnimationView lottie;
    SwipeRefreshLayout swipeRefreshLayout;

    String userDateDay,userDateMonth, userDateYear;
    //내부DB
    GraphDBHelper graphDBHelper;
    SQLiteDatabase sqlDB;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_insert_sleep, container, false);

        swipeRefreshLayout = view.findViewById(R.id.srl);
        swipeRefreshLayout.setOnRefreshListener(this);

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

        tvUpdateGoalSleep = view.findViewById(R.id.tvUpdateGoalSleep);
        tvCurrentSleep = view.findViewById(R.id.tvCurrentSleep);
        tvGoalSleep = view.findViewById(R.id.tvGoalSleep);
        civMyPicture = view.findViewById(R.id.ivMyPicture);
        LL3 = view.findViewById(R.id.LL3);
        lottie = view.findViewById(R.id.lottie);

        // 목표 수정 다이얼로그 호출
        tvUpdateGoalSleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getSleepGoalChangeDialog();

            }
        });


        //init
        size = new Point();
        init = new PointF(0, 0);
        clicked1Point = new PointF(0, 0);
        clicked2Point = new PointF(0, 0);
        clickedNum = new int[]{-1, -1};
        ivClock = view.findViewById(R.id.ivClockTracking);

        tvTime = new TextView[]{
                view.findViewById(R.id.tv0),
                view.findViewById(R.id.tv1),
                view.findViewById(R.id.tv2),
                view.findViewById(R.id.tv3),
                view.findViewById(R.id.tv4),
                view.findViewById(R.id.tv5),
                view.findViewById(R.id.tv6),
                view.findViewById(R.id.tv7),
                view.findViewById(R.id.tv8),
                view.findViewById(R.id.tv9),
                view.findViewById(R.id.tv10),
                view.findViewById(R.id.tv11)
        };

        clockDegree = new double[12];
        testNumResult = view.findViewById(R.id.testNumResult);


        graphDBHelper = new GraphDBHelper(getActivity());
        tvUpdateGoalSleep = view.findViewById(R.id.tvUpdateGoalSleep);

        settingData();

        //getSize메소드로 화면크기를 받아옴
        displaySize = getSize();

        //화면 크기에 따른 정 중앙의 좌표를 계산
        centerPoint = new PointF(displaySize[0] / 2, displaySize[1] / 2);
        numberPoint = new PointF[12];

        //PointF배열 초기화
        for (int i = 0; i < 12; i++) {
            numberPoint[i] = new PointF();
        }

        //clockDegree의 index가 시계의 숫자가 된다.
        //숫자 0은 중심(0,0)을 기준으로 90도, 1은 60도, 2는 30도, 3은 0도, 4는 330도...에 배치되어있다는 경향성을 파악해 for문을 다음과 같이 설계했다.
        for (int i = 0; i < clockDegree.length; i++) {
            double num = 90.0 - i * 30;
            if (num < 0.0) {
                num = 360.0 + num;
            }
            clockDegree[i] = num;
        }

        //메소드 실행. 각 숫자의 좌표를 계산후 화면에 배치한다.
        setNumberPoint();
        setNumber(tvTime);

        //시계이미지 위치 세팅
        ivClock.setX(centerPoint.x - radius);
        ivClock.setY(centerPoint.y - radius);
        ivClock.getLayoutParams().width = radius * 2;
        ivClock.getLayoutParams().height = radius * 2;
        ivClock.setClickable(false);

        //TextView에 클릭리스너 등록 : 중복 코드를 방지하기 위해 OnClickListener 인터페이스를 사용함
        for (int i = 0; i < tvTime.length; i++) {
            tvTime[i].setOnClickListener(this);
        }

        return view;
    }


    //클릭한 TextView의 숫자를 받아와 화면에 나타내는 메소드
    public void onClick(View view) {

        for (int i = 0; i < tvTime.length; i++) {
            //클릭한 텍스트뷰의 id가 일치하고 clickedNum배열의 초기값(-1)과 일치할때
            if (clickedNum[0] == -1 && view.getId() == tvTime[i].getId()) {
                //클릭한 숫자를 clickedNum배열에 삽입 및 화면에 출력
                clickedNum[0] = Integer.parseInt(tvTime[i].getText().toString());
                testNumResult.setText("취침시간 : " + String.valueOf(clickedNum[0]) + "시");
                clicked1Point = numberPoint[i]; //클릭한 첫번째 숫자의 좌표를 clicked1Point에 저장

            } else if (clickedNum[1] == -1 && view.getId() == tvTime[i].getId()) {
                clickedNum[1] = Integer.parseInt(tvTime[i].getText().toString());
                testNumResult.setText("취침시간 : " + String.valueOf(clickedNum[0]) + "시 기상시간 : " + String.valueOf(clickedNum[1]) + "시");
                double angle = calAngle(clickedNum);
                drawTracking(clickedNum[0], angle, centerPoint);
                if (clickedNum[1] < clickedNum[0]) {
                    sleepTime = clickedNum[1] + 12 - clickedNum[0];
                } else if (clickedNum[0] < clickedNum[1]) {
                    sleepTime = clickedNum[1] - clickedNum[0];
                }

                //DB에 값입력
                sqlDB = graphDBHelper.getWritableDatabase();
                Cursor checkCursor = sqlDB.rawQuery("SELECT userDateMonth, userDateDay, userDataSleep FROM graph WHERE userID='"+ userID+"' and userDateMonth = '"+userDateMonth+"' and userDateDay = '"+userDateDay+"'",null);
                if (checkCursor.getCount() == 0 ) {
                    sqlDB.execSQL("INSERT INTO graph (userID, userDateYear, userDateMonth, userDateDay, userDataSleep) VALUES ('" + userID + "', '" + userDateYear + "', '" + userDateMonth + "','" + userDateDay + "', " + sleepTime + ")");
                    Log.d("DB입력성공", "DB입력성공");
                } else {
                    sqlDB.execSQL("UPDATE graph SET userDataSleep = "+sleepTime+" WHERE userID='"+userID+"' and userDateMonth = '"+userDateMonth+"' and userDateDay = '"+userDateDay+"'");
                    Log.d("DB수정성공", "DB수정성공");
                }
                sqlDB.close();
                settingData();
                //   updateSleep();

            }
        }
    }

    private void settingData(){

        sqlDB = graphDBHelper.getReadableDatabase();
        Cursor cursor = sqlDB.rawQuery("SELECT userDataSleep from graph where userID= '"+userID+"' AND userDateYear='"+userDateYear+"' AND userDateMonth='"+userDateMonth+"' AND userDateDay='"+userDateDay+"';", null);
        cursor.moveToFirst();
        if (!(cursor.getCount() == 0)) {
            userDataSleep = cursor.getInt(0);
            tvCurrentSleep.setText(String.valueOf(userDataSleep) + "시간");
        }
        cursor.close();
    }

    private double calAngle(int[] clock) {
        double angle;
        int firstNum, secNum;
        firstNum = clock[0];
        secNum = clock[1];

        if (secNum < firstNum) {
            secNum = secNum + 12;
        }

        angle = (secNum - firstNum) * 30;

        return angle;
    }

    //숫자의 좌표를 삼각함수를 이용해 계산해주는 메소드
    private void setNumberPoint() {
        for (int i = 0; i < numberPoint.length; i++) {
            //숫자가 위치할 좌표(numberPoint)의 x, y좌표에 해당 숫자가 매칭된 각을 계산한다.
            //clockDegree배열에 담긴 숫자들은 전부 각도법 기반이므로, degree2radian메소드를 활용해 호도법으로 바꾼 후 삼각함수 계산
            //임의의 theta값과 반지름 radius를 알 때 삼각형의 밑변은 radius * cos(theta)이고, 삼각형의 높이는 radous * sin(theta)이다.
            //테스트해보니 x좌표계가 반대인듯 싶어 x좌표는 * -1을 해주었음.
            numberPoint[i].x = centerPoint.x - (float) (radius * Math.cos(degree2radian(clockDegree[i]))) * -1;
            numberPoint[i].y = centerPoint.y - (float) (radius * Math.sin(degree2radian(clockDegree[i])));
        }
    }

    //숫자를 화면에 배치
    private void setNumber(TextView[] tvTime) {
        for (int i = 0; i < tvTime.length; i++) {
            tvTime[i].setX(numberPoint[i].x);
            tvTime[i].setY(numberPoint[i].y);
        }
    }

    //화면의 크기를 받아오는 메소드
    private int[] getSize() {
        int w = getResources().getDisplayMetrics().widthPixels;
        int h = getResources().getDisplayMetrics().heightPixels;

        return new int[]{w, h};
    }

    //호를 그리는 메소드
    private void drawTracking(int clickNum, double angle, PointF center) {

        Bitmap bitmap = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);
        final double maxAngle = angle;
        final int cNum = clickNum;

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(getResources().getColor(R.color.colorAccent));

        final RectF rect = new RectF();
        rect.set(0, 0, 300, 300);

        final double[] arcAngle = new double[]{270, 300, 330, 0, 30, 60, 90, 120, 150, 180, 210, 240};


        ivClock.setImageBitmap(bitmap);
        //호 애니매이션
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (currentAngle < maxAngle) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            canvas.drawArc(rect, (float) arcAngle[cNum], currentAngle, false, paint);
                            ivClock.invalidate();
                            currentAngle++;
                        }
                    });
                    SystemClock.sleep(5);
                }
            }
        }).start();

    }

    //각도법을 호도법으로
    private double degree2radian(double dg) {
        return (dg * Math.PI / 180.0);
    }

    //호도법을 각도법으로
    private double radian2degree(double rd) {
        return (rd * 180.0 / Math.PI);
    }

    private void getSleepGoalChangeDialog() {

        DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        SleepGoalChangeDialog sleepGoalChangeDialog = new SleepGoalChangeDialog(getContext());
        WindowManager.LayoutParams wm = sleepGoalChangeDialog.getWindow().getAttributes();
        wm.copyFrom(sleepGoalChangeDialog.getWindow().getAttributes());
        wm.width = width / 1;
        wm.height = height / 3;

        sleepGoalChangeDialog.show();
    }

    private void updateSleep() {

        userCurrentSleep = String.valueOf(sleepTime);

        final Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        // Log.d("JBH SLEEP : ", sleepTime + "입력 완료");
                    } else {
                        //Log.d("JBH SLEEP : ", "입력 안됨");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };

        InsertSleepRequest insertSleepRequest = new InsertSleepRequest(userID, userCurrentSleep, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(insertSleepRequest);

    }

    @Override
    public void onRefresh() {

        new BackgroundTask().execute();
    }

    class BackgroundTask extends AsyncTask<Void, Void, String> {

        String target;

        @Override
        protected void onPreExecute() {

            target = "http://jbh9730.cafe24.com/GetSleep.php?userID=" + userID;
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
         //       userCurrentSleep = object.getString("userCurrentSleep");
                userGoalSleep = object.getString("userGoalSleep");
                userImgURL = object.getString("userImgURL");
                sleepGiftCheck = object.getString("sleepGiftCheck");
     //           tvCurrentSleep.setText(userCurrentSleep + " 시간");
                tvGoalSleep.setText(userGoalSleep + " 시간");

          //       목표 달성하면
                if (userDataSleep >= Integer.parseInt(userGoalSleep)) {
                    Log.d("진입", String.valueOf(sleepTime));
                    LL3.setVisibility(View.VISIBLE);
                    lottie.playAnimation();
                    lottie.loop(true);

                    lottie.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            if (sleepGiftCheck == "0") {
                                getBoxDialog();
                                lottie.loop(false);
                                sleepGiftCheck = "1";
                                sleepGiftCheckUpdate();

                            } else {
                                Toast.makeText(getActivity(), "이미 오늘의 보상을 받으셨습니다.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }


                Picasso.with(getContext())
                        .load(userImgURL)
                        .into(civMyPicture);


                swipeRefreshLayout.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sleepGiftCheckUpdate() {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                         Log.d("JBH GIFT CHECK : ", "WORKED");
                    } else {
                        Log.d("JBH GIFT CHECK : ", "NOT WORKED");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        SleepGiftRequest sleepGiftRequest = new SleepGiftRequest(userID, sleepGiftCheck, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(sleepGiftRequest);
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

