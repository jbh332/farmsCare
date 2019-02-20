package com.example.hong.boaaproject.mainActivity.walk;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class StepsGoalChangeRequest extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/StepsGoalChange.php";
    private Map<String, String> parameters;

    public StepsGoalChangeRequest(String userID, String userGoalSteps, Response.Listener<String> listener) {

        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userGoalSteps", userGoalSteps);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}

