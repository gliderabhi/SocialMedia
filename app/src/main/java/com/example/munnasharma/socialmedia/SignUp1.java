package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class SignUp1 extends Activity  implements  AdapterView.OnItemSelectedListener{

    private Button NextPage;
     private StudentDetails studentDetails;
    private Spinner Branchspinner;
     private CheckBox MaleBox,FemaleBox;
    private String firstname,lastName,college,branch,email,mobileNo,sex,year;
    private EditText FirstName,LastName,College,Email,MobileNo,Year;
     private ProgressDialog pr;
    private String [] Branches={
            "Chemistry(apc)",
            "Physicss(app)",
            "Mathematics(apm)",
            "Humanistic Sciences(hss)",
            "Ceramic(cer)",
            "Chemical(che)",
            "Civil(civ)",
            "Computer(cse)",
            "Electrical(eee)",
            "Electronics(ece)",
            "Mechanical(mec)",
            "Metallurgy(met)",
            "Mining(min)",
            "Pharmaceutics(phe)",
            "Bio_Chemical(bce)",
            "Bio Medical(bme)",
            "Material Science And Technology(mst)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        //Variable initialixzation
        NextPage = (Button) findViewById(R.id.NextPage);
        FirstName = (EditText) findViewById(R.id.FirstNameTextField);
        LastName = (EditText) findViewById(R.id.LastNameTextField);
        College = (EditText) findViewById(R.id.CollegeTextField);
        Email = (EditText) findViewById(R.id.EmailTextField);
        MobileNo = (EditText) findViewById(R.id.MobileNoTextField);
        MaleBox = (CheckBox) findViewById(R.id.MaleCheckBox);
        FemaleBox = (CheckBox) findViewById(R.id.FemaleCheckBox);
         Year=(EditText)findViewById(R.id.YearTextField);
         Branchspinner=(Spinner)findViewById(R.id.BranchSpinner);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Branches);
       Branchspinner.setAdapter(adapter);
       Branchspinner.setOnItemSelectedListener(this);
        // set all fields to be empty
        emptyfields();
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

                                            if(!Year.getText().toString().matches("")){
                                                year=Year.getText().toString();
                                            }else{

                                                Toast.makeText(getApplicationContext(), "Fill in Year of college  ", Toast.LENGTH_SHORT).show();
                                            }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Fill in Mobile No ", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(getApplicationContext(), "Fill in Email ", Toast.LENGTH_SHORT).show();
                                }


                        } else {
                            Toast.makeText(getApplicationContext(), "Fill in College ", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Fill in Last Name ", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Fill in First Name ", Toast.LENGTH_SHORT).show();
                }


                if (MaleBox.isChecked()) {
                    sex = "Male";
                }
                if (FemaleBox.isChecked()) {
                    sex = "Female";
                }
                // what to do next with the values
                //Create student object
                studentDetails=new StudentDetails(firstname,lastName,college,branch,year,email,mobileNo,sex);

                //Send to server add the add row to respective table
                if(DataSend()) {

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
    private void emptyfields(){
        FirstName.setText("");
        LastName.setText("");
        College.setText("");
        MobileNo.setText("");
        Email.setText("");
        Year.setText("");
    }

    public String getEmailDomain() {
        String b = email.substring(email.indexOf('@') + 1);
        return b;
    }

    //methods for spinner
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),"Please Select Some branch",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(position){
            case 0: branch="apc";break;
            case 1: branch="app";break;
            case 2: branch="apm";break;
            case 3: branch="hss";break;
            case 4: branch="cer";break;
            case 5: branch="che";break;
            case 6: branch="civ";break;
            case 7: branch="cse";break;
            case 8: branch="eee";break;
            case 9: branch="ece";break;
            case 10: branch="mec";break;
            case 11: branch="met";break;
            case 12: branch="min";break;
            case 13: branch="phe";break;
            case 14: branch="bce";break;
            case 15: branch="bme";break;
            case 16: branch="mst";break;

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
            builder.setMessage("Please check your internet connection ")
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
                        boolean success = jsonResponse.getBoolean("success");


                        if (success) {
                            pr.dismiss();
                           success=true;

                            Intent i = new Intent(getApplicationContext(), SignUpPage2.class);
                            i.putExtra("email", email);
                            startActivity(i);

                            Toast.makeText(getApplicationContext(),"Created ",Toast.LENGTH_SHORT).show();

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
            case "app": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/app.php";break;
            case "apc": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/apc.php";break;
            case "apm": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/apm.php";break;
            case "hss": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/hss.php";break;
            case "cer": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/cer.php";break;
            case "che": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/che.php";break;
            case "civ": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/civ.php";break;
            case "cse": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/cse.php";break;
            case "eee": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/eee.php";break;
            case "ece": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/ece.php";break;
            case "mec": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/mec.php";break;
            case "met": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/met.php";break;
            case "min": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/min.php";break;
            case "mst": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/mst.php";break;
            case "bme": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/bme.php";break;
            case "bce": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/bce.php";break;
            case "phe": url="http://cazimegliderabhi.000webhostapp.com/RegisterFiles/phe.php";break;
        }
        return  url;
    }


}
