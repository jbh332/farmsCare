package com.example.hong.boaaproject.mainActivity.sleep;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class SleepItemRequest extends StringRequest {


    final static private String URL = "http://jbh9730.cafe24.com/ItemUpload.php";
    private Map<String, String> parameters;

    public SleepItemRequest(String itemCode, String itemURL, String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("itemURL", itemURL);
        parameters.put("itemCode", itemCode);

    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
