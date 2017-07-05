package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


public class SignUp1 extends Activity  implements  AdapterView.OnItemSelectedListener{

    private Button NextPage;
    private Spinner Branchspinner,YearSpinner;
     private CheckBox MaleBox,FemaleBox;
    private String firstname,lastName,college,branch,email,mobileNo,sex,year,mail,colege,mobile_no,f_name,l_name,sx;
    private EditText FirstName,LastName,College,Email,MobileNo,Year;
     private ProgressDialog pr;
    private boolean yes;
    private ImageView profileImg;
    private FirebaseAuth mFirebaseAuth;
    private StudentDetails studentDetails;
    private FirebaseUser user;
    private DatabaseReference mCurrentUserDatabaseReference;
    private Context mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);
    //set up firebase auth user
        mFirebaseAuth = FirebaseAuth.getInstance();

     mail="";
        mobile_no="";
        colege="";
        sx="";
        f_name="";
        l_name="";
        //Variable initialization
        NextPage = (Button) findViewById(R.id.NextPage);
        FirstName = (EditText) findViewById(R.id.FirstNameTextField);
        LastName = (EditText) findViewById(R.id.LastNameTextField);
        College = (EditText) findViewById(R.id.CollegeTextField);
        Email = (EditText) findViewById(R.id.EmailTextField);
        MobileNo = (EditText) findViewById(R.id.MobileNoTextField);
        MaleBox = (CheckBox) findViewById(R.id.MaleCheckBox);
        FemaleBox = (CheckBox) findViewById(R.id.FemaleCheckBox);
         Branchspinner=(Spinner)findViewById(R.id.BranchSpinner);
         YearSpinner=(Spinner)findViewById(R.id.YearSpinner);
         profileImg=(ImageView)findViewById(R.id.ProfileImg);

        user=mFirebaseAuth.getCurrentUser();

        //select image
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();
            }
        });
        //initialize user

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Const.Branches);
        Branchspinner.setAdapter(adapter);
       Branchspinner.setOnItemSelectedListener(this);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Const.years);
        YearSpinner.setAdapter(adapter);
        YearSpinner.setOnItemSelectedListener(this);
        // set all fields to be empty
        fillFields();
        //Add Listenr for checkBoxes ,Only one is to be checked
        MaleBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneBoxOnly(R.id.MaleCheckBox);
            }
        });
        FemaleBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OneBoxOnly(R.id.FemaleCheckBox);
            }
        });

        //Button click listenr, get all the filed values
        NextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //store values from the fields as string
                // check if any field is empty and ask for refilling

                if (!FirstName.getText().toString().matches("")) {
                    firstname = FirstName.getText().toString();

                    if (!LastName.getText().toString().matches("")) {
                        lastName = LastName.getText().toString();

                        if (!College.getText().toString().matches("")) {
                            college = College.getText().toString();

                            if (!Email.getText().toString().matches("")) {
                                email = Email.getText().toString();
                                //Validate Email Address to be of iitbhu
                                 /* if( getEmailDomain().matches("itbhu.ac.in") || getEmailDomain().matches("iitbhu.ac.in") ||  getEmailDomain().matches("itbhu.ac.in ") || getEmailDomain().matches("iitbhu.ac.in ")) {
                                       email = Email.getText().toString();
                                    } else{
                                        Toast.makeText(getApplicationContext(),"Please Enter a valid institute email id ",Toast.LENGTH_SHORT).show();
                                    }*/
                                if (!MobileNo.getText().toString().matches("")) {
                                    mobileNo = MobileNo.getText().toString();

                                    yes = true;
                                } else {
                                    Toast.makeText(getApplicationContext(), Const.fill_Mob, Toast.LENGTH_SHORT).show();
                                    yes = false;
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), Const.fill_Email, Toast.LENGTH_SHORT).show();
                                yes = false;
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), Const.fill_College, Toast.LENGTH_SHORT).show();
                            yes = false;
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), Const.fill_LastName, Toast.LENGTH_SHORT).show();
                        yes = false;
                    }

                } else {
                    Toast.makeText(getApplicationContext(), Const.fill_FirstName, Toast.LENGTH_SHORT).show();
                    yes = false;
                }


                if (MaleBox.isChecked()) {
                    sex = Const.Male;
                }
                if (FemaleBox.isChecked()) {
                    sex = Const.Female;
                }
                // what to do next with the values
                //Create student object
                studentDetails = new StudentDetails(firstname, lastName, college, branch, year, email, mobileNo, sex);


                if(profileImg==null){
                    AlertDialog builder=new AlertDialog.Builder(SignUp1.this)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    openImageSelector();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), "Please Select an image ", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setMessage("Select an image").create();
                    builder.show();

                }
                //Send to server add the add row to respective table

               //if all fields set then only send data
                if (yes) {
                   /// createUser(user);
                   DataSend();
                     }
            }

        });


    }



    //Method to check if only one box is checked
       private void OneBoxOnly(int v){
       if(v==R.id.MaleCheckBox){
           MaleBox.setChecked(true);
           FemaleBox.setChecked(false);
       }

       if(v==R.id.FemaleCheckBox){
           MaleBox.setChecked(false);
           FemaleBox.setChecked(true);
       }
   }
    // set the fields to empty
    private void fillFields(){
        FirstName.setText(user.getDisplayName());
        LastName.setText(l_name);
        College.setText(colege);
        MobileNo.setText(mobile_no);
        Email.setText(user.getEmail());
        if((sx.matches("male")) ){
           MaleBox.setChecked(true);
            FemaleBox.setChecked(false);
        }else if(sx.matches("female")){
            FemaleBox.setChecked(true);
            MaleBox.setChecked(false);
        }else{

            FemaleBox.setChecked(false);
            MaleBox.setChecked(false);
        }
        Glide
                .with(getApplicationContext())
                .load(user.getPhotoUrl()) // the uri you got from Firebase
                .centerCrop()
                .into(profileImg);

    }

    public String getEmailDomain() {
        String b = email.substring(email.indexOf('@') + 1);
        return b;
    }

    //methods for spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),Const.fill_Branch_year,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
       // find which spinner is used and get the data selected
        switch(parent.getId()){
        //for branches
            case R.id.BranchSpinner:  switch (position) {
                case 0:
                    branch = "apc";
                    break;
                case 1:
                    branch = "app";
                    break;
                case 2:
                    branch = "apm";
                    break;
                case 3:
                    branch = "hss";
                    break;
                case 4:
                    branch = "cer";
                    break;
                case 5:
                    branch = "che";
                    break;
                case 6:
                    branch = "civ";
                    break;
                case 7:
                    branch = "cse";
                    break;
                case 8:
                    branch = "eee";
                    break;
                case 9:
                    branch = "ece";
                    break;
                case 10:
                    branch = "mec";
                    break;
                case 11:
                    branch = "met";
                    break;
                case 12:
                    branch = "min";
                    break;
                case 13:
                    branch = "phe";
                    break;
                case 14:
                    branch = "bce";
                    break;
                case 15:
                    branch = "bme";
                    break;
                case 16:
                    branch = "mst";
                    break;

            }
                break;
         //for year
            case R.id.YearSpinner: switch (position) {
                case 0:
                    year =Const.FirstYear;
                    break;
                case 1:
                    year = Const.SecondYear;
                    break;
                case 2:
                    year =Const.ThirdYear;
                    break;
                case 3:
                    branch =Const.FourthYear;
                    break;
                case 4:
                    year =Const.MTech;
                    break;
                case 5:
                    year = Const.Phd;
                    break;

            }
                 break;
        }
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

    private boolean DataSend(){
        boolean success=false;
        //call network check
        boolean network = haveNetworkConnection();
        if (!network) {

            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp1.this);
            builder.setMessage(Const.checkInternet)
                    .setNegativeButton("Retry",null)
                    .create()
                    .show();
        }

        if (network) {
            pr= ProgressDialog.show(SignUp1.this,"Create Account ","Registering on server please wait....",true);





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


                        if (success) {
                            pr.dismiss();
                           success=true;

                            Intent i = new Intent(getApplicationContext(), SignUpPage2.class);
                            i.putExtra(Const.Email, email);
                            i.putExtra(Const.branch,branch);
                            startActivity(i);

                           // Toast.makeText(getApplicationContext(),"Created ",Toast.LENGTH_SHORT).show();

                        } else {
                            pr.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUp1.this);
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
            String LoginUrlDetails=getLoginUrl();
            RegisterRequest registerRequest = new RegisterRequest(firstname,lastName,college,branch,year,email,mobileNo,sex,LoginUrlDetails,responseListener);
            RequestQueue queue = Volley.newRequestQueue(SignUp1.this);
            registerRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,2, (float) 2.0));
            queue.add(registerRequest);

        }
        return  success;
    }


    @Override
    public void onBackPressed() {
        pr.dismiss();

    }

    //Check branch and accordingly set the register url
    private String getLoginUrl() {
       String url=null;
        switch (branch) {
            case "app": url=Const.urlApp;break;
            case "apc": url=Const.urlApc;break;
            case "apm": url=Const.urlApm;break;
            case "hss": url=Const.urlHss;break;
            case "cer": url=Const.urlCer;break;
            case "che": url=Const.urlChe;break;
            case "civ": url=Const.urlCiv;break;
            case "cse": url=Const.urlCse;break;
            case "eee": url=Const.urlEee;break;
            case "ece": url=Const.urlEce;break;
            case "mec": url=Const.urlMec;break;
            case "met": url=Const.urlMet;break;
            case "min": url=Const.urlMin;break;
            case "mst": url=Const.urlMst;break;
            case "bme": url=Const.urlBme;break;
            case "bce": url=Const.urlBce;break;
            case "phe": url=Const.urlPhe;break;
        }
        return  url;
    }

    private StorageReference mStorage;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){

        mStorage = FirebaseStorage.getInstance().getReference(); //make global
        super.onActivityResult(requestCode, requestCode, data);

        if(requestCode ==2 && resultCode == RESULT_OK){
             pr=new ProgressDialog(this);
            pr.setMessage("Uploading...");
            pr.show();

            Uri uri = data.getData();
            //Keep all images for a specific chat grouped together
            final String imageLocation = "Photos/profile_picture/" + user.getEmail();
            final String imageLocationId = imageLocation + "/" + uri.getLastPathSegment();
            final String uniqueId = UUID.randomUUID().toString();
            final StorageReference filepath = mStorage.child(imageLocation).child(uniqueId + "/profile_pic");
            final String downloadURl = filepath.getPath();
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //create a new message containing this image
                    addImageToProfile(downloadURl);
                    pr.dismiss();
                }
            });
        }

    }

    public void addImageToProfile(final String imageLocation){
        mCurrentUserDatabaseReference
                .child("profilePicLocation").setValue(imageLocation).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        StorageReference storageRef = FirebaseStorage.getInstance()
                                .getReference().child(imageLocation);
                        Glide.with(mView)
                                .using(new FirebaseImageLoader())
                                .load(storageRef)
                                .bitmapTransform(new CropCircleTransformation(mView))
                                .into(profileImg);
                    }
                }
        );

    }

    public void openImageSelector(){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 2);

    }

    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference usrr, root2,root3,userDetails;

    private void createUser(FirebaseUser user) {
        userDetails = root.child("userDetails");
        Map<String, Object> map3 = new HashMap<String, Object>();
        studentDetails =new StudentDetails(firstname,lastName,college,branch,year,email,mobileNo,sex);
        map3.put(user.getEmail(),studentDetails);
        userDetails.updateChildren(map3);

        Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
    }

}
