package com.example.munnasharma.ChatActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.munnasharma.socialmedia.FriendsListActivity;
import com.example.munnasharma.socialmedia.R;
import com.example.munnasharma.socialmedia.SearchFields;
import com.example.munnasharma.socialmedia.SignUp1;
import com.example.munnasharma.socialmedia.UserProfile;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.example.munnasharma.extras.*;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import com.example.munnasharma.classes.*;

public class GroupChatList extends AppCompatActivity {

    private Button chatBtn,GroupBtn,ThreadBtn;
    private FloatingActionButton fab;
    private Intent i;
    private EditText SearchBox;
    private ListView GroupList;
    private String temp_key,roomName;
    private ImageView settings;
    private FirebaseAuth FirebaseAuth;
    private FirebaseDatabase FirebaseDatabase;
    private DatabaseReference mGroupChatReference,mUserDatabaseReference;
    private ChildEventListener childEventListener;
    private FirebaseListAdapter mAdaptor;
    private ValueEventListener valueEventListener;
    private String userEmail;
    private ProgressDialog pr;
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_list);

       intialize();
        settings();
        //Setup firebse adaptor
        mAdaptor = new FirebaseListAdapter<Chat>(this, Chat.class, R.layout.chat_item, mGroupChatReference) {
            @Override
            protected void populateView(final View view, Chat chat, final int position) {
                //Log.e("TAG", "");
                //final Friend addFriend = new Friend(chat);


                pr=ProgressDialog.show(GroupChatList.this,"","",true);

                ((TextView) view.findViewById(R.id.messageTextView)).setText(chat.getChatName());

                //Fetch last message from chat
                final DatabaseReference messageRef =
                        FirebaseDatabase.getReference(Const.MESSAGE_LOCATION
                                + "/" + chat.getUid());

                final TextView latestMessage = (TextView)view.findViewById(R.id.nameTextView);
                final ImageView senderPic = (ImageView)view.findViewById(R.id.photoImageView);
                final TextView timeText=(TextView)findViewById(R.id.timeTextView);

                messageRef.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                        Message newMsg = dataSnapshot.getValue(Message.class);
                        latestMessage.setText(newMsg.getSender() + ": " + newMsg.getMessage());
                        timeText.setText(newMsg.getTimestamp());

                        mUserDatabaseReference.child(newMsg.getSender())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User msgSender = dataSnapshot.getValue(User.class);
                                        if(msgSender != null && msgSender.getProfilePicLocation() != null){
                                            StorageReference storageRef = FirebaseStorage.getInstance()
                                                    .getReference().child(msgSender.getProfilePicLocation());
                                            Glide.with(view.getContext())
                                                    .using(new FirebaseImageLoader())
                                                    .load(storageRef)
                                                    .bitmapTransform(new CropCircleTransformation(view.getContext()))
                                                    .into(senderPic);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {}

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {}

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                //Replace this with the most recent message from the chat

                pr.dismiss();
            }

        };

        GroupList.setAdapter(mAdaptor);
        GroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String messageLocation = mAdaptor.getRef(position).toString();

                if(messageLocation != null){
                    Intent intent = new Intent(view.getContext(), ChatMessagesActivity.class);
                    String messageKey = mAdaptor.getRef(position).getKey();
                    intent.putExtra(Const.MESSAGE_ID, messageKey);
                    Chat chatItem = (Chat)mAdaptor.getItem(position);
                    roomName=chatItem.getChatName();
                    intent.putExtra(Const.CHAT_NAME,roomName );
                    startActivity(intent);
                }

                Log.e("TAG", mAdaptor.getRef(position).toString());
            }

        });



       ChangeActivityListener();

        //Floating button to add a new group
        fab=(FloatingActionButton)findViewById(R.id.FloatingButtonAddChat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              i=new Intent(getApplicationContext(),AddGroupChat.class);
                startActivity(i);
            }
        });




}

     private void settings(){
         //setup for settings menu inflator
         settings.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //Creating the instance of PopupMenu
                 PopupMenu popup = new PopupMenu(GroupChatList.this, settings);
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
    private void ChangeActivityListener(){
        //Change Activities
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

            }
        });
        ThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(getApplicationContext(),ThreadActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

    }
    private void intialize(){
        //setup firebase
//Initialize Firebase components
        FirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth = FirebaseAuth.getInstance();
        userEmail=FirebaseAuth.getCurrentUser().getEmail();
        if(userEmail!=null) {
            mGroupChatReference = FirebaseDatabase.getReference().child(Const.UserDetails + "/"
                    + encodeEmail(userEmail) + "/"
                    + Const.GroupChatName);
            mUserDatabaseReference = FirebaseDatabase.getReference()
                    .child(Const.UserDetails);

        }else{
            Toast.makeText(getApplicationContext(),"Not autheticated",Toast.LENGTH_SHORT).show();
        }
        //set buttons for the diff options
        chatBtn=(Button)findViewById(R.id.ChatButtonFragment);
        GroupBtn=(Button)findViewById(R.id.GrouChatFragment);
        ThreadBtn=(Button)findViewById(R.id.ThreadButtonFragment);
        GroupList=(ListView)findViewById(R.id.GroupChatList);
        settings=(ImageView)findViewById(R.id.SettingsButton);
         SearchBox=(EditText)findViewById(R.id.SearchBox);
        SearchBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i=new Intent(getApplicationContext(),SearchFields.class);
            startActivity(i);
            }
        });
    }

    public void signout(){
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        mauth.signOut();
    }

}
