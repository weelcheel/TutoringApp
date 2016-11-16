package com.csce.tutorapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {

    /* current conversation */
    private ArrayList<ConversationMessage> currentConversationMessages;
    private Conversation currentConversation;

    private ListView convoList;
    private Button sendBtn;
    private EditText sendMessageTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        convoList = (ListView) findViewById(R.id.convoList);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendMessageTxt = (EditText) findViewById(R.id.sendMessage);

        currentConversationMessages = new ArrayList<>();

        final ArrayAdapter<ConversationMessage> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, currentConversationMessages);
        convoList.setAdapter(adapter);

        final String convoID = getIntent().getStringExtra("convoID");
        final DatabaseReference userProfileDb = FirebaseDatabase.getInstance().getReference("conversations").child(convoID);
        userProfileDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentConversationMessages.clear();
                currentConversation = dataSnapshot.getValue(Conversation.class);
                adapter.clear();

                if (currentConversation != null)
                {
                    currentConversationMessages = (ArrayList<ConversationMessage>) currentConversation.getMessages().clone();
                    adapter.addAll(currentConversationMessages);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConversationMessage newMessage = new ConversationMessage(FirebaseUtility.getCurrentFirebaseUser().getUid(), sendMessageTxt.getText().toString());
                currentConversation.getMessages().add(newMessage);
                sendMessageTxt.getText().clear();
                FirebaseDatabase.getInstance().getReference().child("conversations").child(convoID).setValue(currentConversation);
            }
        });
    }

    void addTextView(String msg){
        TextView newMsg = new TextView(this);
        newMsg.setText(msg);
        newMsg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

    }
}
