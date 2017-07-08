package com.example.munnasharma.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.munnasharma.extras.Const;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 6/2/2017.
 */
public class LoginRequest extends StringRequest {
    private Map<String, String> params;

    public LoginRequest(String email, String password, Response.Listener<String> listener) {
        super(Request.Method.POST, Const.LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put(Const.Email, email);
        params.put(Const.Password, password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
