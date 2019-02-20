package com.example.hong.boaaproject.mainActivity.calorie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.mainActivity.GraphDBHelper;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalorieRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    Context context;
    List<CalorieModel> calorieModels;
    String userID, userCurrentCalorie2;
    int totalCalories = 0;
    long now;
    Date date;
    String userDateDay,userDateMonth, userDateYear;
    SimpleDateFormat sdf;
    GraphDBHelper graphDBHelper;
    SQLiteDatabase sqlDB;



    public CalorieRecyclerAdapter(Context context, List<CalorieModel> calorieModels) {
        this.context = context;
        this.calorieModels = calorieModels;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calorie, parent, false);


        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


        ((CustomViewHolder) holder).tvFoodCalorie.setText(calorieModels.get(position).foodCalorie);
        ((CustomViewHolder) holder).tvFoodName.setText(calorieModels.get(position).foodName);


        ((CustomViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context, calorieModels.get(position).foodName + ", " + calorieModels.get(position).foodCalorie + "Kcal가 입력됐습니다.", Toast.LENGTH_LONG).show();

                totalCalories = totalCalories + Integer.parseInt(calorieModels.get(position).userCurrentCalorie) + Integer.parseInt(calorieModels.get(position).foodCalorie);


                updateCalorie(totalCalories);

            }
        });

    }


    @Override
    public int getItemCount() {
        return calorieModels.size();
    }

    private class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tvFoodCalorie, tvFoodName;
        CardView cardView;

        public CustomViewHolder(View view) {
            super(view);

            tvFoodCalorie = view.findViewById(R.id.tvFoodCalorie);
            tvFoodName = view.findViewById(R.id.tvFoodName);
            cardView = view.findViewById(R.id.cardView);
        }
    }

    private void updateCalorie(int totalCalories) {

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(context);
        userID = keepLoginActivity.getUserID();
        userCurrentCalorie2 = String.valueOf(totalCalories); // DB 에 넣을 유저의 최신 칼로리
        now = System.currentTimeMillis();
        date = new Date(now);
        sdf = new SimpleDateFormat("yyyy", Locale.KOREA);
        userDateYear = sdf.format(date);
        sdf = new SimpleDateFormat("MM", Locale.KOREA);
        userDateMonth = sdf.format(date);
        sdf = new SimpleDateFormat("dd", Locale.KOREA);
        userDateDay = sdf.format(date);

        graphDBHelper = new GraphDBHelper(context);
        sqlDB = graphDBHelper.getWritableDatabase();
        Cursor checkCursor = sqlDB.rawQuery("SELECT userDateMonth, userDateDay, userDataCalorie FROM graph WHERE userID='"+ userID+"' and userDateMonth = '"+userDateMonth+"' and userDateDay = '"+userDateDay+"'",null);
        if (checkCursor.getCount() == 0 ) {
            sqlDB.execSQL("INSERT INTO graph (userID, userDateYear, userDateMonth, userDateDay, userDataSleep) VALUES ('" + userID + "', '" + userDateYear + "', '" + userDateMonth + "','" + userDateDay + "', " + totalCalories + ")");
            Log.d("DB입력성공", "DB입력성공");
        } else {
            sqlDB.execSQL("UPDATE graph SET userDataCalorie = "+totalCalories+" WHERE userID='"+userID+"' and userDateMonth = '"+userDateMonth+"' and userDateDay = '"+userDateDay+"'");
            Log.d("DB수정성공", "DB수정성공");
        }
        sqlDB.close();


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        //Log.d("JBH CALORIE : ", userCurrentCalorie2 + " 추가요");
                    } else {
                        //Log.d("JBH WATER : ", "등록안됨");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        InsertCalorieRequest insertCalorieRequest = new InsertCalorieRequest(userID, userCurrentCalorie2, responseListener);
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(insertCalorieRequest);

    }
}
