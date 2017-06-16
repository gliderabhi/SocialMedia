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
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

public class MainActivity extends Activity {
//declare variables
    private AccessToken accessToken;
     private Intent i;
    private EditText Email,Password;
    private Button LoginBtn;
    private ImageView linkedInImg,gmailImg,twitterImg;
    private String email,password,sex,f_name,l_name,id,contact;
    private TextView CreateAccount,forgotPass;
    private SessionManager sessionManager;
    private ProgressDialog pr;
    private    SignInButton signInButton;
    private LoginButton fbImg;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    private  GoogleSignInOptions gso;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profTrack;
    private FacebookCallback<LoginResult> mFacebookCallback;
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
        fbImg = (LoginButton) findViewById(R.id.FbImg);


        signInButton = (SignInButton) findViewById(R.id.GmailImg);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //Gmail Login
        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();
                mGoogleApiClient = new GoogleApiClient.Builder(MainActivity.this)
                        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                        .build();

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 0);

            }
        });

    /*linkedInImg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Linkedin Session
            //If logged in linkedin
             AccessToken accessToken;
            LISessionManager.getInstance(getApplicationContext()).init();

            LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
                @Override
                public void onAuthSuccess() {
                    // Authentication was successful.  You can now do
                    // other calls with the SDK.
                }

                @Override
                public void onAuthError(LIAuthError error) {
                    // Handle authentication errors
                }
            }, true);


        }
    });
*/
    //for facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        //set progress dialog
        pr=new ProgressDialog(MainActivity.this);
        pr.setMessage("Logging you in ,please wait");
        pr.setIndeterminate(false);
        pr.setCancelable(false);

        //set permission for facebook profile data
        fbImg.setReadPermissions("email");
        fbImg.setReadPermissions(Arrays.asList("user_status"));
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // App code
                Log.d("current token", "" + currentAccessToken);

                //}
            }
        };
        profTrack = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(
                    Profile oldProfile,
                    Profile currentProfile) {
                // App code
                Log.d("current profile", "" + currentProfile);
                }
        };
//get data from profile
        fbImg.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code

                        // login ok get access token
                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object,
                                                            GraphResponse response) {

                                        if (BuildConfig.DEBUG) {
                                            FacebookSdk.setIsDebugEnabled(true);
                                            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

                                            Profile.getCurrentProfile().getId();
                                            Profile.getCurrentProfile().getFirstName();
                                            Profile.getCurrentProfile().getLastName();
                                           f_name=Profile.getCurrentProfile().getFirstName().toString();
                                           l_name=Profile.getCurrentProfile().getLastName().toString();
                                            email=Profile.getCurrentProfile().getLinkUri().toString();
                                            if(Checkusr(email)){
                                                i=new Intent(getApplicationContext(),Profile.class);
                                                startActivity(i);
                                            }else{
                                              i=new Intent(MainActivity.this,SignUp1.class);
                                            i.putExtra("Email",email);
                                            i.putExtra("sex","");
                                            i.putExtra("FirstName",f_name);
                                            i.putExtra("LastName",l_name);
                                            i.putExtra("ContactNo","");
                                            i.putExtra("College","");
                                            startActivity(i);
                                           }
                                        }
                                    }

                                });
                        request.executeAsync();
                    }


            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login cancelled", Toast.LENGTH_SHORT).show();
                pr.dismiss();

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Login error please try again later", Toast.LENGTH_SHORT).show();
                pr.dismiss();

            }

        });


        accessTokenTracker.startTracking();
        profTrack.startTracking();

      //add click listener for fb button
                    //  LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
fbImg.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {

        LoginManager.getInstance().logInWithReadPermissions((Activity) v.getContext(),Arrays.asList("public_profile", "user_friends"));

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
                i.putExtra("Email","");
                i.putExtra("sex","");
                i.putExtra("FirstName","");
                i.putExtra("LastName","");
                i.putExtra("ContactNo","");
                i.putExtra("College","");
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

    private void ServerConnection()
    {

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
                             Intent intent = new Intent(MainActivity.this, ProfilePage.class);
                            startActivity(intent);


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

        //Facebook ativity result
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
       if (requestCode == 0) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

       //Linked in activity result
    /*    if(requestCode==1) {
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        }
*/

    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Login result", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();

            if(Checkusr(personEmail)){

                i=new Intent(getApplicationContext(),ProfilePage.class);
                startActivity(i);
            }else{
            i=new Intent(getApplicationContext(),SignUp1.class);
            i.putExtra("Email",personEmail);
            i.putExtra("sex","");
            i.putExtra("FirstName",personGivenName);
            i.putExtra("LastName",personFamilyName);
            i.putExtra("ContactNo","");
            i.putExtra("College","");
            startActivity(i);
            }
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
        //    updateUI(false);
        }
    }



    @Override
    public void onBackPressed() {
        pr.dismiss();}
/*
        //if linkedin api running
       APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.cancelCalls(this);
       Toast.makeText(getApplicationContext(),"Login Failed Please try again ",Toast.LENGTH_SHORT).show();
    }
    // Build the list of member permissions our LinkedIn session requires
    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE);
    }

*/
@Override
public void onResume() {
    super.onResume();
    AccessToken.getCurrentAccessToken();
    Log.d("resume current token", "" + AccessToken.getCurrentAccessToken());
    Profile.fetchProfileForCurrentAccessToken();
}

    @Override
    public void onStop() {
        super.onStop();
        profTrack.stopTracking();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profTrack.stopTracking();
    }

    private boolean stmt;
    //check if user exists on db
    private boolean Checkusr(String mail){

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
            pr.setMessage("Checking credentials , Please wait ");
           pr.show();

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
                            stmt=true;


                        } else {
                            pr.dismiss();
                           stmt=false;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            UserExistReq userExistReq = new UserExistReq(mail, responseListener);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(userExistReq);

            int socketTimeout = 30000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            userExistReq.setRetryPolicy(policy);
            queue.add(userExistReq);
        }


return stmt;
    }
}

