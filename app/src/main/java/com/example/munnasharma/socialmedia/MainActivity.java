package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

     private Intent i;
    private EditText email,pass;
    private Button LoginBtn;
    private TextView CreateAccount,forgotPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise the variables
        LoginBtn=(Button)findViewById(R.id.LoginBtn);
        CreateAccount=(TextView)findViewById(R.id.CreateAccount);
        email=(EditText)findViewById(R.id.LoginEmail);
        forgotPass=(TextView)findViewById(R.id.ForgotPassText);
        pass=(EditText)findViewById(R.id.LoginPAassword);


        //Set EditText as empty
         email.setText("");
         pass.setText("");


        //Login Button Listener
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Logged In",Toast.LENGTH_LONG).show();
               // Add Login function
            }
        });

        //Forgot Pass Request For new Pss
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Bhai Bacha le ",Toast.LENGTH_LONG).show();
            //add method for password recovery
            }
        });
        //Listener for new account creation
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(getApplicationContext(),SignUp1.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //add method for back press
    }
}
