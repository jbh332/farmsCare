package com.example.hong.boaaproject.firstActivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivityLoginBinding;
import com.example.hong.boaaproject.mainActivity.KeepLoginActivity;
import com.example.hong.boaaproject.mainActivity.MainActivity;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);


        // 로그인 검사
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                widgetEnabledFalse();

                if (validate() == false) { // 로컬 상에서 로그인 검사

                    widgetEnabledTrue();
                    return;

                } else { // 서버에서 검사

                    binding.rotateloading.start();
                    loginEvent();

                }
            }
        });

        // 회원가입 클릭 시
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //비밀번호 찾기 클릭 시
        binding.btnFindPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, FindPasswordActivity.class);
                startActivity(intent);
            }
        });

        // 아이디 입력 검사
        binding.etID.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                checkInputID();
                return false;
            }
        });

        // 아이디 수정 검사
        binding.etID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkInputID();
            }
        });

        binding.etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                checkInputPassword();
                return false;
            }
        });

        binding.etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkInputPassword();
            }
        });
    }

    private void widgetEnabledTrue() { // 위젯 사용 가능 설정 함수
        binding.btnLogin.setEnabled(true);
        binding.etPassword.setEnabled(true);
        binding.etID.setEnabled(true);
    }

    private void widgetEnabledFalse() { // 위젯 사용 불가능 설정 함수
        binding.btnLogin.setEnabled(false);
        binding.etID.setEnabled(false);
        binding.etPassword.setEnabled(false);
    }

    private void loginEvent() { // 서버에서 아이디 확인하는 함수

        final String userID = binding.etID.getText().toString();
        String userPW = binding.etPassword.getText().toString();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success"); // 성공 신호
                    if (success) {

                        KeepLoginActivity keepLoginActivity = new KeepLoginActivity(LoginActivity.this);
                        keepLoginActivity.setUserID(userID);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {

                        Toast.makeText(LoginActivity.this, "계정을 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                        widgetEnabledTrue();
                        binding.rotateloading.stop();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        LoginRequest loginRequest = new LoginRequest(userID, userPW, responseListener);
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        queue.add(loginRequest);
    }

    private boolean validate() { // 로컬 상에서 입력 유효성 검사
        boolean valid = true;
        String id, password;
        id = binding.etID.getText().toString();
        password = binding.etPassword.getText().toString();

        if (id.isEmpty()) {
            binding.etID.setError("ID를 입력해 주세요!");
            valid = false;
        } else {
            binding.etID.setError(null);
        }

        if (password.isEmpty()) {
            binding.etPassword.setError("Password를 입력해 주세요!");
            valid = false;
        } else {
            binding.etPassword.setError(null);
        }
        return valid;
    }

    private void checkInputID() { // 아이디 입력 시 체킹 함수
        String ID = binding.etID.getText().toString();
        if (ID.isEmpty()) {
            binding.ivCheckID.setImageResource(R.drawable.ic_check_gray);
        } else {
            binding.ivCheckID.setImageResource(R.drawable.ic_check_black);
        }
    }

    private void checkInputPassword() { // 비밀번호 입력 시 체킹 함수
        String password = binding.etPassword.getText().toString();
        if (password.isEmpty()) {
            binding.ivCheckPassword.setImageResource(R.drawable.ic_check_gray);
        } else {
            binding.ivCheckPassword.setImageResource(R.drawable.ic_check_black);
        }
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





