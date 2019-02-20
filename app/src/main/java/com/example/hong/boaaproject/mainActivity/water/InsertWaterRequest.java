package com.example.hong.boaaproject.mainActivity.water;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InsertWaterRequest extends StringRequest {

    final private static String URL = "http://jbh9730.cafe24.com/UpdateWater.php";
    private Map<String, String> parameters;

    public InsertWaterRequest(String userID, String twt, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("userCurrentWater", twt);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
