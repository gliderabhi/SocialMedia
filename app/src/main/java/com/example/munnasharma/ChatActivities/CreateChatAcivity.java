package com.example.munnasharma.ChatActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.munnasharma.classes.Chat;
import com.example.munnasharma.classes.Message;
import com.example.munnasharma.extras.Const;
import com.example.munnasharma.socialmedia.R;
import com.example.munnasharma.socialmedia.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.munnasharma.ChatActivities.GroupChatList.encodeEmail;

public class CreateChatAcivity extends AppCompatActivity {
    private static final int GALLERY_INTENT=2;
    private static final String LOG_TAG = "Record_log";
    private String messageId,email;
    private TextView mMessageField;
    private ImageButton mSendButton;
    private String chatName;
    private ListView mMessageList;
    private Toolbar mToolBar;
    private String currentUserEmail;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessageDatabaseReference,currentUserChatRef,otherUserChatRef;
    private DatabaseReference mUsersDatabaseReference;
    private FirebaseListAdapter<Message> mMessageListAdapter;
    private FirebaseAuth mFirebaseAuth;
    private ImageButton mphotoPickerButton;
    private StorageReference mStorage;
    private ProgressDialog mProgress;
    private ImageButton mrecordVoiceButton;
    private TextView mRecordLable;
    private MediaRecorder mRecorder;
    private String mFileName = null;
    private FirebaseUser mUsr;
    private ValueEventListener mValueEventListener;
    private String timestamp;
    //Audio Runtime Permissions
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String[] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 200:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                permissionToWriteAccepted  = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) CreateChatAcivity.super.finish();
        if (!permissionToWriteAccepted ) CreateChatAcivity.super.finish();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_acivity);

        messageId= getIntent().getStringExtra(Const.MESSAGE_ID);
        email=getIntent().getStringExtra(Const.Email);
        chatName=getIntent().getStringExtra(Const.CHAT_NAME);


        Log.i("Data",chatName+email+messageId);
        if(messageId == null){
           messageId= mMessageDatabaseReference.push().getKey();
            // replace this.. nav user back to home
            return;
        }

        //Check Permissions at runtime
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }


        initializeScreen();
        mToolBar.setTitle(chatName);
        showMessages();
        addListeners();
        openImageSelector();
        openVoiceRecorder();
    }



    public void openVoiceRecorder(){
        //Implement voice selection
        mrecordVoiceButton =(ImageButton) findViewById(R.id.recordVoiceButton);
        mRecordLable = (TextView) findViewById(R.id.recordLable);

        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/recorded_audio.3gp";

        mrecordVoiceButton.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){

                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){

                    startRecording();

                    mRecordLable.setText("Recording started...");
                }
                else if (motionEvent.getAction() == MotionEvent.ACTION_UP){

                    stopRecording();

                    mRecordLable.setText("Recording stopped...");

                }
                return false;
            }
        });

        //on complete: sendVoice()
    }

    private void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mFileName);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        uploadAudio();
    }

    private void uploadAudio() {

        mStorage = FirebaseStorage.getInstance().getReference();

        mProgress.setMessage("Sending the Audio...");
        mProgress.show();

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                mProgress.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 60000);
        Uri uri = Uri.fromFile(new File(mFileName));
        //Keep all voice for a specific chat grouped together
        final String voiceLocation = "Voice" + "/" + messageId;
        final String voiceLocationId = voiceLocation + "/" + uri.getLastPathSegment();
        final String uniqueId = UUID.randomUUID().toString();
        final StorageReference filepath = mStorage.child(voiceLocation).child(uniqueId + "/audio_message.3gp");
        final String downloadURl = filepath.getPath();

        filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                addVoiceToMessages(downloadURl);
                mProgress.dismiss();
                mRecordLable.setText("Tap and Hold the Phone Button to Record");

            }
        });
    }

    //Add listener for on completion of image selection
    public void openImageSelector(){
        mphotoPickerButton = (ImageButton) findViewById(R.id.photoPickerButton);
        mProgress = new ProgressDialog(this);
        mphotoPickerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_INTENT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data){

        mStorage = FirebaseStorage.getInstance().getReference(); //make global
        super.onActivityResult(requestCode, requestCode, data);

        if(requestCode ==GALLERY_INTENT && resultCode == RESULT_OK){

            mProgress.setMessage("Sending the image...");
            mProgress.show();

            Runnable progressRunnable = new Runnable() {

                @Override
                public void run() {
                    mProgress.cancel();
                }
            };

            Handler pdCanceller = new Handler();
            pdCanceller.postDelayed(progressRunnable, 60000);
            Uri uri = data.getData();
            //Keep all images for a specific chat grouped together
            final String imageLocation = "Photos" + "/" + messageId;
            final String imageLocationId = imageLocation + "/" + uri.getLastPathSegment();
            final String uniqueId = UUID.randomUUID().toString();
            final StorageReference filepath = mStorage.child(imageLocation).child(uniqueId + "/image_message");
            final String downloadURl = filepath.getPath();
            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //create a new message containing this image
                    addImageToMessages(downloadURl);
                    mProgress.dismiss();
                }
            });
        }

    }
    //If voice message add them to Firebase.Storage
    public void addVoiceToMessages(String voiceLocation) {
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        //Create message object with text/voice etc
        String currentUserEmail = mFirebaseAuth.getCurrentUser().getEmail();
        if (currentUserEmail != null) {
            Message message =
                    new Message(currentUserEmail,
                            "Message: Voice Sent", "VOICE", voiceLocation, timestamp);
            //Create HashMap for Pushing
            HashMap<String, Object> messageItemMap = new HashMap<>();
            HashMap<String, Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                    .convertValue(message, Map.class);
            messageItemMap.put("/" + pushKey, messageObj);
            mMessageDatabaseReference.updateChildren(messageItemMap)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mMessageField.setText("");
                        }
                    });
        }
    }


    //Send image messages from here
    public void addImageToMessages(String imageLocation) {
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        String timestamp = dateFormat.format(date);
        //Create message object with text/voice etc
        String currentUserEmail = mFirebaseAuth.getCurrentUser().getEmail();
        if (currentUserEmail != null) {
            Message message =
                    new Message(currentUserEmail,
                            "Message: Image Sent", "IMAGE", imageLocation, timestamp);
            //Create HashMap for Pushing
            HashMap<String, Object> messageItemMap = new HashMap<>();
            HashMap<String, Object> messageObj = (HashMap<String, Object>) new ObjectMapper()
                    .convertValue(message, Map.class);
            messageItemMap.put("/" + pushKey, messageObj);
            mMessageDatabaseReference.updateChildren(messageItemMap)
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            mMessageField.setText("");
                        }
                    });
        }
    }



    public void sendMessage(View view) {
        //final DatabaseReference messageRef = mFirebaseDatabase.getReference(Constants.MESSAGE_LOCATION);
        final DatabaseReference pushRef = mMessageDatabaseReference.push();
        final String pushKey = pushRef.getKey();

        String messageString = mMessageField.getText().toString();

       try {
           SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
           Date date = new Date();
            timestamp = dateFormat.format(date);
       }catch(Exception e){
           Log.i("Error",e.toString());
       }
        //Create message object with text/voice etc
        String currentUserEmail = mFirebaseAuth.getCurrentUser().getEmail();
        if (currentUserEmail != null) {
            try {
                HashMap<String, Object> user1 = new HashMap<>();
                Chat cht = new Chat();
                cht.setChatName(chatName);
                cht.setUid(messageId);
                user1.put(chatName, cht);
                currentUserChatRef.updateChildren(user1);


                user1 = new HashMap<>();
                cht = new Chat();
                cht.setChatName(mUsr.getDisplayName());
                cht.setUid(messageId);
                user1.put(mUsr.getDisplayName(), cht);
                otherUserChatRef.updateChildren(user1);

                Message message = new Message(currentUserEmail, messageString, timestamp);
                //Create HashMap for Pushing
                HashMap<String, Object> messageItemMap = new HashMap<>();
                messageItemMap.put(messageId, message);
                mMessageDatabaseReference.updateChildren(messageItemMap)
                        .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                mMessageField.setText("");
                            }
                        });
            } catch (Exception e) {

                Log.i("Error",e.toString());
            }
        }

    }

    private void showMessages() {
        mMessageListAdapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message_item, mMessageDatabaseReference) {
            @Override
            protected void populateView(final View view, final Message message, final int position) {
                LinearLayout messageLine = (LinearLayout) view.findViewById(R.id.messageLine);
                TextView messgaeText = (TextView) view.findViewById(R.id.messageTextView);
                TextView senderText = (TextView) view.findViewById(R.id.senderTextView);
                //TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);
                final ImageView leftImage = (ImageView) view.findViewById(R.id.leftMessagePic);
                final ImageView rightImage = (ImageView) view.findViewById(R.id.rightMessagePic);
                LinearLayout individMessageLayout = (LinearLayout) view.findViewById(R.id.individMessageLayout);


                try { //set message and sender text
                    messgaeText.setText(message.getMessage());
                    senderText.setText(message.getSender());
                    //If you sent this message, right align
                    String mSender = message.getSender();

                    if (mSender.equals(currentUserEmail)) {
                        //messgaeText.setGravity(Gravity.RIGHT);
                        //senderText.setGravity(Gravity.RIGHT);
                        messageLine.setGravity(Gravity.END);
                        leftImage.setVisibility(View.GONE);
                        rightImage.setVisibility(View.VISIBLE);

                        //profile image back to here
                   /* mUsersDatabaseReference.child(mSender).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User userInfo = dataSnapshot.getValue(User.class);
                            try{
                                if(userInfo != null && userInfo.getProfilePicLocation() != null){
                                    StorageReference storageRef = FirebaseStorage.getInstance()
                                            .getReference().child(userInfo.getProfilePicLocation());
                                    Glide.with(view.getContext())
                                            .using(new FirebaseImageLoader())
                                            .load(storageRef)
                                            .bitmapTransform(new CropCircleTransformation(view.getContext()))
                                            .into(rightImage);
                                }
                            }catch (Exception e){
                                Log.e("ERR", e.toString());
;                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
*/
                        individMessageLayout.setBackgroundResource(R.drawable.roundedmessagescolored);
                        //messgaeText.setBackgroundColor(ResourcesCompat.getColor(getResources(),
                        //       R.color.colorAccent, null));
                    } else if (mSender.equals("System")) {
                        messageLine.setGravity(Gravity.CENTER_HORIZONTAL);
                        leftImage.setVisibility(View.GONE);
                        rightImage.setVisibility(View.GONE);
                    } else {
                        //messgaeText.setGravity(Gravity.LEFT);
                        //senderText.setGravity(Gravity.LEFT);
                        messageLine.setGravity(Gravity.START);
                        leftImage.setVisibility(View.VISIBLE);
                        rightImage.setVisibility(View.GONE);
                        individMessageLayout.setBackgroundResource(R.drawable.roundedmessages);
                        //messgaeText.setBackgroundColor(ResourcesCompat.getColor(getResources(),
                        //       R.color.colorPrimary, null));


                        //profile image back to here
                /* mUsersDatabaseReference.child(mSender).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User userInfo = dataSnapshot.getValue(User.class);
                            if(userInfo != null && userInfo.getProfilePicLocation() != null){
                                try{
                                    StorageReference storageRef = FirebaseStorage.getInstance()
                                            .getReference().child(userInfo.getProfilePicLocation());
                                    Glide.with(view.getContext())
                                            .using(new FirebaseImageLoader())
                                            .load(storageRef)
                                            .bitmapTransform(new CropCircleTransformation(view.getContext()))
                                            .into(leftImage);
                                }catch(Exception e){
                                    Log.e("Err", e.toString());
                                }
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });*/
                    }

                    //If this is multimedia display it
                    final ImageView imageView = (ImageView) view.findViewById(R.id.imageMessage);
                    final ImageButton activateVoiceMsg = (ImageButton) view.findViewById(R.id.voiceMessageButton);
                    if (message.getMultimedia()) {
                        if (message.getContentType().equals("IMAGE")) {
                            StorageReference storageRef = FirebaseStorage.getInstance()
                                    .getReference().child(message.getContentLocation());
                            imageView.setVisibility(View.VISIBLE);
                            activateVoiceMsg.setVisibility(View.GONE);
                            activateVoiceMsg.setImageDrawable(null);
                            //storageRef.getDownloadUrl().addOnCompleteListener(new O)
                            Glide.with(view.getContext())
                                    .using(new FirebaseImageLoader())
                                    .load(storageRef)
                                    .into(imageView);
                        }
                        if (message.getContentType().equals("VOICE")) {
                            //show play button
                            activateVoiceMsg.setVisibility(View.VISIBLE);
                            //hide imageview
                            imageView.setVisibility(View.GONE);
                            imageView.setImageDrawable(null);
                            //line below will reduce padding further on play audio image if necessary
                            //individMessageLayout.setPadding(10,0,0,10);
                            activateVoiceMsg.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(message.getContentLocation());
                                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            playSound(uri);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception exception) {
                                            // Handle any errors
                                        }
                                    });

                                }
                            });
                        }
                    } else {
                        activateVoiceMsg.setVisibility(View.GONE);
                        activateVoiceMsg.setImageDrawable(null);
                        imageView.setVisibility(View.GONE);
                        imageView.setImageDrawable(null);
                    }
                } catch (Exception e) {

                    Log.i("Error",e.toString());
                }
            }
        };
        mMessageList.setAdapter(mMessageListAdapter);

        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Message itemRef=mMessageListAdapter.getItem(position);
                AlertDialog.Builder builder=new AlertDialog.Builder(CreateChatAcivity.this);
                builder.setTitle("")
                        .setMessage(itemRef.getMessage())
                        .create()
                        .show();
            }
        });
    }

    private void playSound(Uri uri){
        MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(uri.toString());
        }catch(Exception e){

            Log.i("Error",e.toString());
        }
        mediaPlayer.prepareAsync();
        //You can show progress dialog here untill it prepared to play
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //Now dismis progress dialog, Media palyer will start playing
                mp.start();
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // dissmiss progress bar here. It will come here when MediaPlayer
                //  is not able to play file. You can show error message to user
                return false;
            }
        });
    }
    public void addListeners(){
        mMessageField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }
    private void initializeScreen() {
        mMessageList = (ListView) findViewById(R.id.messageListView);
        mToolBar = (Toolbar) findViewById(R.id.toolbarChat);
        mMessageField = (TextView)findViewById(R.id.messageToSend);
        mSendButton = (ImageButton)findViewById(R.id.sendButton);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        currentUserEmail = mFirebaseAuth.getCurrentUser().getEmail();
         mUsr=mFirebaseAuth.getCurrentUser();
        currentUserChatRef=mFirebaseDatabase.getReference().child(Const.UserDetails+"/"+encodeEmail(currentUserEmail)+"/"+"chats");
        otherUserChatRef=mFirebaseDatabase.getReference().child(Const.UserDetails+"/"+encodeEmail(email)+"/"+"chats");

        String temp=currentUserChatRef.push().getKey();
        currentUserChatRef=mFirebaseDatabase.getReference().child(Const.UserDetails+"/"+encodeEmail(currentUserEmail)+"/"+"chats"+"/"+temp);
         temp=otherUserChatRef.push().getKey();
        otherUserChatRef=mFirebaseDatabase.getReference().child(Const.UserDetails+"/"+encodeEmail(email)+"/"+"chats"+"/"+temp);

        mUsersDatabaseReference = mFirebaseDatabase.getReference().child(Const.USERS_LOCATION);
        mMessageDatabaseReference = mFirebaseDatabase.getReference().child(Const.MESSAGE_LOCATION+"/"+messageId);

        mToolBar.setTitle(chatName);

        mToolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(), UserProfile.class);
                i.putExtra(Const.Email,email);
                startActivity(i);
            }
        });
        setSupportActionBar(mToolBar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
