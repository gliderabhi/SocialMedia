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
        params.put(Const.FirstName, firstName);
        params.put(Const.LastName, LastName);
        params.put(Const.College, College);
        params.put(Const.branch, Branch);
        params.put(Const.Email, email);
        params.put(Const.Year, year);
        params.put("MobileNo", MobileNo);
        params.put(Const.sex, sex);

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
