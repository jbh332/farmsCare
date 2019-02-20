package com.example.hong.boaaproject.firstActivity;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/UserLogin.php"; // URL 접속
    private Map<String, String> parameters;

    public LoginRequest(String userID, String userPW, Response.Listener<String> listener){ // 생성자

        super(Method.POST, URL, listener, null); //post 형식으로 해당 URL 파라미터들을 보낸다
        parameters = new HashMap<>();
        parameters.put("userID", userID); // 해쉬맵 형식으로 PUT
        parameters.put("userPW", userPW);

    }

    @Override
    public Map<String, String> getParams(){
        return parameters;
    }


}