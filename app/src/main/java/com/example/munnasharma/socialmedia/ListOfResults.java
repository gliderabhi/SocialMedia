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
   // private ArrayList<String > college,name;
    private String [] name={};
    private String [] college={};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_results);
      searchList=(ListView)findViewById(R.id.SearchList);
    //   name=getIntent().getStringArrayListExtra("name");
     //   college=getIntent().getStringArrayListExtra("College");

        CustomAdaptor adaptor=new CustomAdaptor(this,getApplicationContext(),name,college);
        searchList.setAdapter(adaptor);

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),name[position],Toast.LENGTH_SHORT).show();
            }
        });

    }
}
