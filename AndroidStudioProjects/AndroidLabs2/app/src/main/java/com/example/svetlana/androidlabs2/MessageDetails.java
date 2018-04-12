package com.example.svetlana.androidlabs2;

import android.app.Activity;
import android.os.Bundle;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MessageDetails extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);

        Bundle infoToPass = getIntent().getExtras();
        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MessageFragment mf  =  new MessageFragment();
        mf.setArguments( infoToPass );
        ft.addToBackStack("Any name, not used"); //only undo FT on back button
        ft.replace(  R.id.phone_frame , mf);
        ft.commit();
    }
}
