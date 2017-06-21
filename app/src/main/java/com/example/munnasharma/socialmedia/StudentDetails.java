package com.example.munnasharma.socialmedia;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by MunnaSharma on 5/31/2017.
 */

public class StudentDetails {

 private String FirstName,LastName,College,Branch,Email,MobileNo,Year,sex;

  public StudentDetails(){

    }
    public StudentDetails(String firstName,String LastName,String College,String Branch,String year,String email,String MobileNo,String sex){
       //Save the data sent of students in objetcs created
        this.FirstName=firstName;
        this.LastName=LastName;
        this.College=College;
        this.Branch=Branch;
        this.Email=email;
        this.Year=year;
        this.sex=sex;
        this.MobileNo=MobileNo;

    }


    public String getBranch() {
        return Branch;
    }

    public void setBranch(String branch) {
        Branch = branch;
    }

    public String getCollege() {
        return College;
    }

    public void setCollege(String college) {
        College = college;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        Year = year;
    }
}
