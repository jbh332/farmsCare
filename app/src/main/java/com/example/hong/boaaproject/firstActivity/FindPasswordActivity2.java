package com.example.hong.boaaproject.firstActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;

import org.json.JSONObject;


public class FindPasswordActivity2 extends AppCompatActivity {

    private TextView tvUserID;
    private ArrayAdapter<String> adapter;
    private Spinner spnPwQuestion;
    Button btnFindPW, btnNext;
    EditText etPWAnswer;
    String userID;
    TextView tvUserPW, tvPW;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password2);

        tvUserID = findViewById(R.id.tvUserID);
        tvPW = findViewById(R.id.tvPW);
        spnPwQuestion = findViewById(R.id.spnPwQuestion);
        btnFindPW = findViewById(R.id.btnFindPW);
        etPWAnswer = findViewById(R.id.etPwAnswer);
        tvUserPW = findViewById(R.id.tvUserPW);
        btnNext = findViewById(R.id.btnNext);

        Intent intent = getIntent();
        userID = intent.getExtras().getString("userID");
        tvUserID.setText(userID);

        final String[] pwQuestion = getResources().getStringArray(R.array.pwQuestion);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, pwQuestion);
        spnPwQuestion.setAdapter(adapter);

        btnFindPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userFindPW();
            }
        });


    }

    private void userFindPW() {

        String pwQuestion = spnPwQuestion.getSelectedItem().toString();
        String pwAnswer = etPWAnswer.getText().toString();

        btnFindPW.setEnabled(true);
        btnNext.setEnabled(false);


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");


                    if (success) {

                        String userPW = jsonResponse.getString("userPW");
                        tvUserPW.setText(userPW);
                        tvPW.setEnabled(true);
                        btnFindPW.setEnabled(false);
                        btnNext.setEnabled(true);

                        btnNext.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(FindPasswordActivity2.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    } else {

                        Toast.makeText(FindPasswordActivity2.this, "정확히 입력해주세요.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }
            }
        };

        FindPWRequest2 findPWRequest2 = new FindPWRequest2(userID, pwQuestion, pwAnswer, responseListener);
        RequestQueue queue = Volley.newRequestQueue(FindPasswordActivity2.this);
        queue.add(findPWRequest2);
    }

}

