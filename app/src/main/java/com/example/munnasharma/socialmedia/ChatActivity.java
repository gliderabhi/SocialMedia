package com.example.munnasharma.socialmedia;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

public class ChatActivity extends FragmentActivity {

    private Button chatBtn,GroupBtn,ThreadBtn;

    Fragment fr;
    FragmentManager fm;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        final android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();

        // Check that the activity is using the layout version with
        // the fragment_container FrameLayout
        if (findViewById(R.id.FragmentLayoutContainer) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            else{
                ThreadFragment myf = new ThreadFragment();

               transaction.add(R.id.FragmentLayoutContainer, myf);
                transaction.commit();
            }

        }
        //set buttons for the diff options
        chatBtn=(Button)findViewById(R.id.ChatButtonFragment);
        GroupBtn=(Button)findViewById(R.id.GrouChatFragment);
        ThreadBtn=(Button)findViewById(R.id.ThreadButtonFragment);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.Fragment chatFragment=new ChatFragment();
                fm=getFragmentManager();
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.FragmentLayoutContainer, chatFragment);
                fragmentTransaction.commit();

            }
        });

        GroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.Fragment groupChatFragment=new GroupChatFragment();
                fm=getFragmentManager();
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.FragmentLayoutContainer, groupChatFragment);
                fragmentTransaction.commit();
            }
        });
        ThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.Fragment threadFragment=new ThreadFragment();
                fm=getFragmentManager();
                fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.FragmentLayoutContainer, threadFragment);
                fragmentTransaction.commit();

            }
        });
    }
}