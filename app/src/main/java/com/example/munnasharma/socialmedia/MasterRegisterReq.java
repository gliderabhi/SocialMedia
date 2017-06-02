package com.example.munnasharma.socialmedia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 6/3/2017.
 */

public class MasterRegisterReq  extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "http://cazimegliderabhi.000webhostapp.com/LoginFiles/MasterLogin.php";
    private Map<String, String> params;

    public MasterRegisterReq(String email, String password,String sq,String sa, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("Email", email);
        params.put("Password", password);
        params.put("SecurityQuestion",sq);
        params.put("SecurityAnswer",sa);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}