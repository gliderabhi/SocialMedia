package com.example.munnasharma.ChatActivities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.munnasharma.classes.Chat;
import com.example.munnasharma.classes.User;
import com.example.munnasharma.extras.Const;
import com.example.munnasharma.socialmedia.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class AddGroupChat extends AppCompatActivity {


    private static final int GALLERY_INTENT = 1;
    private FirebaseAuth mFirebaseAuth;
    private String currentUserEmail;
    private FirebaseDatabase mFirebaseDatabase;
    private Button createGroup;
    private ImageView groupImage;
    private EditText chatNameField;
    private String chatName,imageLocation,uniqueId;
    private StorageReference mStorage,filepath;
    private ProgressDialog mProgress;
    private Uri uri;
    private FirebaseUser usr;
    private DatabaseReference mGroupChatReference,mGroupChatListReference,mUsersDatabaseReference,mGroupMembers;

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_chat);

        intialize();
    }

    private void intialize(){
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        usr=mFirebaseAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        currentUserEmail = mFirebaseAuth.getCurrentUser().getEmail();
        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(Const.USERS_LOCATION);
        if(currentUserEmail!=null) {
            mGroupChatReference = mFirebaseDatabase.getReference().child(Const.UserDetails + "/"
                    + encodeEmail(currentUserEmail) + "/"
                    + Const.GroupChatName);

        }else{
            Toast.makeText(getApplicationContext(),"Not autheticated",Toast.LENGTH_SHORT).show();
        }
        createGroup=(Button)findViewById(R.id.CreateGroup);
        groupImage=(ImageView)findViewById(R.id.GroupChatIcon);
        chatNameField=(EditText)findViewById(R.id.ChatNameField);

        groupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateGroup();
            }
        });


    }

    //Add listener for on completion of image selection
    public void SelectImage(){
        mProgress = new ProgressDialog(this);
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){

        mStorage = FirebaseStorage.getInstance().getReference(); //make global
        super.onActivityResult(requestCode, requestCode, data);

        if(requestCode ==GALLERY_INTENT && resultCode == RESULT_OK){

             uri = data.getData();
            groupImage.setImageURI(uri);
            //Keep all images for a specific chat grouped together
             imageLocation = "Photos" + "/" + "MID";
            final String imageLocationId = imageLocation + "/" + uri.getLastPathSegment();
             uniqueId = UUID.randomUUID().toString();
                  }

    }
    private void CreateGroup() {

        if (groupImage.getDrawable() != null) {
            createGroup.setClickable(false);
            chatName = chatNameField.getText().toString();

            mGroupChatReference = mFirebaseDatabase.getReference().child(Const.UserDetails + "/"
                    + encodeEmail(currentUserEmail) + "/"
                    + Const.GroupChatName);

            HashMap<String, Object> map = new HashMap<>();
            Chat cht = new Chat();
            cht.setChatName(chatName);
            cht.setUid(mGroupChatReference.getKey());
            map.put(chatName, cht);
            mGroupChatReference.updateChildren(map);

            mGroupMembers = mFirebaseDatabase.getReference(Const.UserDetails + "/"
                    + encodeEmail(currentUserEmail) + "/"
                    + Const.GroupChatName + "/" + chatName);

            User user = new User();
            user.setEmail(usr.getEmail());
            user.setUsername(usr.getDisplayName());
            user.setProfilePicLocation("");

            HashMap<String, Object> map2 = new HashMap<>();
            map2.put(Const.Members, user);
            mGroupMembers.updateChildren(map2);
            filepath = mStorage.child(imageLocation).child(chatName + "/" + uniqueId + "/groupIcon");
            final String downloadURl = filepath.getPath();
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //  mProgress.dismiss();
                    Log.i("DOne", "Added Succesfully");

                    Intent i = new Intent(getApplicationContext(), GroupChatList.class);
                    startActivity(i);
                }
            });
        }else{
            SelectImage();
        }
    }
}

