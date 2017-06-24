package com.example.munnasharma.socialmedia;

import android.content.Intent;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SearchFields extends Activity implements  AdapterView.OnItemSelectedListener{

    private EditText coll,name;
    private Spinner yr;
    private Button colBtn,YrBtn,NameBtn;
    private String nm,college,year;
    private Intent i;
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

        //Set the spinner
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,Const.years);
        yr.setAdapter(adapter);
        yr.setOnItemSelectedListener(this);
        //Setup listener for the buttons
        colBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                college=coll.getText().toString();
                i=new Intent(getApplicationContext(),ListOfResults.class);
                startActivity(i);
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
}
