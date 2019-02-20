package com.example.hong.boaaproject.communityActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class BoardRegisterRequest extends StringRequest {
    final static private String URL = "http://jbh9730.cafe24.com/BoardRegister.php";
    private Map<String, String> parameters;

    public BoardRegisterRequest(String userID, String boardContent, String boardImgURL, String boardNum, String boardDate, Response.Listener<String> listener) {

        super(Request.Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userID", userID);
        parameters.put("boardContent", boardContent);
        parameters.put("boardImgURL", boardImgURL);
        parameters.put("boardNum", boardNum);
        parameters.put("boardDate", boardDate);

    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }

}
