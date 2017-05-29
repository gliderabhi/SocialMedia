package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp1 extends Activity {

    private Button NextPage;
    private CheckBox MaleBox,FemaleBox;
    private String firstname,lastName,college,branch,email,mobileNo,sex;
    private EditText FirstName,LastName,College,Branch,Email,MobileNo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);

        //Variable initialixzation
        NextPage=(Button)findViewById(R.id.NextPage);
        FirstName=(EditText)findViewById(R.id.FirstNameTextField);
        LastName=(EditText)findViewById(R.id.LastNameTextField);
        College=(EditText)findViewById(R.id.CollegeTextField);
        Branch=(EditText)findViewById(R.id.BranchTextField);
        Email=(EditText)findViewById(R.id.EmailTextField);
        MobileNo=(EditText)findViewById(R.id.MobileNoTextField);
        MaleBox=(CheckBox)findViewById(R.id.MaleCheckBox);
        FemaleBox=(CheckBox)findViewById(R.id.FemaleCheckBox);

         // set all fields to be empty
         FirstName.setText("");
        LastName.setText("");
        College.setText("");
        Branch.setText("");
        Email.setText("");
        MobileNo.setText("");
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

                firstname=FirstName.getText().toString();
                lastName=LastName.getText().toString();
                college=College.getText().toString();
                branch=Branch.getText().toString();
                mobileNo=MobileNo.getText().toString();
                email=Email.getText().toString();


              // check if any field is empty and ask for refilling

                //needs correction  toast should be only once
           /*      if(!firstname.matches("")){}else{
                     Toast.makeText(getApplicationContext(),"Pease fill all fields",Toast.LENGTH_SHORT).show();emptyfields();}
                if(!lastName.matches("")){}else{Toast.makeText(getApplicationContext(),"Pease fill all fields",Toast.LENGTH_SHORT).show();emptyfields();}
                if(!college.matches("")){}else{Toast.makeText(getApplicationContext(),"Pease fill all fields",Toast.LENGTH_SHORT).show();emptyfields();}
                if(!branch.matches("")){}else{Toast.makeText(getApplicationContext(),"Pease fill all fields",Toast.LENGTH_SHORT).show();emptyfields();}
                if(!mobileNo.matches("")){}else{Toast.makeText(getApplicationContext(),"Pease fill all fields",Toast.LENGTH_SHORT).show();emptyfields();}
                if(!email.matches("")){}else{Toast.makeText(getApplicationContext(),"Pease fill all fields",Toast.LENGTH_SHORT).show();emptyfields();}
*/
                if(MaleBox.isChecked()){
                    sex="1";
                }
                if(FemaleBox.isChecked()){
                    sex="2";
                }
                // what to do next with the values
                Intent i =new Intent (getApplicationContext(),SignUpPage2.class);
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
        Branch.setText("");
        Email.setText("");
    }
}
