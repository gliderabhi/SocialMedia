package com.example.munnasharma.ChatActivities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.munnasharma.socialmedia.SignUp1;
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
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String name;
    private ListView GroupList;
    private String temp_key;

    private ImageView settings;
    private ListView menuList;
    private RelativeLayout chatRel;
    private FirebaseAuth FirebaseAuth;
    private FirebaseDatabase FirebaseDatabase;
    private DatabaseReference mGroupChatReference,mUserDatabaseReference;
    private ChildEventListener childEventListener;
    private FirebaseListAdapter mAdaptor;
    private ValueEventListener valueEventListener;

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat_list);

        //setup firebase
//Initialize Firebase components
        FirebaseDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth = FirebaseAuth.getInstance();
        String userEmail=FirebaseAuth.getCurrentUser().getEmail();
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
        menuList=(ListView)findViewById(R.id.MenuList);
        chatRel=(RelativeLayout)findViewById(R.id.GroupRel);

        //Setup firebse adaptor

        mAdaptor = new FirebaseListAdapter<Chat>(this, Chat.class, R.layout.chat_item, mGroupChatReference) {
            @Override
            protected void populateView(final View view, Chat chat, final int position) {
                //Log.e("TAG", "");
                //final Friend addFriend = new Friend(chat);
                ((TextView) view.findViewById(R.id.messageTextView)).setText(chat.getChatName());

                //Fetch last message from chat
                final DatabaseReference messageRef =
                        FirebaseDatabase.getReference(Const.MESSAGE_LOCATION
                                + "/" + chat.getUid());

                final TextView latestMessage = (TextView)view.findViewById(R.id.nameTextView);
                final ImageView senderPic = (ImageView)view.findViewById(R.id.photoImageView);

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

            }
        };


        GroupList.setAdapter(mAdaptor);
        GroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String messageLocation = mAdaptor.getRef(position).toString();

                menuList.setVisibility(View.GONE);
                if(messageLocation != null){
                    Intent intent = new Intent(view.getContext(), ChatMessagesActivity.class);
                    String messageKey = mAdaptor.getRef(position).getKey();
                    intent.putExtra(Const.MESSAGE_ID, messageKey);
                    Chat chatItem = (Chat)mAdaptor.getItem(position);
                    intent.putExtra(Const.CHAT_NAME, chatItem.getChatName());
                    startActivity(intent);
                }

                Log.e("TAG", mAdaptor.getRef(position).toString());
            }

        });


        //array adaptor for menu option
        ArrayAdapter adaptor=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,Const.options);
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
                switch (position){
                    case 0: i=new Intent(getApplicationContext(), SignUp1.class);
                        startActivity(i);break;
                    case 1: i=new Intent(getApplicationContext(), FriendsListActivity.class);
                        startActivity(i);break;
                    case 2: signout();break;
                }
            }
        });

       ChangeActivityListener();

        //Floating button to add a new group
        fab=(FloatingActionButton)findViewById(R.id.FloatingButtonAddChat);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    AddChatRoom();
            }
        });



        /*root.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            try {
                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();

                while (i.hasNext()) {

                    set.add(((DataSnapshot) i.next()).getKey());


                }
                set.remove(String.valueOf("Groups"));
                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }catch (Exception e){
                Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });*/

        GroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            Intent intent = new Intent(getApplicationContext(),GroupChatActivityMssag.class);
            intent.putExtra("room_name",((TextView)view).getText().toString() );
            startActivity(intent);
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

   /* private void AddChatRoom(){
        try{
            root=root.child(Const.Group);
            Map<String, Object> map = new HashMap<String, Object>();
            String roomName = "Hello ";
            if (!roomName.matches("")) {
                map.put(roomName, "");
                root.updateChildren(map);


                root2 = root.child("Permissions");
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put(roomName, "");
                root2.updateChildren(map2);


                root3 = root2.child(roomName);
                Map<String, Object> map3 = new HashMap<String, Object>();
                temp_key = root3.push().getKey();
                map3.put(temp_key, name);
                root3.updateChildren(map3);


            } else {
                Toast.makeText(getApplicationContext(),"please provide a name for group ",Toast.LENGTH_SHORT).show();
            }}catch (Exception e){

        }
    }*/

    public void signout(){
        FirebaseAuth mauth = FirebaseAuth.getInstance();
        mauth.signOut();
    }

}
