package com.example.munnasharma.ChatActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.munnasharma.socialmedia.SignUpActivity;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import com.example.munnasharma.classes.*;
import com.example.munnasharma.extras.*;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    Intent i;
    private Button chatBtn,GroupBtn,ThreadBtn;
    private FloatingActionButton fab;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mChatDatabaseReference;
    private ChildEventListener mChildEventListener;

    private ListView mChatListView;
    private FirebaseListAdapter mChatAdapter;
    private String mUsername;
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
        setContentView(R.layout.activity_chat);

        //setup firebase details
        initializeScreen();
        //set buttons for the diff options
        chatBtn = (Button) findViewById(R.id.ChatButtonFragment);
        GroupBtn = (Button) findViewById(R.id.GrouChatFragment);
        ThreadBtn = (Button) findViewById(R.id.ThreadButtonFragment);
        settings = (ImageView) findViewById(R.id.SettingsButton);
        menuList = (ListView) findViewById(R.id.MenuList);
        chatRel = (RelativeLayout) findViewById(R.id.ChatRel);

        // remove the option menu if clicked any where else
        chatRel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuList.setVisibility(View.GONE);
            }
        });
        ArrayAdapter adaptor = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, Const.options);
        menuList.setAdapter(adaptor);

        menuList.setVisibility(View.GONE);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuList.setVisibility(View.VISIBLE);
            }
        });
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuList.setVisibility(View.GONE);
                switch (position) {
                    case 0:
                        i = new Intent(getApplicationContext(), SignUp1.class);
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(getApplicationContext(), FriendsListActivity.class);
                        startActivity(i);
                        break;
                    case 2:
                        signout();
                        break;
                }
            }
        });
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        GroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), GroupChatList.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
        ThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i = new Intent(getApplicationContext(), ThreadActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.FloatingButtonAddChat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        //Initialize screen variables
        mChatListView = (ListView) findViewById(R.id.ChatList);

        try {
            mChatAdapter = new FirebaseListAdapter<Chat>(this, Chat.class, R.layout.chat_item, mChatDatabaseReference) {
                @Override
                protected void populateView(final View view, Chat chat, final int position) {
                    //Log.e("TAG", "");
                    //final Friend addFriend = new Friend(chat);
                    ((TextView) view.findViewById(R.id.messageTextView)).setText(chat.getChatName());

                    //Fetch last message from chat
                    final DatabaseReference messageRef =
                            mFirebaseDatabase.getReference(Const.MESSAGE_LOCATION
                                    + "/" + chat.getUid());

                    final TextView latestMessage = (TextView) view.findViewById(R.id.nameTextView);
                    final ImageView senderPic = (ImageView) view.findViewById(R.id.photoImageView);

                    messageRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                            Message newMsg = dataSnapshot.getValue(Message.class);
                            latestMessage.setText(newMsg.getSender() + ": " + newMsg.getMessage());

                            mUserDatabaseReference.child(newMsg.getSender())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User msgSender = dataSnapshot.getValue(User.class);
                                            if (msgSender != null && msgSender.getProfilePicLocation() != null) {
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
                        public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    //Replace this with the most recent message from the chat

                }
            };


            mChatListView.setAdapter(mChatAdapter);

        } catch (Exception e) {

            Log.i("Error",e.toString());
        }
    }

    private void initializeScreen() {
        //Initialize Firebase components
        FirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth = FirebaseAuth.getInstance();
        String userEmail = FirebaseAuth.getCurrentUser().getEmail();
        if (userEmail != null) {
            mChatReference = FirebaseDatabase.getReference().child(Const.UserDetails + "/"
                    + encodeEmail(userEmail) + "/"
                    + Const.CHAT_LOCATION);

        } else {

            Toast.makeText(getApplicationContext(),"User not authenticated",Toast.LENGTH_SHORT).show();
        }
    }

    public void SearchPage(View v){
        i=new Intent(getApplicationContext(), SearchFields.class);
        startActivity(i);
    }
    public void signout(){
        mFirebaseAuth.signOut();
    }
}