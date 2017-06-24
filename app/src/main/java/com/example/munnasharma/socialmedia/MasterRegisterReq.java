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
    private Map<String, String> params;

    public MasterRegisterReq(String email, String password,String branch,String sq,String sa, Response.Listener<String> listener) {
        super(Request.Method.POST, Const.Register_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put(Const.Email, email);
        params.put(Const.branch,branch);
        params.put(Const.Password, password);
        params.put(Const.sq,sq);
        params.put(Const.sa,sa);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}