package com.example.munnasharma.socialmedia;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import com.example.munnasharma.classes.*;
import com.example.munnasharma.extras.*;

public class FriendsListActivity extends AppCompatActivity {

    private final List<String> mUsersFriends = new ArrayList<>();
    private String TAG = "Friends List Activity";
    private ListView mListView;
    private Toolbar mToolBar;
    private FirebaseListAdapter mFriendListAdapter;
    private ValueEventListener mValueEventListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mUserDatabaseReference;
    private DatabaseReference mCurrentUsersFriends;
    private FirebaseAuth mFirebaseAuth;
    private String mCurrentUserEmail;

    private ProgressDialog pr;

    //TODO: Used in multiple places, should probably move to its own class
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friends_activity);
        initializeScreen();

        mToolBar.setTitle("Find new friends");

        showUserList();
    }

    private void showUserList() {
        pr=ProgressDialog.show(FriendsListActivity.this,"Friends","Populating list please wait",true);

        Runnable progressRunnable = new Runnable() {

            @Override
            public void run() {
                pr.cancel();
            }
        };

        Handler pdCanceller = new Handler();
        pdCanceller.postDelayed(progressRunnable, 60000);
        mFriendListAdapter = new FirebaseListAdapter<User>(this, User.class, R.layout.friend_item, mCurrentUsersFriends) {
            @Override
            protected void populateView(final View view, User user, final int position) {
                //Log.e("TAG", user.toString());
                final String email = user.getEmail();
                final String name=user.getUsername();
                final User usr=user;
                //Check if this user is already your friend
                final DatabaseReference friendRef =
                        mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                                + "/" + mCurrentUserEmail + "/" + encodeEmail(email));

                if (email.equals(mCurrentUserEmail)) {
                    view.findViewById(R.id.addFriend).setVisibility(View.GONE);
                    view.findViewById(R.id.removeFriend).setVisibility(View.GONE);
                }

                friendRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            Log.w(TAG, "User is friend");
                            view.findViewById(R.id.addFriend).setVisibility(View.GONE);
                            view.findViewById(R.id.removeFriend).setVisibility(View.VISIBLE);
                        } else {
                            Log.w(TAG, "User is not friend");
                            view.findViewById(R.id.removeFriend).setVisibility(View.GONE);
                            view.findViewById(R.id.addFriend).setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                if (user.getProfilePicLocation() != null && user.getProfilePicLocation().length() > 0) {
                    StorageReference storageRef = FirebaseStorage.getInstance()
                            .getReference().child(user.getProfilePicLocation());
                    Glide.with(view.getContext())
                            .using(new FirebaseImageLoader())
                            .load(storageRef)
                            .bitmapTransform(new CropCircleTransformation(view.getContext()))
                            .into((ImageView) view.findViewById(R.id.photoImageView));
                }

                ((TextView) view.findViewById(R.id.messageTextView)).setText(user.getUsername());
                ((TextView) view.findViewById(R.id.nameTextView)).setText(email);
                (view.findViewById(R.id.addFriend)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.w(TAG, "Clicking row: " + position);
                        Log.w(TAG, "Clicking user: " + email);
                        //Add this user to your friends list, by email
                        addNewFriend(usr);
                    }
                });
                (view.findViewById(R.id.removeFriend)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.w(TAG, "Clicking row: " + position);
                        Log.w(TAG, "Clicking user: " + email);
                        //Add this user to your friends list, by email
                        removeFriend(email);
                    }
                });
            }
        };

        mListView.setAdapter(mFriendListAdapter);

        pr.dismiss();
        mValueEventListener = mUserDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
               /* if (user == null) {

                    return;
                }*/
                mFriendListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeFriend(String  friendEmail) {
        //Get current user logged in by email
        final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
        Log.e(TAG, "User logged in is: " + userLoggedIn);
        final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                + "/" + encodeEmail(userLoggedIn));

        friendsRef.child(encodeEmail(friendEmail)).removeValue();

    }

    private void addNewFriend(User user) {
        //Get current user logged in by email
        try {
            final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
            Log.e(TAG, "User logged in is: " + userLoggedIn);
            //final String newFriendEncodedEmail = encodeEmail(newFriendEmail);
            final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                    + "/" + encodeEmail(userLoggedIn));
            //Add friends to current users friends list
            Map<String, Object> map3 = new HashMap<>();
            user = new User(user.getUsername(), user.getEmail(), user.getProfilePicLocation());
            map3.put(encodeEmail(user.getEmail()), user);
            friendsRef.updateChildren(map3);
        } catch (Exception e) {

            Log.i("Error",e.toString());
            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeScreen() {
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mCurrentUserEmail = encodeEmail(mFirebaseAuth.getCurrentUser().getEmail().toString());
        //Eventually this list will filter out users that are already your friend
        if (mCurrentUserEmail != null) {
            mUserDatabaseReference = mFirebaseDatabase.getReference().child(Const.USERS_LOCATION);
            mCurrentUsersFriends = mFirebaseDatabase.getReference().child(Const.FRIENDS_LOCATION
                    + "/" + encodeEmail(mCurrentUserEmail));

            mListView = (ListView) findViewById(R.id.friendsListView);
            mToolBar = (Toolbar) findViewById(R.id.toolbar);

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
        }else{
            Log.i("Error","No user authenticated");
        }
    }
}
