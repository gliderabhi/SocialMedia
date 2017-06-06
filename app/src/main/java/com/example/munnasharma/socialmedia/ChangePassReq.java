package com.example.munnasharma.socialmedia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 6/6/2017.
 */
public class ChangePassReq extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://cazimegliderabhi.000webhostapp.com/ChangePass.php";
    private Map<String, String> params;
    public ChangePassReq(String paas1,String Email,Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("Password", paas1);
        params.put("Email",Email);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
