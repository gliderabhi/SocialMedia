package com.example.munnasharma.socialmedia;

import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "StudentLoginDetails";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "FirstName";
    public static final String KEY_EMAIL = "Email";
    public static final String KEY_LASTNAME="LastName";
    public static final String KEY_CONTACT="MobileNo";
    public  static final String KEY_BRANCH="Branch";
    public  static final String KEY_COLLEGE="College";
    public  static final String KEY_Year="Year";
    public  static final String KEY_Sex="Sex";


    public SessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String firstName,String LastName,String College,String Branch,String year,String email,String MobileNo,String sex){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, firstName);
        editor.putString(KEY_LASTNAME, LastName);
        editor.putString(KEY_BRANCH, Branch);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_Year, year);
        editor.putString(KEY_COLLEGE, College);
        editor.putString(KEY_CONTACT, MobileNo);
        editor.putString(KEY_Sex, sex);

        editor.commit();
    }

   /* public void insertDetails(float lat,float lng){
        editor.putFloat(KEY_LATITUDE, lat);
        editor.putFloat(KEY_LONGITUDE, lng);
        editor.commit();

    }*/
    public void checkLogin(){
        if(!this.isLoggedIn()){
         Intent i = new Intent(_context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }

    }
    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        return user;
    }


    public void logoutUser(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }




    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }

}
