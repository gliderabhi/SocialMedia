package com.example.munnasharma.classes;

import retrofit2.http.Url;

public class User {

    private String username;
    private String email;
    private String profilePicLocation;

    public User(){

    }

    public User(String name, String email,String profilePicLocation){
        this.username = name;
        this.email = email;
        this.profilePicLocation=profilePicLocation;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicLocation() {
        return profilePicLocation;
    }

    public void setProfilePicLocation(String profilePicLocation) {
        this.profilePicLocation = profilePicLocation;
    }

}
