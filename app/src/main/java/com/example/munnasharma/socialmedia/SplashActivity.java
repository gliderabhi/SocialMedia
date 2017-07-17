package com.example.munnasharma.socialmedia;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.example.munnasharma.ChatActivities.GroupChatList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;
    ImageView img;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private Intent i;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        img = (ImageView) findViewById(R.id.ImageView8);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
       /* mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    GroupChatList();

                } else {
                    Log.i("Error", "Error");
                    SignUpChatActivity();
                }
            }
        };*/
       user=mFirebaseAuth.getCurrentUser();
        if(user==null){
            SignUpChatActivity();
        }else{
            GroupChatList();
        }

    }
    private void GroupChatList(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

             i=new Intent(getApplicationContext(),GroupChatList.class);
                startActivity(i);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
    private void SignUpChatActivity(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                i=new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(i);
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}



