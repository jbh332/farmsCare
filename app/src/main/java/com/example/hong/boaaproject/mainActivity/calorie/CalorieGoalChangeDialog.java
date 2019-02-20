package com.example.hong.boaaproject.mainActivity.calorie;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;

import org.json.JSONObject;

public class CalorieGoalChangeDialog extends Dialog {

    EditText etGoal;
    Button btnChange;

    public CalorieGoalChangeDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_calorie_goal_change);

        btnChange = findViewById(R.id.btnChange);
        etGoal = findViewById(R.id.etGoal);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CalorieGoalChangeComplete();
            }
        });

    }

    private void CalorieGoalChangeComplete() {

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());
        String userID = keepLoginActivity.getUserID();
        String userGoalCalorie = etGoal.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {
                        Toast.makeText(getContext(), "칼로리 목표량이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        CalorieGoalChangeRequest calorieGoalChangeRequest = new CalorieGoalChangeRequest(userID, userGoalCalorie, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(calorieGoalChangeRequest);
    }
}
