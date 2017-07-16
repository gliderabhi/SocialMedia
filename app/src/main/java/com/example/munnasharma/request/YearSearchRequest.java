package com.example.munnasharma.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.example.munnasharma.extras.Const;

import java.util.HashMap;
import java.util.Map;


public class YearSearchRequest extends  StringRequest {
        private Map<String, String> params;
        public YearSearchRequest(String Year, Response.Listener<String> listener) {
            super(Request.Method.POST, Const.Check_Year_REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put(Const.Year, Year);
        }

        @Override
        public Map<String, String> getParams() {
            return params;
        }
    }


