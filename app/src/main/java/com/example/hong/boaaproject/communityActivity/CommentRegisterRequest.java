package com.example.hong.boaaproject.communityActivity;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class CommentRegisterRequest extends StringRequest {

    final private static String URL = "http://jbh9730.cafe24.com/CommentRegister.php";
    private Map<String, String> parameters;

    public CommentRegisterRequest(String userID, String boardComment, String boardNum, String commentNum, String commentDate, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();

        parameters.put("userID", userID);
        parameters.put("boardComment", boardComment);
        parameters.put("boardNum", boardNum);
        parameters.put("commentNum", commentNum);
        parameters.put("commentDate", commentDate);

    }

    @Override
    protected Map<String, String> getParams() {
        return parameters;
    }
}
