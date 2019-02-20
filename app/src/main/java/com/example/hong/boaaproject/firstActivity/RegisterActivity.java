package com.example.hong.boaaproject.firstActivity;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.hong.boaaproject.R;
import com.example.hong.boaaproject.databinding.ActivityRegisterBinding;

import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding b;
    private ArrayAdapter<String> adapter;
    private boolean IDValidate = false;
    private boolean NicNameValidate = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        b = DataBindingUtil.setContentView(this, R.layout.activity_register);

        // 스피너 질문
        final String[] question = getResources().getStringArray(R.array.pwQuestion);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, question);
        b.spnPwQuestion.setAdapter(adapter);


        //  아이디 중복체크
        b.btnValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = b.etID.getText().toString();

                if (IDValidate) {

                    return;
                }

                // 중복체크 ( 서버 연동 부분 )
                userIDCheck(userID);
            }
        });

        // 닉네임 중복체크
        b.btnNicNameValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userNicName = b.etNicName.getText().toString();

                if (NicNameValidate) {

                    return;
                }

                // 중복체크 ( 서버 연동 부분 )
                userNicNameCheck(userNicName);
            }
        });

        // 회원 가입
        b.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userID = b.etID.getText().toString();
                String userPW = b.etPassword.getText().toString();
                String userNicName = b.etNicName.getText().toString();
                String pwAnswer = b.etPwAnswer.getText().toString();
                String pwQuestion = b.spnPwQuestion.getSelectedItem().toString();

                if (validate() == false) {
                    return;
                }

                if (!IDValidate) {

                    Toast.makeText(RegisterActivity.this, "아이디 중복 체크를 해주세요", Toast.LENGTH_LONG).show();
                    return;
                }

                if (!NicNameValidate) {

                    Toast.makeText(RegisterActivity.this, "닉네임 중복 체크를 해주세요", Toast.LENGTH_LONG).show();
                    return;
                }

                // 회원 가입 ( 서버 연동 부분 )
                registerComplete(userID, userPW, userNicName, pwQuestion, pwAnswer);
            }
        });


        // 아이디 실시간 입력 체크
        b.etID.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                checkInputID();
                return false;
            }
        });

        b.etID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkInputID();
            }
        });


        // 비밀번호 실시간 입력 체크
        b.etPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                checkInputPassword();
                return false;
            }
        });

        b.etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkInputPassword();
            }
        });

        // 재확인 실시간 입력 체크
        b.etPassword2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                checkInputPassword2();
                return false;
            }
        });

        b.etPassword2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkInputPassword2();
            }
        });


        // 닉네임 입력 실시간 입력 체크
        b.etNicName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                checkInputNicName();
                return false;
            }
        });

        b.etNicName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkInputNicName();
            }
        });


        // 비밀번호 질문 입력 실시간 입력 체크
        b.etPwAnswer.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                checkInputPwAnswer();
                return false;
            }
        });

        b.etPwAnswer.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                checkInputPwAnswer();
            }
        });

    }

    private void userNicNameCheck(String userNicName) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        Toast.makeText(RegisterActivity.this, "사용할 수 있는 닉네임입니다.", Toast.LENGTH_LONG).show();
                        NicNameValidate = true;
                    } else {

                        Toast.makeText(RegisterActivity.this, "사용할 수 없는 닉네임입니다.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        };
        NicNameValidateRequest nicNameValidateRequest = new NicNameValidateRequest(userNicName, responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(nicNameValidateRequest);
    }


    private void userIDCheck(String userID) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");

                    if (success) {

                        Toast.makeText(RegisterActivity.this, "사용할 수 있는 아이디입니다.", Toast.LENGTH_LONG).show();
                        IDValidate = true;
                    } else {

                        Toast.makeText(RegisterActivity.this, "사용할 수 없는 아이디입니다.", Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        };

        ValidateRequest validateRequest = new ValidateRequest(userID, responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(validateRequest);
    }

    private void registerComplete(String userID, String userPW, String userNicName, String pwQuestion, String pwAnswer) {

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success) { //회원가입에 성공한 경우

                        Toast.makeText(RegisterActivity.this, "회원가입에 성공했습니다.", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // 회원등록 액티비티창 닫음

                    } else {
                        Toast.makeText(RegisterActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        RegisterRequest registerRequest = new RegisterRequest(userID, userPW, userNicName, pwQuestion, pwAnswer, responseListener);
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        queue.add(registerRequest);

    }
    private void checkInputID() {
        String ID = b.etID.getText().toString();
        if (ID.isEmpty()) {
            b.ivCheckID.setImageResource(R.drawable.ic_check_gray);
        } else {
            b.ivCheckID.setImageResource(R.drawable.ic_check_black);
        }
    }

    private void checkInputNicName() {
        String NicName = b.etNicName.getText().toString();
        if (NicName.isEmpty()) {
            b.ivCheckNicName.setImageResource(R.drawable.ic_check_gray);
        } else {
            b.ivCheckNicName.setImageResource(R.drawable.ic_check_black);
        }
    }

    private void checkInputPassword() {
        String Password = b.etPassword.getText().toString();
        if (Password.isEmpty()) {
            b.ivCheckPassword.setImageResource(R.drawable.ic_check_gray);
        } else {
            b.ivCheckPassword.setImageResource(R.drawable.ic_check_black);
        }
    }

    private void checkInputPassword2() {
        String Password = b.etPassword.getText().toString();
        if (Password.isEmpty()) {
            b.ivCheckPassword2.setImageResource(R.drawable.ic_check_gray);
        } else {
            b.ivCheckPassword2.setImageResource(R.drawable.ic_check_black);
        }
    }

    private void checkInputPwAnswer() {
        String Password = b.etPwAnswer.getText().toString();
        if (Password.isEmpty()) {
            b.ivCheckQ.setImageResource(R.drawable.ic_check_gray);
        } else {
            b.ivCheckQ.setImageResource(R.drawable.ic_check_black);
        }
    }

    private boolean validate() { // 빈칸 및 비밀번호 일치 유효성 검사 함수

        boolean valid = true;
        String ID, nicName, password, password2, pwAnswer;
        ID = b.etID.getText().toString();
        nicName = b.etNicName.getText().toString();
        password = b.etPassword.getText().toString();
        password2 = b.etPassword2.getText().toString();
        pwAnswer = b.etPwAnswer.getText().toString();

        if (ID.isEmpty()) {
            b.etID.setError("아이디를 입력해 주세요!");
            valid = false;
        } else {
            b.etID.setError(null);
        }

        if (!password.equals(password2)) {
            b.etPassword2.setError("비밀번호가 일치하지 않습니다!");
            valid = false;
        } else {
            b.etPassword2.setError(null);
        }

        if (nicName.isEmpty()) {
            b.etNicName.setError("닉네임을 입력해 주세요!");
            valid = false;
        } else {
            b.etNicName.setError(null);
        }

        if (password.isEmpty()) {
            b.etPassword.setError("Password를 입력해 주세요!");
            valid = false;
        } else {
            b.etPassword.setError(null);
        }

        if (password2.isEmpty()) {
            b.etPassword2.setError("Password를 입력해 주세요!");
            valid = false;
        } else {
            b.etPassword2.setError(null);
        }

        if (pwAnswer.isEmpty()) {
            b.etPwAnswer.setError("비밀번호 찾기 답을 입력해 주세요!");
            valid = false;
        } else {
            b.etPwAnswer.setError(null);
        }
        return valid;
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

