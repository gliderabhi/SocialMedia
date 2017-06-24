package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class NextPageForgotPass extends Activity {

     private TextView txt;
    private EditText pass1,pass2;
    private  String Pass1,Pass2,email;
    private Button changePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next_page_forgot_pass);
//Initiailze variables
        pass1=(EditText)findViewById(R.id.Pass1);
        pass2=(EditText)findViewById(R.id.Pass2);
        changePass=(Button)findViewById(R.id.ChangPassword);
         txt=(TextView)findViewById(R.id.TextPassNew);
         //get intetn data
        email=getIntent().getStringExtra(Const.Email);
        //button click listener
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pass1=pass1.getText().toString();
                Pass2=pass2.getText().toString();
              //Check if pass is matching and are filled
                if(!Pass1.matches("")){
                    if(!Pass2.matches("")){
                        if(!Pass2.matches(Pass1)){
                            Pass2=PasswordEncrypt.CryptWithMD5(Pass1);
                            ChangePass();
                        }else{
                            Toast.makeText(getApplicationContext(),"Passwords dont match, please check the passwords",Toast.LENGTH_SHORT).show();

                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Please re enter  the password",Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(getApplicationContext(),"Please Fill the password",Toast.LENGTH_SHORT).show();
                }
            }
        });

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


    private void ChangePass(){
        boolean success=false;
        //call network check

        boolean network = haveNetworkConnection();
        if (!network) {

            AlertDialog.Builder builder = new AlertDialog.Builder(NextPageForgotPass.this);
            builder.setMessage(Const.checkInternet)
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();
        }

        if (network) {
            pr = ProgressDialog.show(NextPageForgotPass.this, "Checking credentials ", "Checking your security request on server please wait....", true);
            //Set up the variables

            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    Log.i(Const.Success, "message received ");
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
                            Intent i = new Intent(getApplicationContext(), ProfilePage.class);
                            startActivity(i);
                            //Check if data is receievd andsame as provided at time of signup
                            //Toast.makeText(getApplicationContext(), "recieved ", Toast.LENGTH_SHORT).show();

                        } else {
                            pr.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(NextPageForgotPass.this);
                            builder.setMessage("You entered wrong data please try again later ")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            ChangePassReq forgotPass = new ChangePassReq(Pass2,email, responseListener);
            RequestQueue queue = Volley.newRequestQueue(NextPageForgotPass.this);
            queue.add(forgotPass);


            int socketTimeout = 30000;//30 seconds set timeout
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            forgotPass.setRetryPolicy(policy);
            queue.add(forgotPass);
        }

    }
}
