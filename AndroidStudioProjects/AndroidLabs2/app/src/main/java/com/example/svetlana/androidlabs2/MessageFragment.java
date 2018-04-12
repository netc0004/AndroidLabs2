package com.example.svetlana.androidlabs2;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class MessageFragment extends Fragment {

    Button deleteButton;
    TextView textMessage;
    TextView textID;

    boolean setIsTablet;
    long ID;
    String text;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle infoToPass = getArguments();
        setIsTablet = (Boolean)infoToPass.get("isTablet");
        ID = (long)infoToPass.get("textID");
        text = (String)infoToPass.get("textMessage");
//        setContentView(R.layout.activity_message_fragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View gui = inflater.inflate(R.layout.fragment, null);
        textMessage = (TextView) gui.findViewById(R.id.textMessage);
        textMessage.setText(text);
        textID = (TextView) gui.findViewById(R.id.textID);
        textID.setText(String.valueOf(ID));

        deleteButton = (Button) gui.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (setIsTablet){
                    ((ChatWindow) getActivity()).deleteMessage(ID);
                } else {
                    Intent delIntent = new Intent(getActivity(), ChatWindow.class);
                    delIntent.putExtra("ID", ID);
                    getActivity().setResult(Activity.RESULT_OK, delIntent);
                    getActivity().finish();
                }
            }
        });

        return gui;
    }
}
