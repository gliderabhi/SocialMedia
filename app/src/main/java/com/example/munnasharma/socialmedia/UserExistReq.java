package com.example.munnasharma.socialmedia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 6/16/2017.
 */

public class UserExistReq extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://cazimegliderabhi.000webhostapp.com/checkUserExistence.php";
    private Map<String, String> params;
    public UserExistReq(String Email,Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("Email",Email);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
