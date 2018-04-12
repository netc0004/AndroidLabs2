package com.example.svetlana.androidlabs2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;


public class ChatWindow extends Activity {
    ListView listView;
    EditText editText;
    Button sendButton;
    FrameLayout frameLayout;
    ArrayList<String> messages = new ArrayList<>();

    ChatAdapter messageAdapter;
    ChatDatabaseHelper helper;
    SQLiteDatabase database;
    ContentValues contentValues;
    Cursor cursor;
    protected static final String ACTIVITY_NAME = "ChatWindow";

    boolean isTablet = false;
    MessageFragment messageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

//        Resources resources = getResources();
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
                messages.add(editText.getText().toString());//
                messageAdapter.notifyDataSetChanged(); //
                editText.setText(""); //
                contentValues.put(ChatDatabaseHelper.KEY_MESSAGE, messages.get(messages.size()-1));
                database.insert(ChatDatabaseHelper.TABLE_NAME,"",contentValues); //

            }

        });

        frameLayout = findViewById(R.id.frame); //new
        if (frameLayout != null)
            isTablet = true;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Store bundle info
                Bundle infoToPass = new Bundle();
                infoToPass.putBoolean("isTablet", isTablet);
                infoToPass.putLong("textID", messageAdapter.getId(position));
                infoToPass.putString("textMessage", messages.get(position));


                if (isTablet){//for a tablet
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    messageFragment  =  new MessageFragment();
                    messageFragment.setArguments( infoToPass );
                    ft.addToBackStack("Any name, not used"); //only undo FT on back button
                    ft.replace( R.id.frame , messageFragment);
                    ft.commit();

                } else {//for a phone
                    Intent phoneIntent = new Intent (ChatWindow.this, MessageDetails.class);
                    phoneIntent.putExtras(infoToPass);
                    startActivityForResult(phoneIntent, 50);
                }
            }
        });




        cursor = database.rawQuery("SELECT * FROM " + ChatDatabaseHelper.TABLE_NAME, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast() ) {
            Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            messages.add(cursor.getString(cursor.getColumnIndex(ChatDatabaseHelper.KEY_MESSAGE)));
            messageAdapter.notifyDataSetChanged();
            cursor.moveToNext();
        }

        Log.i(ACTIVITY_NAME,"Cursor's column count =" + cursor.getColumnCount());
        cursor.close();
        messageAdapter.notifyDataSetChanged();
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
            message.setText(getItem(position));
            return result;
        }

        public long getId(int position){
            cursor = database.query(false, helper.TABLE_NAME, new String []
                    {helper.KEY_ID, helper.KEY_MESSAGE}, null, null, null, null, null, null);
            cursor.moveToPosition(position);
            return cursor.getInt(cursor.getColumnIndex(helper.KEY_ID));
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 50) {
            if (resultCode == Activity.RESULT_OK){
                //update database
                Bundle infoToPass = data.getExtras();
                long ID = (long)infoToPass.get("ID");
                deleteMessage(ID);
            }
        }
    }

    public void deleteMessage(long id){
        database.delete(helper.TABLE_NAME, helper.KEY_ID +"=" +id, null);
        refreshChat();
        if (isTablet)
            getFragmentManager().beginTransaction().remove(messageFragment).commit();
    }

    private void refreshChat(){
        //reset chat lost
        messages.clear();
        cursor = database.query(false, helper.TABLE_NAME, new String [] {
                helper.KEY_ID, helper.KEY_MESSAGE}, null, null, null, null, null, null);
        String message;
        if (cursor.moveToFirst()){
            do {
                message = cursor.getString(cursor.getColumnIndex(helper.KEY_MESSAGE));
                messages.add(message);
                Log.i(ACTIVITY_NAME, "SQL MESSAGE:" + message);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }
        messageAdapter.notifyDataSetChanged();
    }

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




