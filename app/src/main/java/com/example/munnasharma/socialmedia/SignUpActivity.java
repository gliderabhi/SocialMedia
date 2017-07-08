package com.example.munnasharma.socialmedia;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.munnasharma.ChatActivities.GroupChatList;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GithubAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.HashMap;
import java.util.Map;
import com.example.munnasharma.classes.*;
import com.example.munnasharma.extras.*;

public class SignUpActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 1;
    private static final String TAG="Firebase Auth Log";
    private CallbackManager mCallbackManager;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private TwitterLoginButton mLoginButton;
    private Intent i;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference userDetails, root2,root3;
    private StudentDetails studentDetails;

    private static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    createUser(user);
                   i=new Intent(getApplicationContext(),GroupChatList.class);
                    startActivity(i);

                } else {
                    // User is signed out
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(
                                            AuthUI.EMAIL_PROVIDER,
                                            AuthUI.GOOGLE_PROVIDER,/*AuthUI.TWITTER_PROVIDER,*/AuthUI.FACEBOOK_PROVIDER)
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

       /* mLoginButton = (TwitterLoginButton) findViewById(R.id.button_twitter_login);
        mLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
            }
        });
*/
      // githubAuthentication();

        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.button_facebook_login);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");

                Toast.makeText(getApplicationContext(),"Login Cancelled",Toast.LENGTH_SHORT).show();
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                // ...
            }
        });



    }

    private void githubAuthentication() {
        //github authentication
        String token = "<GITHUB-ACCESS-TOKEN>";
        AuthCredential credential = GithubAuthProvider.getCredential(token);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       try{ Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        String name=task.getResult().getUser().getDisplayName();
                        String email=task.getResult().getUser().getEmail();
                           String profilePic=task.getResult().getUser().getPhotoUrl().toString();
                        User user=new User(name,email,profilePic);
                        i=new Intent(getApplicationContext(), SignUp1.class);
                        startActivity(i);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }catch (Exception e){
                           Log.i("Error",e.toString());
                       }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //For facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        //for twitter
        mLoginButton.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RC_SIGN_IN) {
                if (resultCode == RESULT_OK) {
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();

                    if(user!=null) {

                        createUser(user);

                        i = new Intent(getApplicationContext(), SignUp1.class);
                        startActivity(i);

                        Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {

            Log.i("Error",e.toString());
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        try {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mFirebaseAuth.getCurrentUser();
                                if (user != null) {
                                    createUser(user);
                                    i = new Intent(getApplicationContext(), GroupChatList.class);
                                    startActivity(i);
                                }else{
                                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (!task.isSuccessful()) {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(getApplicationContext(), "weak pass", Toast.LENGTH_SHORT).show();

                                } catch (FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(getApplicationContext(), "Invalid", Toast.LENGTH_SHORT).show();

                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(getApplicationContext(), "User Exists", Toast.LENGTH_SHORT).show();

                                } catch (Exception e) {
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                            // ...
                        } catch (Exception e) {

                            Log.i("Error",e.toString());
                        }
                    }
                });
    }

    private void handleTwitterSession(TwitterSession session) {

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      try{
                          if (task.isSuccessful()) {
                              // Sign in success, update UI with the signed-in user's information
                              FirebaseUser user = mFirebaseAuth.getCurrentUser();
                              if (user != null) {
                                  createUser(user);
                                  i = new Intent(getApplicationContext(), SignUp1.class);
                                  startActivity(i);
                              }else{
                                  Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                              }
                          }else {
                          // If sign in fails, display a message to the user.
                          Log.w(TAG, "signInWithCredential:failure", task.getException());
                          Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                  Toast.LENGTH_SHORT).show();
                      }


                    }catch (Exception e) {
                          Log.i("Error",e.toString());
                      } // ...
                    }
                });
    }

    private void createUser(FirebaseUser user) {
       //final String newFriendEncodedEmail = encodeEmail(newFriendEmail);
        try{     final DatabaseReference userRef = mFirebaseDatabase.getReference(Const.UserDetails);
        Map<String, Object> map3 = new HashMap<>();
        User  usr =new User(user.getDisplayName(),user.getEmail(),user.getPhotoUrl().toString());
        map3.put(encodeEmail(user.getEmail()),usr);
        userRef.updateChildren(map3);

        Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
      }catch (Exception e){
          Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
          Log.i("Error",e.toString());
      }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStart(){
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mFirebaseAuth.getCurrentUser();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mAuthStateListener!=null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}

