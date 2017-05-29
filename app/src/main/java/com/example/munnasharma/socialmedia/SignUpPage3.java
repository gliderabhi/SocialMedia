package com.example.munnasharma.socialmedia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class SignUpPage3 extends AppCompatActivity {

    private Button resendLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page3);

         resendLink=(Button)findViewById(R.id.ResendButton);

        resendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Sent",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
