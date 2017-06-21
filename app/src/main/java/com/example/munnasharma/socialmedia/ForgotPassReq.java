package com.example.munnasharma.socialmedia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 6/5/2017.
 */

public class ForgotPassReq extends StringRequest {
    private Map<String, String> params;
    public ForgotPassReq(String email,String sq, String sa,String pass, Response.Listener<String> listener) {
        super(Request.Method.POST, Const.FORGOT_PASS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("SecurityQuestion", sq);
        params.put(Const.Email,email);
        params.put("OldPass",pass);
        params.put("SecurityAnswer", sa);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
