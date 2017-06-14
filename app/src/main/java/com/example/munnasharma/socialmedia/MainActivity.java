package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.Arrays;

public class MainActivity extends Activity  {

     private Intent i;
    private EditText Email,Password;
    private Button LoginBtn;
    private ImageView linkedInImg,gmailImg,twitterImg;
    private String email,password;
    private TextView CreateAccount,forgotPass;
    private SessionManager sessionManager;
    private ProgressDialog pr;
    private LoginButton fbImg;
    private CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 //Set up sesion manager to know if the user is already logged in
        sessionManager=new SessionManager(getApplicationContext());
        if(sessionManager.isLoggedIn()){
            //Show Logged in , later open the profile straight away
//            Toast.makeText(getApplicationContext(),"Already Logged In ",Toast.LENGTH_SHORT).show();
        }
        //Initialise the variables
        LoginBtn=(Button)findViewById(R.id.LoginBtn);
        CreateAccount=(TextView)findViewById(R.id.CreateAccount);
        Email=(EditText)findViewById(R.id.LoginEmail);
        forgotPass=(TextView)findViewById(R.id.ForgotPassText);
        Password=(EditText)findViewById(R.id.LoginPAassword);
        linkedInImg=(ImageView)findViewById(R.id.LinedInImg);
        gmailImg=(ImageView)findViewById(R.id.GmailImg);
        twitterImg=(ImageView)findViewById(R.id.TwitterImg);
        fbImg = (LoginButton) findViewById(R.id.FbImg);
        fbImg.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();



      //  LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
fbImg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        fbImg.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String name=loginResult.getAccessToken().getUserId();
                String user=loginResult.getAccessToken().getToken();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException e) {

            }
        });
    }
});

        //Set EditText as empty
         Email.setText("");
         Password.setText("");


        //Login Button Listener
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(),"Logged In",Toast.LENGTH_LONG).show();
               // Add Login function

                ServerConnection();

            }
        });

        //Forgot Pass Request For new Pass
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Bhai Bacha le ",Toast.LENGTH_LONG).show();
            //add method for password recovery
                Intent i =new Intent(getApplicationContext(),ForgotPass.class);
                startActivity(i);
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



    //Check for network connection
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

    private void ServerConnection(){

        //call network check
        boolean network = haveNetworkConnection();
        if (!network) {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("Please check your internet connection ")
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();
        }

        if (network) {

            email = Email.getText().toString();
            password = Password.getText().toString();
            //Apply Encryption to password
            password = PasswordEncrypt.CryptWithMD5(password);
           // Toast.makeText(getApplicationContext(), password, Toast.LENGTH_SHORT).show();

            if (email.matches("") && password.matches("")) {
                Toast.makeText(MainActivity.this, "Please fill both the email and password", Toast.LENGTH_SHORT).show();
                return;
            } else if (email.matches("")) {
                Toast.makeText(this, "You did not enter a email", Toast.LENGTH_SHORT).show();
                return;
            } else if (password.matches("")) {
                Toast.makeText(this, "Please fill the  password", Toast.LENGTH_SHORT).show();
                return;
            } else {
                pr = ProgressDialog.show(MainActivity.this, "Log in ", "Logging you in please wait.... ", true);


                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        Log.i("error", "message recieved ");
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
                                String Email = jsonResponse.getString("Email");
                                String FirstName = jsonResponse.getString("FirstName");
                                String LastName = jsonResponse.getString("LastName");
                                String College = jsonResponse.getString("College");
                                String Branch = jsonResponse.getString("Branch");
                                String Year = jsonResponse.getString("Year");
                                String MobileNo = jsonResponse.getString("MobileNo");
                                String Sex = jsonResponse.getString("Sex");

                                //open profile
                            /*  Intent intent = new Intent(MainActivity.this, GetLocation.class);
                            startActivity(intent);

*/
                                sessionManager.createLoginSession(FirstName, LastName, College, Branch, Year, Email, MobileNo, Sex);
                                Toast.makeText(getApplicationContext(), "Received ", Toast.LENGTH_SHORT).show();

                            } else {
                                pr.dismiss();
                               // String error = jsonResponse.getString("error");
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("Login Fialed Please Try again later")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(email, password, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);

                int socketTimeout = 30000;//30 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                loginRequest.setRetryPolicy(policy);
                queue.add(loginRequest);
            }
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        pr.dismiss();
       Toast.makeText(getApplicationContext(),"Login Failed Please try again ",Toast.LENGTH_SHORT).show();
    }



}
