package com.example.hong.boaaproject.mainActivity.water;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

public class WaterGoalChangeRequest extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/WaterGoalChange.php";
    private Map<String, String> parameters;

    public WaterGoalChangeRequest(String userID, String userGoalWater, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userGoalWater", userGoalWater);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
