package com.example.munnasharma.socialmedia;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by MunnaSharma on 6/24/2017.
 */

public class CustomAdaptor  extends BaseAdapter   implements View.OnClickListener {

    /*********** Declare Used Variables *********/
    private Activity activity;
    private Context ctx;
    private String[] name,college;
    private static LayoutInflater inflater=null;
    int i=0;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdaptor(Activity a,Context ctx, String [] name,String [] College) {

        /********** Take passed values **********/
        activity = a;
       this.name=name;
        this.college=College;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(name.length<=0)
            return 1;
        return name.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView UserName,colege;

    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.list_row, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.UserName = (TextView) vi.findViewById(R.id.NameUser);
            holder.colege=(TextView)vi.findViewById(R.id.CollegeName);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(name.length<=0)
        {
            holder.UserName.setText("No Data");
            holder.colege.setText("");

        }
        else
        {
            holder.UserName.setText( name[position]);
            holder.colege.setText(college[position] );

        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }


}