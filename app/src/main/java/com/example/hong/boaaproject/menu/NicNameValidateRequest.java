package com.example.hong.boaaproject.menu;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NicNameValidateRequest extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/NicNameValidate.php";
    private Map<String, String> parameters;

    public NicNameValidateRequest(String userNicName, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userNicName", userNicName);

    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
