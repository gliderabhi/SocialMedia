package com.example.munnasharma.extras;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.munnasharma.classes.User;
import com.example.munnasharma.socialmedia.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import static com.bumptech.glide.gifdecoder.GifHeaderParser.TAG;

/**
 * Created by MunnaSharma on 6/24/2017.
 */

public class CustomAdaptor  extends BaseAdapter   implements View.OnClickListener {

    private static LayoutInflater inflater=null;
    int i=0;
    /*********** Declare Used Variables *********/
    private Activity activity;
    private Context ctx;
    private String[] firstName,college,LatName,email;
   private View m;
    /*************  CustomAdapter Constructor *****************/
    public CustomAdaptor(Activity a,Context ctx, String [] name,String[] lastName,String [] College,String [] email) {

        /********** Take passed values **********/
        activity = a;
        this.ctx=ctx;
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
            holder.FirstName = (TextView) vi.findViewById(R.id.NameUser);
            holder.LastName=(TextView)vi.findViewById(R.id.LastNAmeUser);
            holder.colege=(TextView)vi.findViewById(R.id.CollegeName);


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
        }

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView FirstName,LastName,colege;
    }



}