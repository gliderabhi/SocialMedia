package com.example.munnasharma.classes;

/**
 * Created by MunnaSharma on 7/8/2017.
 */

public class searchResult {
    private String FirstName,College,Email,LastName;

    public searchResult(String FirstName,String lastName,String College,String Email){
        this.FirstName=FirstName;
        this.LastName=lastName;
        this.College=College;
        this.Email=Email;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
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

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }
}
