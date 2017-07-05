package com.example.munnasharma.socialmedia;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChatActivity extends AppCompatActivity {

    private Button chatBtn,GroupBtn,ThreadBtn;
    private FloatingActionButton fab;
    Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //set buttons for the diff options
        chatBtn=(Button)findViewById(R.id.ChatButtonFragment);
        GroupBtn=(Button)findViewById(R.id.GrouChatFragment);
        ThreadBtn=(Button)findViewById(R.id.ThreadButtonFragment);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        GroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             i=new Intent(getApplicationContext(),GroupChatList.class);
                startActivity(i);
            }
        });
        ThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          i=new Intent(getApplicationContext(),ThreadActivity.class);
                startActivity(i);
            }
        });
    }
}