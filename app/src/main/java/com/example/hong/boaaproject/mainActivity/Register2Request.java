package com.example.hong.boaaproject.mainActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Register2Request extends StringRequest {

    final static private String URL = "http://jbh9730.cafe24.com/UserRegister2.php";
    private Map<String, String> parameters;

    public Register2Request(String userID, String userHeight, String userWeight, String userGender, String userBirth, String userImgURL, String userGoalWater, String userGoalSleep, String userGoalSteps, String userGoalCalorie, Response.Listener<String> listener) {

        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("userHeight", userHeight);
        parameters.put("userWeight", userWeight);
        parameters.put("userGender", userGender);
        parameters.put("userBirth", userBirth);
        parameters.put("userImgURL", userImgURL);
        parameters.put("userGoalWater", userGoalWater);
        parameters.put("userGoalSleep", userGoalSleep);
        parameters.put("userGoalSteps", userGoalSteps);
        parameters.put("userGoalCalorie", userGoalCalorie);

    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
