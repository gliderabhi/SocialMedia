package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUpPage2 extends Activity  implements  AdapterView.OnItemSelectedListener{

    private String pass1,pass2,Password;
    private EditText Pass1,Pass2,SecurityAnswer;
    private Button SignUpButton;
    private Spinner securityQuestion;

    private String email,securityAnswer,securityQstn,branch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_sign_up_page2);

         //initialize variable
        Pass1=(EditText)findViewById(R.id.pass1);
        Pass2=(EditText)findViewById(R.id.pass2);
        SignUpButton=(Button)findViewById(R.id.SignUpButton);
        SecurityAnswer=(EditText)findViewById(R.id.SecurityAnswer);
        securityQuestion=(Spinner)findViewById(R.id.SecurityQuestion);

        //Setup the spinner or dropdown menu
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Const.questions);
        securityQuestion.setAdapter(adapter);
        securityQuestion.setOnItemSelectedListener(this);
        //get intent data
        email=getIntent().getStringExtra(Const.Email);
         branch=getIntent().getStringExtra(Const.branch);
        //Check data recieved or not
       // Toast.makeText(getApplicationContext(),email,Toast.LENGTH_SHORT).show();

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //Get the security Answer
                if(!SecurityAnswer.getText().toString().matches("")){
                    securityAnswer=SecurityAnswer.getText().toString();
                }else{
                    Toast.makeText(getApplicationContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();

                }

                // Check if fields are filled and are same
               if(!Pass1.getText().toString().matches("")){
                   if(!Pass2.getText().toString().matches("")){
                       pass1=Pass1.getText().toString();
                       pass2=Pass2.getText().toString();
                       if(pass1.matches(pass2)){
                          // Toast.makeText(getApplicationContext(), "Matches", Toast.LENGTH_SHORT).show();
                           DataSend();
                       }else{
                           Toast.makeText(getApplicationContext(), "Passwords Do not match. Re enter passwords ", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        securityQstn=Const.questions[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    Toast.makeText(getApplicationContext(),"Please select a question",Toast.LENGTH_SHORT).show();
    }

    private ProgressDialog pr;

    //Check Network Connection
    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private boolean DataSend(){
        boolean success=false;
        //call network check
        boolean network = haveNetworkConnection();
        if (!network) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpPage2.this);
            builder.setMessage(Const.checkInternet)
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();
        }

        if (network) {
            pr= ProgressDialog.show(SignUpPage2.this,"Create Account ","Registering on server please wait....",true);





            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    Log.i(Const.Success,"message received ");
                    response = response.replaceFirst("<font>.*?</font>", "");
                    int jsonStart = response.indexOf("{");
                    int jsonEnd = response.lastIndexOf("}");

                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart) {
                        response = response.substring(jsonStart, jsonEnd + 1);
                    } else {

                    }

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean(Const.Success);


                        if (success) {
                            pr.dismiss();
                            success=true;

                            Intent i = new Intent(getApplicationContext(), SignUpPage3.class);
                            startActivity(i);

                          Toast.makeText(getApplicationContext(),"Created ",Toast.LENGTH_SHORT).show();

                        } else {
                            pr.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpPage2.this);
                            builder.setMessage("Email is already available or Please try again later ")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                            success=false;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            Password=PasswordEncrypt.CryptWithMD5(pass1);
            MasterRegisterReq masterRegisterReq = new MasterRegisterReq(email,Password,branch,securityQstn,securityAnswer,responseListener);
            RequestQueue queue = Volley.newRequestQueue(SignUpPage2.this);
            masterRegisterReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000,2, (float) 2.0));
            queue.add(masterRegisterReq);


        }
        return  success;
    }

}
