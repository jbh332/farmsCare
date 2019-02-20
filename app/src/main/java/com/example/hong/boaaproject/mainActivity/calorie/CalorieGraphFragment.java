package com.example.hong.boaaproject.mainActivity.calorie;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.mainActivity.GraphDBHelper;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.example.hong.boaaproject.mainActivity.MyAxisValueFormatter;
import com.example.hong.boaaproject.mainActivity.MyMarkerView;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalorieGraphFragment extends Fragment {

    MyMarkerView marker;
    Spinner spinner;
    BarChart calorieChart;
    String[] monthData;
    long now;
    Date date;
    SimpleDateFormat sdf;
    String nowMonth;
    int today;
    TextView tvAvg, tvMonth;
    YAxis yLAxis;
    ArrayList<String> xLabel;

    String userID;

    GraphDBHelper graphDBHelper;
    SQLiteDatabase sqlDB;

    String userDate;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //init
        View view = inflater.inflate(R.layout.activity_graph_calorie_fragment, container, false);

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());
        userID = keepLoginActivity.getUserID();
        tvMonth = view.findViewById(R.id.tvMonth);
        now = System.currentTimeMillis();
        date = new Date(now);
        sdf = new SimpleDateFormat("dd", Locale.KOREA);
        userDate = sdf.format(date);
        today = Integer.parseInt(userDate);
        sdf = new SimpleDateFormat("MM", Locale.KOREA);
        nowMonth = sdf.format(date);
        Log.d("오늘날짜", String.valueOf(today));
        monthData = new String[31];
        graphDBHelper = new GraphDBHelper(getActivity());

        tvMonth.setText(nowMonth + "월");

        calorieChart = view.findViewById(R.id.calorieChart);
        spinner = view.findViewById(R.id.spinner);

        //현재달에 setting
        spinner.setSelection(Integer.valueOf(nowMonth)-1);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getMonthData(position + 1);
                drawChart();
                tvMonth.setText(String.valueOf(position + 1) + "월");
                tvAvg.setText("하루 평균 칼로리 섭취량 : " + String.valueOf(checkAvg(monthData)) +"Kcal");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvAvg = view.findViewById(R.id.tvAvg);
        yLAxis = calorieChart.getAxisLeft();

        xLabel = new ArrayList<>();
        for (int k = 0; k <= 30; k++) {
            xLabel.add(String.valueOf(k + 1) + "일");
        }
        TextView tvAvg = view.findViewById(R.id.tvAvg);

        marker = new MyMarkerView(getContext(), R.layout.markerview_root);
        marker.setChartView(calorieChart);
        calorieChart.setMarker(marker);

        Description description = new Description();
        description.setText("");
        calorieChart.setDescription(description);

        LimitLine limitLine = new LimitLine(2200f, "권장 칼로리 섭취량");
        limitLine.setLineColor(Color.BLACK);
        limitLine.setLineWidth(2f);
        limitLine.setTextColor(Color.BLACK);
        yLAxis.addLimitLine(limitLine);

        return view;
    }

    private void drawChart() {

        int[] color = new int[today];

        //색 데이터 입력
        for (int j = 0; j < today; j++) {
            if (Integer.parseInt(monthData[j]) >= 8) {
                color[j] = getResources().getColor(R.color.graphColorMax);
            } else {
                color[j] = getResources().getColor(R.color.graphColorMin);
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        //값을 대입함
        //i는 entries의 index
        for (int i = 0; i < 31; i++) {
            entries.add(new BarEntry(i + 1, Float.parseFloat(monthData[i])));
        }

        //DataSet을 만든다. 그래프의 선 색 등등의 속성을 정할 수 있다.
        BarDataSet barDataSet = new BarDataSet(entries, "");
        barDataSet.setColors(color);
        barDataSet.setDrawValues(false); // 차트 위에 뜨는 숫자의 유무

        BarData barData = new BarData(barDataSet);
        calorieChart.setData(barData);
        calorieChart.getLegend().setEnabled(false);

        //x축과 y축의 효과
        final XAxis xAxis = calorieChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setLabelCount(10, false);
        xAxis.setGranularity(1f);

        yLAxis.setAxisMaximum(3000f);
        yLAxis.setAxisMinimum(0);
        yLAxis.setTextColor(Color.BLACK);

        YAxis yRAxis = calorieChart.getAxisRight();
        yRAxis.setDrawLabels(false);
        yRAxis.setDrawAxisLine(false);
        yRAxis.setDrawGridLines(false);

        xAxis.setValueFormatter(new MyAxisValueFormatter(xLabel));

        //차트에 효과넣기.
        calorieChart.setVisibleXRangeMaximum(10);
        calorieChart.moveViewToX(today - 9);
        calorieChart.setFitBars(true);
        calorieChart.setDoubleTapToZoomEnabled(false);
        calorieChart.setDrawGridBackground(false);
        calorieChart.animateY(1000, Easing.EasingOption.EaseInCubic);
        calorieChart.invalidate();

    }

    //테스트용 평균구하기
    private float checkAvg(String[] day) {
        int sum = 0;
        for (int i = 0; i < today; i++) {
            sum += Integer.parseInt(day[i]);
        }
        return (sum / today);
    }

    private void getMonthData(int month){
        sqlDB = graphDBHelper.getReadableDatabase();
        String monthString = String.valueOf(month);
        try {
            Cursor cursor = sqlDB.rawQuery("SELECT userDataCalorie, userDateDay from graph where userID= '" + userID + "' and userDateMonth = '" + monthString + "';", null);
            cursor.moveToFirst();

            for (int i=0; i<31; i++){
                //DB에 해당 날짜의 값이 없다면 데이터에 0 대입

                if (!(String.format("%02d", cursor.getInt(1)).equals(String.format("%02d", i + 1)))) {
                    monthData[i] = "0";
                } else { //아닐시 해당 데이터 대입
                    monthData[i] = String.valueOf(cursor.getInt(0));
                    if (!cursor.isLast()) {
                        cursor.moveToNext();
                    }
                }
            }

            cursor.close();
        } catch (CursorIndexOutOfBoundsException e){
            Toast.makeText(getContext(), "등록된 데이터가 없습니다.", Toast.LENGTH_LONG).show();
            for (int i=0; i<31; i++) {
                monthData[i] = "0";
            }
        }
    }

}