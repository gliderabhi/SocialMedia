package com.example.munnasharma.socialmedia;

import retrofit2.http.Url;

public class User {

    private String username;
    private String email;
    private Url profilePicLocation;

    public User(){

    }

    public User(String name, String email){
        this.username = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Url getProfilePicLocation() {
        return profilePicLocation;
    }

}
