package com.example.hong.boaaproject.mainActivity.walk;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class InsertWalkRequest extends StringRequest {

    final private static String URL = "http://jbh9730.cafe24.com/UpdateSteps.php";
    private Map<String, String> parameters;

    public InsertWalkRequest(String userID, String userCurrentSteps, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("userCurrentSteps", userCurrentSteps);
    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
