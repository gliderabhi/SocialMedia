package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.munnasharma.ChatActivities.ChatMessagesActivity;
import com.example.munnasharma.classes.User;
import com.example.munnasharma.extras.Const;
import com.example.munnasharma.extras.CustomAdaptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

public class ListOfResults extends Activity {

    private ListView searchList;
    private String[] firstName;
    private String [] college;
    private String [] lastName;
    private String [] email;
    private TextView noUser;
    private String mCurrentUserEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_results);

        //setup references to view
        searchList = (ListView) findViewById(R.id.SearchList);
        noUser=(TextView)findViewById(R.id.NoUser);
        firstName = getIntent().getStringArrayExtra(Const.FirstName);
        college = getIntent().getStringArrayExtra(Const.College);
        lastName = getIntent().getStringArrayExtra(Const.LastName);
        email=getIntent().getStringArrayExtra(Const.Email);

        if (firstName.length != 0) {
            //  Toast.makeText(getApplicationContext(), firstName.toString(), Toast.LENGTH_SHORT).show();
            CustomAdaptor adaptor = new CustomAdaptor(this, getApplicationContext(), firstName, lastName, college,email);
            searchList.setAdapter(adaptor);

            searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent i=new Intent(getApplicationContext(),ChatMessagesActivity.class);
                    i.putExtra(Const.MESSAGE_ID,"");
                    i.putExtra(Const.CHAT_NAME,firstName[position]+" "+lastName[position]);
                    i.putExtra(Const.Email,email[position]);
                    startActivity(i);
                }
            });

        } else {
            noUser.setText("Sorry No user found");
        }
    }


}
