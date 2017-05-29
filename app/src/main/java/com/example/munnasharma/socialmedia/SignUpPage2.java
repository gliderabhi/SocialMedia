package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpPage2 extends Activity {

    private String pass1,pass2;
    private EditText Pass1,Pass2;
    private Button SignUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_sign_up_page2);

         //initialize variable
        Pass1=(EditText)findViewById(R.id.pass1);
        Pass2=(EditText)findViewById(R.id.pass2);
        SignUpButton=(Button)findViewById(R.id.SignUpButton);

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               // Check if fields are filled and are same
               if(!Pass1.getText().toString().matches("")){
                   if(!Pass2.getText().toString().matches("")){
                       pass1=Pass1.getText().toString();
                       pass2=Pass2.getText().toString();
                       if(pass1.matches(pass2)){
                           Toast.makeText(getApplicationContext(), "Matches", Toast.LENGTH_SHORT).show();
                       }else{
                           Toast.makeText(getApplicationContext(), "Passwords Dont wmatch. Re eneter passwords ", Toast.LENGTH_SHORT).show();
                          Pass1.setText("");Pass2.setText("");
                       }
                   }else{
                       Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

                   }
               }else{
                   Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

               }
                           }
        });
     }
}
