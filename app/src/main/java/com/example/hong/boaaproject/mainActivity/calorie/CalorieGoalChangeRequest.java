package com.example.hong.boaaproject.mainActivity.calorie;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CalorieGoalChangeRequest extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/CalorieGoalChange.php";
    private Map<String, String> parameters;

    public CalorieGoalChangeRequest(String userID, String userGoalCalorie, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userGoalCalorie", userGoalCalorie);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
