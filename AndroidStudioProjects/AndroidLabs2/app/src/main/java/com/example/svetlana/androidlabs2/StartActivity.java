package com.example.svetlana.androidlabs2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity {

    protected static final String ACTIVITY_NAME = "StartActivity";

    TextView messageTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        Log.i(ACTIVITY_NAME, "In onCreate()");
//        Intent i = getIntent();
        Button loginButton = findViewById(R.id.button);
        Button chatButton = findViewById(R.id.chatButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent getResult = new Intent(StartActivity.this, ListItemsActivity.class);
                startActivityForResult(getResult, 50);
            }

        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this, ChatWindow.class);
                startActivity(intent);
                Log.i(ACTIVITY_NAME, "User clicked Start Chat");
            }

        });
    }


    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == 50) {
            Log.i(ACTIVITY_NAME, "Returned to StartActivity.onActivityResult");}

        if (resultCode == Activity.RESULT_OK) {
            String message = data.getStringExtra ("Response");

            int duration= Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(StartActivity.this, message, duration); //this is the ListActivity
            toast.show();
        }
    }

    protected void onResume(){
        super.onResume();
        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    protected void onPause(){
        super.onPause();
        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    protected void onStop(){
        super.onStop();
        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    protected void onDestroy(){
        super.onDestroy();
        Log.i(ACTIVITY_NAME, "In onDestroy()");
    }

}
