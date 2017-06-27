package com.example.munnasharma.socialmedia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 6/26/2017.
 */

public class CollegeSearchRequest extends StringRequest {
    private Map<String, String> params;
    public CollegeSearchRequest(String College, Response.Listener<String> listener) {
        super(Request.Method.POST, Const.Check_College_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put(Const.College, College);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
