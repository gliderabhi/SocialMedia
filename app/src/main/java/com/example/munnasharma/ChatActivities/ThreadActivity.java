package com.example.munnasharma.ChatActivities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.munnasharma.socialmedia.R;

public class ThreadActivity extends AppCompatActivity {

    private Button chatBtn,GroupBtn,ThreadBtn;
    private FloatingActionButton fab;
    private Intent i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);
        //set buttons for the diff options
        chatBtn=(Button)findViewById(R.id.ChatButtonFragment);
        GroupBtn=(Button)findViewById(R.id.GrouChatFragment);
        ThreadBtn=(Button)findViewById(R.id.ThreadButtonFragment);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(getApplicationContext(),ChatActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

        GroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(getApplicationContext(),GroupChatList.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
        ThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fab=(FloatingActionButton)findViewById(R.id.FloatingButtonAddChat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}