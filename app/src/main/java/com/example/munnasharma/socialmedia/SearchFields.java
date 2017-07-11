package com.example.munnasharma.socialmedia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.munnasharma.classes.StudentDetails;
import com.example.munnasharma.classes.searchResult;
import com.example.munnasharma.extras.Const;
import com.example.munnasharma.request.CollegeSearchRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchFields extends AppCompatActivity implements  AdapterView.OnItemSelectedListener{

    AlertDialog.Builder builder;
    private EditText coll,name;
    private Spinner yr;
    private Button colBtn,YrBtn,NameBtn;
    private String nm,college,year;
    private Intent i;
    private boolean success;
    private  ArrayList<String> F_name,L_Name,College,Email;
    private ArrayList<StudentDetails> student;
    private ProgressDialog progressDialog;
    private searchResult searchResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_fields);

        //Initialize the variables
        coll=(EditText)findViewById(R.id.CollegeSearch);
        name=(EditText)findViewById(R.id.NameSearch);
        colBtn=(Button)findViewById(R.id.ColegeSearchBtn);
        YrBtn=(Button)findViewById(R.id.YearSearchBtn);
        NameBtn=(Button)findViewById(R.id.NameSearchBtn);
        yr=(Spinner)findViewById(R.id.YearSearch);


        //set progress dialog
        progressDialog=new ProgressDialog(SearchFields.this);
        progressDialog.setMessage("Logging you in ,please wait");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(true);

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                progressDialog.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 60000);
        //Set the spinner
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, Const.years);
        yr.setAdapter(adapter);
        yr.setOnItemSelectedListener(this);
        //Setup listener for the buttons
        colBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                college=coll.getText().toString();
                SearchCollege();
              /*  i=new Intent(getApplicationContext(),ListOfResults.class);
                startActivity(i);*/
            }
        });

        YrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        NameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nm=name.getText().toString();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        year=Const.years[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(getApplicationContext(),Const.fill_year,Toast.LENGTH_SHORT).show();
    }

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

    private void SearchCollege(){
        builder= new AlertDialog.Builder(SearchFields.this);
        boolean network = haveNetworkConnection();
        if (!network) {
         Toast.makeText(getApplicationContext(),Const.checkInternet,Toast.LENGTH_SHORT).show();
        }
        if (network) {
            progressDialog=ProgressDialog.show(SearchFields.this,"Searching",Const.GettingSearchResults,true);

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    progressDialog.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 60000);
            final Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Log.d("Response", response.toString());
                    response = response.replaceFirst("<font>.*?</font>", "");
                    int jsonStart = response.indexOf("{");
                    int jsonEnd = response.lastIndexOf("}");

                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart) {
                        response = response.substring(jsonStart, jsonEnd + 1);
                    } else {

                    }
                    try {
                        student = new ArrayList<StudentDetails>();
                        F_name = new ArrayList<String>();
                        L_Name = new ArrayList<String>();
                        College = new ArrayList<String>();
                        Email = new ArrayList<String>();

                        int i=0;
                        JSONObject jObject = new JSONObject(response);
                        success = jObject.getBoolean(Const.Success);
                        JSONObject JResult;
                        if (success) {

                           String  ur="user";

                            while(jObject.has(ur+ String.valueOf(i)))
                           /* while(jObject.has(Const.user[i]))*/ {
                                JResult = jObject.getJSONObject(Const.user[i]);

                                    F_name.add(JResult.getString(Const.FirstName));
                                    L_Name.add(JResult.getString(Const.LastName));
                                    College.add(JResult.getString(Const.College));
                                    Email.add(JResult.getString(Const.Email));

                                    Log.i("Data",F_name.get(i)+L_Name.get(i)+College.get(i)+Email.get(i));
                                    i++;

                            }
                            Log.d("ChangedText", F_name.toString());
                            if (F_name.size() == 0) {
                                progressDialog.dismiss();
                                builder.setMessage("Sorry No user with this college available... Please Try again with change of selection ")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();

                            } else {
                                 i = 0;
                                String[] firstName = F_name.toArray(new String[F_name.size()]);
                                String[] lastName = L_Name.toArray(new String[L_Name.size()]);
                                String[] colege = College.toArray(new String[College.size()]);
                                String[] email = Email.toArray(new String[Email.size()]);

                               /* for(i=0;i<F_name.size();++i){
                                    firstName[i]=F_name.get(i).toString();
                                }
                                for(i=0;i<L_Name.size();++i){
                                    lastName[i]=L_Name.get(i).toString();
                                }
                                for(i=0;i<College.size();++i){
                                    colege[i]=College.get(i).toString();
                                }*/

                               progressDialog.dismiss();
                                Intent i1 = new Intent(getApplicationContext(), ListOfResults.class);
                                i1.putExtra(Const.FirstName, firstName);
                                i1.putExtra(Const.LastName, lastName);
                                i1.putExtra(Const.College, colege);
                                i1.putExtra(Const.Email, email);
                                startActivity(i1);
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"Failed to retrieve data ",Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                };
            CollegeSearchRequest collegeSearchRequest = new CollegeSearchRequest(college,responseListener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            collegeSearchRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,2, (float) 2.0));
            queue.add(collegeSearchRequest);


        }
    }

    @Override
    public void onBackPressed() {
        progressDialog.dismiss();
    }
}
