package com.example.munnasharma.ChatActivities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.munnasharma.extras.Const;
import com.example.munnasharma.socialmedia.FriendsListActivity;
import com.example.munnasharma.socialmedia.R;
import com.example.munnasharma.socialmedia.SearchFields;
import com.example.munnasharma.socialmedia.UserProfile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ThreadActivity extends AppCompatActivity {

    Intent i;
    private Button chatBtn,GroupBtn,ThreadBtn;
    private FloatingActionButton fab;
    private com.google.firebase.auth.FirebaseAuth mFirebaseAuth;
    private com.google.firebase.database.FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ListView mChatListView;
    private FirebaseListAdapter mChatAdapter;
    private String userEmail;
    private ValueEventListener mValueEventListener;
    private DatabaseReference mUserDatabaseReference;

    private ImageView settings;
    private ListView menuList;
    private RelativeLayout chatRel;
    private FirebaseAuth FirebaseAuth;
    private FirebaseDatabase FirebaseDatabase;
    private DatabaseReference mChatReference;
    private ChildEventListener childEventListener;

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread);

        initializeScreen();
        createMenu();
        activityChanger();
    }

    private void initializeScreen() {
        //set buttons for the diff options
        chatBtn = (Button) findViewById(R.id.ChatButtonFragment);
        GroupBtn = (Button) findViewById(R.id.GrouChatFragment);
        ThreadBtn = (Button) findViewById(R.id.ThreadButtonFragment);
        settings = (ImageView) findViewById(R.id.SettingsButton);
        chatRel = (RelativeLayout) findViewById(R.id.ChatRel);

        //Initialize Firebase components
        FirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth = FirebaseAuth.getInstance();
        userEmail = FirebaseAuth.getCurrentUser().getEmail();
        if (userEmail != null) {
            mChatReference = FirebaseDatabase.getReference().child(Const.UserDetails + "/"
                    + encodeEmail(userEmail) + "/"
                    + Const.CHAT_LOCATION);

        } else {

            Toast.makeText(getApplicationContext(),"User not authenticated",Toast.LENGTH_SHORT).show();
        }
    }

    private void createMenu(){
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(ThreadActivity.this, settings);
                //Inflating the Popup using xml file
                popup.getMenuInflater()
                        .inflate(R.menu.profile_menu, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        String title= (String) item.getTitle();
                        switch (title){
                            case "My Profile": i=new Intent(getApplicationContext(), UserProfile.class);
                                i.putExtra(Const.Email,userEmail);
                                i.putExtra(Const.College,"");
                                i.putExtra(Const.FirstName,"");
                                startActivity(i);break;
                            case "Sign Out":   signout();break;
                            case "Friends": i=new Intent(getApplicationContext(),FriendsListActivity.class);
                                startActivity(i);
                        }
                        return true;
                    }
                });

                popup.show(); //showing popup menu
            }

        });

    }

    public void SearchPage(View v){
        i=new Intent(getApplicationContext(), SearchFields.class);
        startActivity(i);
    }

    public void signout(){
        mFirebaseAuth.signOut();
    }

    private void activityChanger(){
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(getApplicationContext(),ChatActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

        GroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(getApplicationContext(),GroupChatList.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
        ThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        fab = (FloatingActionButton) findViewById(R.id.FloatingButtonAddChat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
