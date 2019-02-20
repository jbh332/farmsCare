package com.example.hong.boaaproject.firstActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;

import org.json.JSONObject;

public class FindPasswordActivity extends AppCompatActivity {

    Button btnCommit;
    EditText etID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        btnCommit = findViewById(R.id.btnCommit);
        etID = findViewById(R.id.etID);


        Toolbar toolbar = findViewById(R.id.tBar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);


        btnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = etID.getText().toString();
                userIDCheck(userID);
            }
        });
    }

    private void userIDCheck(final String userID) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        Intent intent = new Intent(FindPasswordActivity.this, FindPasswordActivity2.class);
                        intent.putExtra("userID", userID);
                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(FindPasswordActivity.this, "사용할 수 없는 아이디입니다.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        };

        FindPWRequest findPWRequest = new FindPWRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(FindPasswordActivity.this);
        queue.add(findPWRequest);
    }

    long backPressedTime = 0;
    long FINISH_INTERVAL_TIME = 2000;

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한 번 더 누르시면 이전 화면으로 이동합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
