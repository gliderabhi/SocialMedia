package com.example.munnasharma.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.munnasharma.extras.Const;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 7/16/2017.
 */

public class SearchByName  extends StringRequest {
    private Map<String, String> params;
    public SearchByName(String Name, Response.Listener<String> listener) {
        super(Request.Method.POST, Const.Check_Name_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put(Const.FirstName, Name);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}


