package com.example.hong.boaaproject.mainActivity.calorie;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CalorieGiftRequest extends StringRequest {

    final private static String URL = "http://jbh9730.cafe24.com/UpdateCalorieGift.php";
    private Map<String, String> parameters;


    public CalorieGiftRequest(String userID, String calorieGiftCheck, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("calorieGiftCheck", calorieGiftCheck);

    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
