package com.example.munnasharma.socialmedia;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by MunnaSharma on 6/2/2017.
 */

public class RegisterRequest extends StringRequest {
   // private static  String LOGIN_REQUEST_URL = "http://cazimegliderabhi.000webhostapp.com/LoginFiles/bme.php";
    private Map<String, String> params;

    public RegisterRequest(String firstName,String LastName,String College,String Branch,String year,String email,String MobileNo,String sex,String LOGIN_REQUEST_URL, Response.Listener<String> listener) {
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("FirstName", firstName);
        params.put("LastName", LastName);
        params.put("College", College);
        params.put("Branch", Branch);
        params.put("Email", email);
        params.put("Year", year);
        params.put("MobileNo", MobileNo);
        params.put("Sex", sex);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
