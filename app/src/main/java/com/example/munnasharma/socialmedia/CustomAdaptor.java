package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by MunnaSharma on 6/24/2017.
 */

public class CustomAdaptor  extends BaseAdapter   implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private Context ctx;
    private String[] firstName,college,LatName,email;
    private static LayoutInflater inflater=null;
    int i=0;
    private boolean Frnd;
   private View m;
    /*************  CustomAdapter Constructor *****************/
    public CustomAdaptor(Activity a,Context ctx, String [] name,String[] lastName,String [] College,String [] email) {

        /********** Take passed values **********/
        activity = a;
       this.firstName=name;
        this.LatName=lastName;
        this.college=College;
        this.email=email;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(firstName.length<=0)
            return 1;
        return firstName.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView FirstName,LastName,colege;
        public ImageView AddFrnd,RemoveFrnd,FrndAlready;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.list_row, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

             m=vi;
            holder = new ViewHolder();
            holder.AddFrnd=(ImageView)vi.findViewById(R.id.PositiveBtn);
            holder.RemoveFrnd=(ImageView)vi.findViewById(R.id.NegativeBtn);
            holder.FrndAlready=(ImageView)vi.findViewById(R.id.FrndsAlready);
            holder.FirstName = (TextView) vi.findViewById(R.id.NameUser);
            holder.LastName=(TextView)vi.findViewById(R.id.LastNAmeUser);
            holder.colege=(TextView)vi.findViewById(R.id.CollegeName);

            holder.AddFrnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 addNewFriend(position);
                }
            });
            holder.RemoveFrnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                     removeFriend(position);
                }
            });
            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(firstName.length<=0)
        {
            holder.FirstName.setText("No Data");
            holder.colege.setText("");
            holder.LastName.setText("");

        }
        else
        {
            holder.FirstName.setText( firstName[position]);
            holder.colege.setText(college[position] );
            holder.LastName.setText(LatName[position]);
           CheckUserFrnd(position);
        }

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mFirebaseDatabase;

    public void removeFriend(int position){
        //Get current user logged in by email
        final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
        Log.e(TAG, "User logged in is: " + userLoggedIn);
        final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                + "/" + encodeEmail(userLoggedIn));
        friendsRef.child(encodeEmail(email[position])).removeValue();
    }

    public void addNewFriend(int position){
        //Get current user logged in by email
        final String userLoggedIn = mFirebaseAuth.getCurrentUser().getEmail();
        Log.e(TAG, "User logged in is: " + userLoggedIn);
        //final String newFriendEncodedEmail = encodeEmail(newFriendEmail);
        final DatabaseReference friendsRef = mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                + "/" + encodeEmail(userLoggedIn));
        //Add friends to current users friends list
        friendsRef.child(encodeEmail(email[position])).setValue(email[position]);
    }
    //TODO: Used in multiple places, should probably move to its own class
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public boolean CheckUserFrnd(int position){


      String  mCurrentUserEmail = encodeEmail(mFirebaseAuth.getCurrentUser().getEmail().toString());
        //Check if this user is already your friend
        final DatabaseReference friendRef =
                mFirebaseDatabase.getReference(Const.FRIENDS_LOCATION
                        + "/" + mCurrentUserEmail + "/" + encodeEmail(firstName[position]));

        if(firstName[position].equals(mCurrentUserEmail)){
            m.findViewById(R.id.PositiveBtn).setVisibility(View.GONE);
            m.findViewById(R.id.NegativeBtn).setVisibility(View.GONE);

        }

        friendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null){
                    Log.w(TAG, "User is friend");
                    m.findViewById(R.id.PositiveBtn).setVisibility(View.GONE);
                    m.findViewById(R.id.NegativeBtn).setVisibility(View.VISIBLE);
                 Frnd=true;
                }else{
                    Log.w(TAG, "User is not friend");
                    m.findViewById(R.id.PositiveBtn).setVisibility(View.GONE);
                    m.findViewById(R.id.NegativeBtn).setVisibility(View.VISIBLE);
                  Frnd=false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Toast.makeText(ctx,databaseError.toString(),Toast.LENGTH_SHORT).show();
            }
        });

        return Frnd;
    }
}