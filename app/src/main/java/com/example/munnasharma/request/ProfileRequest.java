package com.example.munnasharma.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.munnasharma.extras.Const;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 7/8/2017.
 */

public class ProfileRequest  extends StringRequest {
    private Map<String, String> params;
    public ProfileRequest(String Email,Response.Listener<String> listener) {
        super(Request.Method.POST, Const.Profile_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put(Const.Email,Email);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
