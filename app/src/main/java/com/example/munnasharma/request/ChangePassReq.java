package com.example.munnasharma.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.munnasharma.extras.Const;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 6/6/2017.
 */
public class ChangePassReq extends StringRequest {
     private Map<String, String> params;
    public ChangePassReq(String paas1,String Email,Response.Listener<String> listener) {
        super(Request.Method.POST, Const.Change_pass_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put(Const.Password, paas1);
        params.put(Const.Email,Email);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
