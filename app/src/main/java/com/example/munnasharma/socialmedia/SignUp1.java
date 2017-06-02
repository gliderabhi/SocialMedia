package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class SignUp1 extends Activity  implements  AdapterView.OnItemSelectedListener{

    private Button NextPage;
     private StudentDetails studentDetails;
    private Spinner Branchspinner;
     private CheckBox MaleBox,FemaleBox;
    private String firstname,lastName,college,branch,email,mobileNo,sex,year;
    private EditText FirstName,LastName,College,Email,MobileNo,Year;

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
                                    //Validate Email Address to be of iitbhu
                                    if( getEmailDomain().matches("itbhu.ac.in") || getEmailDomain().matches("iitbhu.ac.in")) {
                                        email = Email.getText().toString();
                                    } else{
                                        Toast.makeText(getApplicationContext(),"Please Enter a valid institute email id ",Toast.LENGTH_SHORT).show();
                                    }
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

                Intent i = new Intent(getApplicationContext(), SignUpPage2.class);
                i.putExtra("StudentDetails", (Parcelable) studentDetails);
                startActivity(i);
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
    private void Setbranch(int position){


    }
    public String getEmailDomain()
    {
        String b = email.substring(email.indexOf('@') + 1);
        return b;
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

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
      Toast.makeText(getApplicationContext(),"Please Select Some branch",Toast.LENGTH_SHORT).show();
    }
}
