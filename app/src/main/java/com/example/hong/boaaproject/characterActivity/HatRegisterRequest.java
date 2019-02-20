package com.example.hong.boaaproject.characterActivity;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class HatRegisterRequest extends StringRequest {

    final private static String URL = "http://jbh9730.cafe24.com/UpdateHats.php";
    private Map<String, String> parameters;

    public HatRegisterRequest(String userID, String hatURL, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("hatURL", hatURL);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}