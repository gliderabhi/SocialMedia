package com.example.munnasharma.socialmedia;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by MunnaSharma on 6/27/2017.
 */

public class ThreadFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View v= inflater.inflate(R.layout.thread_fragment,container,false);
            return v;

        }

}
