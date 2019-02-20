package com.example.hong.boaaproject.menu;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class NicNameChangeRequest extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/NicNameChange.php";
    private Map<String, String> parameters;

    public NicNameChangeRequest(String userID, String userNicName, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userNicName", userNicName);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
