package com.example.hong.boaaproject.mainActivity.sleep;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SleepGoalChangeRequest extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/SleepGoalChange.php";
    private Map<String, String> parameters;

    public SleepGoalChangeRequest(String userID, String userGoalSleep, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userGoalSleep", userGoalSleep);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
