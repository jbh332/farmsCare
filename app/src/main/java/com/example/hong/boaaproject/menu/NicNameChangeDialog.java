package com.example.hong.boaaproject.menu;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;

import org.json.JSONObject;

public class NicNameChangeDialog extends Dialog {

    EditText etNicName;
    Button btnValidate, btnChange;
    TextView tvValidate;
    private boolean NicNameValidate = false;
    String userNicName, userID;

    public NicNameChangeDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_nicname_change);

        etNicName = findViewById(R.id.etNicName);
        btnValidate = findViewById(R.id.btnValidate);
        btnChange = findViewById(R.id.btnChange);
        tvValidate = findViewById(R.id.tvValidate);

        btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate() == false) {

                    Toast.makeText(getContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    userNicNameValidate();
                }
            }
        });

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate() == false) {
                    return;
                }

                if (!NicNameValidate) {
                    Toast.makeText(getContext(), "중복체크를 해주세요.", Toast.LENGTH_SHORT).show();
                    return;

                } else {

                    nicNameChangeComplete();
                }
            }
        });


    }

    private boolean validate() {

        boolean valid = true;
        String nicName = etNicName.getText().toString();

        if (nicName.isEmpty()) {
            etNicName.setError("닉네임을 입력해 주세요!");
            valid = false;
        } else {
            etNicName.setError(null);
        }
        return valid;
    }

    private void nicNameChangeComplete() {

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());

        userID = keepLoginActivity.getUserID();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        Toast.makeText(getContext(), "닉네임 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), UserInfoUpdateActivity.class);
                        getContext().startActivity(intent);
                        dismiss();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        };
        NicNameChangeRequest nicNameChangeRequest = new NicNameChangeRequest(userID, userNicName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(nicNameChangeRequest);


    }

    private void userNicNameValidate() {

        userNicName = etNicName.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        tvValidate.setText("사용 가능한 닉네임입니다.");
                        NicNameValidate = true;
                        tvValidate.setTextColor(getContext().getResources().getColor(R.color.mainColor));
                    } else {

                        tvValidate.setText("이미 존재하는 닉네임입니다.");
                        tvValidate.setTextColor(getContext().getResources().getColor(R.color.warningColor));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        NicNameValidateRequest nicNameValidateRequest = new NicNameValidateRequest(userNicName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(nicNameValidateRequest);
    }
}
