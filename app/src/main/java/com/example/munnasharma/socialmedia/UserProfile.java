package com.example.munnasharma.socialmedia;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.munnasharma.classes.User;
import com.example.munnasharma.extras.Const;
import com.example.munnasharma.request.ProfileRequest;
import com.example.munnasharma.request.RegisterRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class UserProfile extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG ="Error" ;
    private TextView Name,Email,College,Branch,Year,Sex,MobileNo,frndText;
    private Button removeFrnd;
    private Spinner addFrn;
    private String email,name,college,branch,year,sex,mobileno,lastName,url,mCurrentUserEmail;
    private ImageView profileImg;
    private User user;
    private ProgressDialog pr;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mCurrentUsersFriends;
    private FirebaseAuth mFirebaseAuth;
    private boolean yes=false;

    //TODO: Used in multiple places, should probably move to its own class
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initialize();

        email = getIntent().getStringExtra(Const.Email);
        getData();

    }

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

    private void getData(){
        boolean success=false;
        //call network check
        boolean network = haveNetworkConnection();
        if (!network) {

            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
            builder.setMessage(Const.checkInternet)
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();
        }

        if (network) {
            pr= ProgressDialog.show(UserProfile.this,"Profile Details ","Getting details please wait",true);


            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    pr.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 60000);
            Response.Listener<String> responseListener = new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    Log.i("success","message received ");
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


                        Log.i("Resposne",response);
                        if (success) {
                            email=jsonResponse.getString(Const.Email);
                            name=jsonResponse.getString(Const.FirstName);
                            college=jsonResponse.getString(Const.College);
                            sex=jsonResponse.getString(Const.sex);
                            branch=jsonResponse.getString(Const.branch);
                            year=jsonResponse.getString(Const.Year);
                            lastName=jsonResponse.getString(Const.LastName);
                            mobileno=jsonResponse.getString("MobileNo");

                            DisplayResults();
                            // Toast.makeText(getApplicationContext(),"Created ",Toast.LENGTH_SHORT).show();

                            user=new User();
                            user.setEmail(email);
                            user.setUsername(name+" "+lastName);
                            user.setProfilePicLocation("");
                            pr.dismiss();
                        } else {
                            pr.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(UserProfile.this);
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
            ProfileRequest profileReq = new ProfileRequest(email,responseListener);
            RequestQueue queue = Volley.newRequestQueue(UserProfile.this);
            profileReq.setRetryPolicy(new DefaultRetryPolicy(
                    30000,2, (float) 2.0));
            queue.add(profileReq);

        }


    }

    private boolean checkUserFrnd(){

        final DatabaseReference friendRef =
                mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                        + "/" + mCurrentUserEmail );
        if(friendRef!=null) {
            friendRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(encodeEmail(email))) {
                        yes = true;
                        frndText.setText("Friends");
                    } else {
                        yes = false;
                        frndText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            yes=false;
        }
        return  yes;
    }
    private boolean checkMentor(){

        final DatabaseReference mentorRef =
                mFirebaseDatabase.getReference(Const.MENTOR_LOCATION
                        + "/" + mCurrentUserEmail );
        if(mentorRef!=null) {
            mentorRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(encodeEmail(email))) {
                        yes = true;
                        frndText.setText("Mentor");
                    } else {
                        yes = false;
                        frndText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            yes=false;
        }
        return  yes;
    }
    private boolean BatchMates(){

        final DatabaseReference batchRef =
                mFirebaseDatabase.getReference(Const.MENTOR_LOCATION
                        + "/" + mCurrentUserEmail );
        if(batchRef!=null) {
            batchRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(encodeEmail(email))) {
                        yes = true;
                        frndText.setText("Batch Mates");
                    } else {
                        yes = false;
                        frndText.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }else{
            yes=false;
        }
        return  yes;
    }
    private void initialize(){
        Name=(TextView)findViewById(R.id.UserName);
        Email=(TextView)findViewById(R.id.EmailText);
        College=(TextView)findViewById(R.id.CollegeNameText);
        Sex=(TextView)findViewById(R.id.UserSex);
        MobileNo=(TextView)findViewById(R.id.MobileNo);
        Branch=(TextView)findViewById(R.id.Branch);
        Year=(TextView)findViewById(R.id.Year);
        frndText=(TextView)findViewById(R.id.CategoryFrnd);

        profileImg=(ImageView)findViewById(R.id.ProfileImgUser);
        addFrn=(Spinner)findViewById(R.id.AddFrndBtn);
        removeFrnd=(Button)findViewById(R.id.RemoveFrndButton);

        removeFrnd.setVisibility(View.GONE );


        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUserEmail = encodeEmail(mFirebaseAuth.getCurrentUser().getEmail());
        //Eventually this list will filter out users that are already your friend
        if (mCurrentUserEmail != null) {
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(Const.USERS_LOCATION);
            mCurrentUsersFriends = mFirebaseDatabase.getReference().child(Const.FRIENDS_LOCATION
                    + "/" + encodeEmail(mCurrentUserEmail));

        }else{
            Log.i("Error","No user authenticated");
        }

        if(checkUserFrnd()){
            frndText.setText("Friends");
            removeFrnd.setVisibility(View.VISIBLE);
            addFrn.setVisibility(View.GONE);
        }
        else{
            if(checkMentor()){
                frndText.setText("Mentor");
                removeFrnd.setText("Remove mentor");
                removeFrnd.setVisibility(View.VISIBLE);
                addFrn.setVisibility(View.GONE);
            }
            else{
                if(BatchMates()){
                    frndText.setText("Batch Mate");
                    removeFrnd.setText("Remove batch match");
                    removeFrnd.setVisibility(View.VISIBLE);
                    addFrn.setVisibility(View.GONE);
                }else{

                    addFrn.setVisibility(View.VISIBLE);
                    removeFrnd.setVisibility(View.GONE);
                }
                addFrn.setVisibility(View.VISIBLE);
                removeFrnd.setVisibility(View.GONE);
            }
            addFrn.setVisibility(View.VISIBLE);
            removeFrnd.setVisibility(View.GONE);
        }
        if(addFrn.getVisibility()==View.VISIBLE){
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, Const.frndCategory);
            addFrn.setAdapter(adapter);
            addFrn.setOnItemSelectedListener(this);
        }
    }

    private void DisplayResults(){
        Name.setText(name+" "+lastName);
        Email.setText(email);
        Sex.setText(sex);
        Branch.setText(branch);
        College.setText(college);
        Year.setText(year);
        MobileNo.setText(mobileno);

    }

    private void removeFriend(String  friendEmail) {
        //Get current user logged in by email
        final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
        Log.e(TAG, "User logged in is: " + userLoggedIn);
        final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                + "/" + encodeEmail(userLoggedIn));

        friendsRef.child(encodeEmail(friendEmail)).removeValue();

    }

    private void addNewFriend(User user) {
        //Get current user logged in by email
        try {
            pr=ProgressDialog.show(getApplicationContext(),"Adding","",true);

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    pr.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 60000);

            final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
            Log.e(TAG, "User logged in is: " + userLoggedIn);
            //final String newFriendEncodedEmail = encodeEmail(newFriendEmail);
            final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                    + "/" + encodeEmail(userLoggedIn));
            //Add friends to current users friends list
            Map<String, Object> map3 = new HashMap<>();
            user = new User(user.getUsername(), user.getEmail(), user.getProfilePicLocation());
            map3.put(encodeEmail(user.getEmail()), user);
            friendsRef.updateChildren(map3);
            if(friendsRef!=null) {
                friendsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(encodeEmail(email))) {

                            removeFrnd.setText("Remove friend");
                            removeFrnd.setVisibility(View.VISIBLE);
                            addFrn.setVisibility(View.GONE);
                            pr.dismiss();
                        } else {
                            pr.dismiss();
                            addFrn.setVisibility(View.VISIBLE);
                            removeFrnd.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        } catch (Exception e) {

            Log.i("Error",e.toString());
            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
        }

    }

    private void addMentor(User user) {
        //Get current user logged in by email
        try {
            final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
            Log.e(TAG, "User logged in is: " + userLoggedIn);
            //final String newFriendEncodedEmail = encodeEmail(newFriendEmail);
            final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Const.MENTOR_LOCATION
                    + "/" + encodeEmail(userLoggedIn));
            //Add friends to current users friends list
            Map<String, Object> map3 = new HashMap<>();
            user = new User(user.getUsername(), user.getEmail(), user.getProfilePicLocation());
            map3.put(encodeEmail(user.getEmail()), user);
            friendsRef.updateChildren(map3);
            if(friendsRef!=null) {
                friendsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(encodeEmail(email))) {

                            removeFrnd.setText("Remove friend");
                            removeFrnd.setVisibility(View.VISIBLE);
                            addFrn.setVisibility(View.GONE);
                            pr.dismiss();
                        } else {
                            pr.dismiss();
                            addFrn.setVisibility(View.VISIBLE);
                            removeFrnd.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        } catch (Exception e) {

            Log.i("Error",e.toString());
            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void addBatchMate(User user) {
        //Get current user logged in by email
        try {
            final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
            Log.e(TAG, "User logged in is: " + userLoggedIn);
            //final String newFriendEncodedEmail = encodeEmail(newFriendEmail);
            final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Const.BATCH_MATE_LOCATION
                    + "/" + encodeEmail(userLoggedIn));
            //Add friends to current users friends list
            Map<String, Object> map3 = new HashMap<>();
            user = new User(user.getUsername(), user.getEmail(), user.getProfilePicLocation());
            map3.put(encodeEmail(user.getEmail()), user);
            friendsRef.updateChildren(map3);
            if(friendsRef!=null) {
                friendsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(encodeEmail(email))) {

                            removeFrnd.setText("Remove friend");
                            removeFrnd.setVisibility(View.VISIBLE);
                            addFrn.setVisibility(View.GONE);
                            pr.dismiss();
                        } else {
                            pr.dismiss();
                            addFrn.setVisibility(View.VISIBLE);
                            removeFrnd.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

        } catch (Exception e) {

            Log.i("Error",e.toString());
            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position){
            case 0: addNewFriend(user);break;
            case 1: addMentor(user);break;
            case 2: addBatchMate(user);break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Log.i("OOps","None slected");
    }
}
