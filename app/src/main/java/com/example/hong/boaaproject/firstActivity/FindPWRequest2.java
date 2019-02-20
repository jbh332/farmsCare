package com.example.hong.boaaproject.firstActivity;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class FindPWRequest2 extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/FindPW.php";
    private Map<String, String> parameters;

    public FindPWRequest2(String userID, String pwQuestion, String pwAnswer, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("pwQuestion", pwQuestion);
        parameters.put("pwAnswer", pwAnswer);

    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
