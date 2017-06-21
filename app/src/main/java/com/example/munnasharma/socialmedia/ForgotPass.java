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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgotPass extends Activity implements  AdapterView.OnItemSelectedListener{

    private Spinner securityq;
    private EditText securitya,OldPass,Email;
   private  String SecurityQ,SecurityA,oldPAss,email;

    private Button resetPass;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
    // ijnitilaize variable
        securityq=(Spinner)findViewById(R.id.SecurityQuestionForgot);
        securitya=(EditText)findViewById(R.id.SecurityAnswerForgot);
        resetPass=(Button)findViewById(R.id.ResetPassButton);
        OldPass=(EditText)findViewById(R.id.OldPassword);
        Email=(EditText)findViewById(R.id.EmailAddress);
        //Setup the spinner or dropdown menu
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Const.questions);
        securityq.setAdapter(adapter);
        securityq.setOnItemSelectedListener(this);

        //Set button click listener
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecurityA=securitya.getText().toString();
                oldPAss=OldPass.getText().toString();
                email=Email.getText().toString();
                if(!SecurityQ.matches("")) {
                   if(!SecurityA.matches("")){
                      if(!oldPAss.matches("")){
                         if(!email.matches("")){
                             OpenCheck();
                         }else{
                             Toast.makeText(getApplicationContext(),Const.fill_Email,Toast.LENGTH_SHORT).show();

                         }
                      }else{
                          Toast.makeText(getApplicationContext(),Const.fill_pass,Toast.LENGTH_SHORT).show();
                      }
                   }else{
                       Toast.makeText(getApplicationContext(),Const.fill_securityQuestion,Toast.LENGTH_SHORT).show();

                   }
                }
                else{
                        Toast.makeText(getApplicationContext(),Const.selectOption,Toast.LENGTH_SHORT).show();
                    }


            }
        });

    }
//Get Data of the drop down menu
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        SecurityQ=Const.questions[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),Const.selectOption,Toast.LENGTH_SHORT).show();
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

    private void OpenCheck(){
        boolean success=false;
        //call network check

        boolean network = haveNetworkConnection();
        if (!network) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPass.this);
            builder.setMessage(Const.checkInternet)
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();
        }

        if (network) {
            pr = ProgressDialog.show(ForgotPass.this, "Checking credentials ", "Checking your security request on server please wait....", true);
             //Set up the variables

            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    Log.i("success", "message received ");
                    response = response.replaceFirst("<font>.*?</font>", "");
                    int jsonStart = response.indexOf("{");
                    int jsonEnd = response.lastIndexOf("}");

                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart) {
                        response = response.substring(jsonStart, jsonEnd + 1);
                    } else {

                    }

                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");


                        if (success) {
                            pr.dismiss();
                            Intent i = new Intent(getApplicationContext(), NextPageForgotPass.class);
                            i.putExtra(Const.Email,email);
                            startActivity(i);
                          //Check if data is receievd andsame as provided at time of signup
                            //Toast.makeText(getApplicationContext(), "recieved ", Toast.LENGTH_SHORT).show();

                        } else {
                            pr.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(ForgotPass.this);
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
            ForgotPassReq forgotPass = new ForgotPassReq(email,SecurityQ,SecurityA,oldPAss, responseListener);
            RequestQueue queue = Volley.newRequestQueue(ForgotPass.this);
            queue.add(forgotPass);

        }

    }
}
