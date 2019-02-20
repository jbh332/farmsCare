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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;

import org.json.JSONObject;

public class PWChangeDialog extends Dialog {

    EditText etPassword, etPassword2;
    Button btnChange;
    String password, password2, userPW;

    public PWChangeDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_pw_change);

        etPassword = findViewById(R.id.etPassword);
        etPassword2 = findViewById(R.id.etPassword2);
        btnChange = findViewById(R.id.btnChange);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (validate() == false) {
                    return;

                } else {

                    pwChangeComplete();
                }

            }
        });

    }

    private void pwChangeComplete() {

        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(getContext());
        String userID = keepLoginActivity.getUserID();
        userPW = password2;

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        Toast.makeText(getContext(), "비밀번호 변경이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getContext(), UserInfoUpdateActivity.class);
                        getContext().startActivity(intent);
                        dismiss();

                    } else {
                        Toast.makeText(getContext(), "비밀번호 변경을 실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        PWChangeRequest pwChangeRequest = new PWChangeRequest(userID, userPW, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(pwChangeRequest);

    }

    private boolean validate() {

        boolean valid = true;

        password = etPassword.getText().toString();
        password2 = etPassword2.getText().toString();

        if (!password.equals(password2)) {
            Toast.makeText(getContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            valid = false;
        } else {
            etPassword2.setError(null);
        }
        if (password.isEmpty()) {
            etPassword.setError("Password를 입력해 주세요!");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (password2.isEmpty()) {
            etPassword2.setError("Password를 입력해 주세요!");
            valid = false;
        } else {
            etPassword2.setError(null);
        }
        return valid;
    }
}
