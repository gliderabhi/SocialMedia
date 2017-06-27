package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListOfResults extends Activity {

    private ListView searchList;
    private String[] firstName;
    private String [] college;
    private String [] lastName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_results);
      searchList=(ListView)findViewById(R.id.SearchList);

        firstName=getIntent().getStringArrayExtra(Const.FirstName);
        college=getIntent().getStringArrayExtra(Const.College);
        lastName=getIntent().getStringArrayExtra(Const.LastName);


        //  Toast.makeText(getApplicationContext(), firstName.toString(), Toast.LENGTH_SHORT).show();
        CustomAdaptor adaptor=new CustomAdaptor(this,getApplicationContext(),firstName,lastName,college);
        searchList.setAdapter(adaptor);

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),firstName[position],Toast.LENGTH_SHORT).show();
            }
        });

    }

}
