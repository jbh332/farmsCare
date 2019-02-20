package com.example.hong.boaaproject.firstActivity;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NicNameValidateRequest extends StringRequest { // userID를 보내서 회원가입이 가능한지 체크

    final static private String URL = "http://jbh9730.cafe24.com/UserNicNameValidate.php"; // URL 접속
    private Map<String, String> parameters;

    public NicNameValidateRequest(String userNicName, Response.Listener<String> listener) { // 생성자

        super(Method.POST, URL, listener, null); //post 형식으로 해당 URL 파라미터들을 보낸다
        parameters = new HashMap<>();
        parameters.put("userNicName", userNicName);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
