package com.example.hong.boaaproject.mainActivity.sleep;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InsertSleepRequest extends StringRequest {

    final private static String URL = "http://jbh9730.cafe24.com/UpdateSleep.php";
    private Map<String, String> parameters;

    public InsertSleepRequest(String userID, String userCurrentSleep, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("userCurrentSleep", userCurrentSleep);


    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
