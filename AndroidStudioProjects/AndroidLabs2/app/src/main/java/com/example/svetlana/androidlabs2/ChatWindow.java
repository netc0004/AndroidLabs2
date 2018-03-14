package com.example.svetlana.androidlabs2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import java.util.ArrayList;

public class ChatWindow extends Activity {
    ListView listView;
    EditText editText;
    Button sendButton;
    ArrayList<String> messages = new ArrayList<>();

    ChatAdapter messageAdapter;
    ChatDatabaseHelper helper;
    SQLiteDatabase database;
    ContentValues contentValues;
    protected static final String ACTIVITY_NAME = "ChatWindow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        Resources resources = getResources();
        listView = (ListView)findViewById(R.id.listView);
        editText = (EditText)findViewById(R.id.editField);
        sendButton = (Button)findViewById(R.id.sendButton);


       messageAdapter = new ChatAdapter( this );
        listView.setAdapter (messageAdapter);
        helper = new ChatDatabaseHelper(this);
        database = helper.getWritableDatabase();
        contentValues = new ContentValues();



        final ChatAdapter chatAdapter = new ChatAdapter(this);
        listView.setAdapter(chatAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messages.add(editText.getText().toString());
                Log.i("*********Lab5 List", messages.toString());
                messageAdapter.notifyDataSetChanged();
                editText.setText("");
                contentValues.put(ChatDatabaseHelper.KEY_MESSAGE, messages.get(messages.size()-1));
                database.insert(ChatDatabaseHelper.TABLE_NAME,"",contentValues);

            }

        });

        Cursor cursor = database.rawQuery("SELECT * FROM " + ChatDatabaseHelper.TABLE_NAME, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast() ) {
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            messages.add(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            messageAdapter.notifyDataSetChanged();
            cursor.moveToNext();
        }

        Log.i(ACTIVITY_NAME,"Cursor's column count =" + cursor.getColumnCount());

//        for (int i = 0; i < cursor.getColumnCount(); i++){
//            Log.i(ACTIVITY_NAME, cursor.getColumnName(i));
//        }
        cursor.close();
    }

    class ChatAdapter extends ArrayAdapter<String> {

        public ChatAdapter(Context ctx) {
            super(ctx, 0);
        }

        public int getCount () {
            int count = messages.size();
            return count;
        }

        public String getItem(int position) {
            return messages.get(position);
        }

        public View getView(int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = ChatWindow.this.getLayoutInflater();
            View result = null ;
            if(position%2 == 0)
                result = inflater.inflate(R.layout.chat_row_incoming, null);
            else
                result = inflater.inflate(R.layout.chat_row_outgoing, null);

            TextView message = (TextView)result.findViewById(R.id.message_text);
            Log.i("**************Lab5 Adap", message.toString());
            message.setText(getItem(position));
            return result;
        }

        public long getId(int position){
            return position;
        }
    };

    protected void onResume(){
        super.onResume();
//        Log.i(ACTIVITY_NAME, "In onResume()");
    }

    protected void onPause(){
        super.onPause();
//        Log.i(ACTIVITY_NAME, "In onPause()");
    }

    protected void onStop(){
        super.onStop();
//        Log.i(ACTIVITY_NAME, "In onStop()");
    }

    protected void onDestroy(){
        super.onDestroy();
        helper.close();
        Log.i(ACTIVITY_NAME, "In onDestroy");
    }

}




