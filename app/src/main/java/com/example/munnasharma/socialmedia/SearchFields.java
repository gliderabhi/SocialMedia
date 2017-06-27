package com.example.munnasharma.socialmedia;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchFields extends Activity implements  AdapterView.OnItemSelectedListener{

    private EditText coll,name;
    private Spinner yr;
    private Button colBtn,YrBtn,NameBtn;
    private String nm,college,year;
    private Intent i;
    private  ArrayList<String> F_name,L_Name,College;
    private ArrayList<StudentDetails> student;
    private ProgressDialog progressDialog;
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

        //Set the spinner
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Const.years);
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
    AlertDialog.Builder builder;
    private void SearchCollege(){
        builder= new AlertDialog.Builder(SearchFields.this);
        boolean network = haveNetworkConnection();
        if (!network) {
            builder.setMessage(Const.checkInternet)
                    .create()
                    .show();
        }
        if (network) {
            progressDialog=ProgressDialog.show(SearchFields.this,"Searching",Const.GettingSearchResults,true);

            final Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    response = response.replaceFirst("<font>.*?</font>", "");
                    int jsonStart = response.indexOf("{");
                    int jsonEnd = response.lastIndexOf("}");

                    if (jsonStart >= 0 && jsonEnd >= 0 && jsonEnd > jsonStart) {
                        response = response.substring(jsonStart, jsonEnd + 1);
                    }
                    Log.i("Result",response);
                    try {
                        student=new ArrayList<StudentDetails>();
                        F_name=new ArrayList<String>();
                        L_Name  = new ArrayList<String>();
                        College=new ArrayList<String>();
                        JSONObject obj= new JSONObject(response);

                        for (int i = 0; i < obj.length(); i++) {
                            F_name.add(obj.getString(Const.FirstName));
                            L_Name.add(obj.getString(Const.LastName));
                            College.add(obj.getString(Const.College));
                            }

                        if(F_name.size()==0){
                            progressDialog.dismiss();
                            builder.setMessage("Sorry No user with this college available... Please Try again with change of selection ")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();

                        }else {
                            progressDialog.dismiss();
                                int i=0;
                            String [] firstName=new String[0];
                            String [] lastName = new String[0];
                            String [] colege=new String[0];

                                for(i=0;i<F_name.size();++i){
                                    firstName[i]=F_name.get(i).toString();
                                }
                                for(i=0;i<L_Name.size();++i){
                                    lastName[i]=L_Name.get(i).toString();
                                }
                                for(i=0;i<College.size();++i){
                                    colege[i]=College.get(i).toString();
                                }

                           // Toast.makeText(getApplicationContext(),F_name.get(1), Toast.LENGTH_SHORT).show();
                         Intent i1=new Intent(getApplicationContext(),ListOfResults.class);
                            i1.putExtra(Const.FirstName,firstName);
                            i1.putExtra(Const.LastName,lastName);
                            i1.putExtra(Const.College,colege);
                            startActivity(i1);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            CollegeSearchRequest collegeSearchRequest = new CollegeSearchRequest(college,responseListener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(collegeSearchRequest);

            collegeSearchRequest.setRetryPolicy(new DefaultRetryPolicy(
                    30000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }
    }

    @Override
    public void onBackPressed() {
        progressDialog.dismiss();
    }
}
