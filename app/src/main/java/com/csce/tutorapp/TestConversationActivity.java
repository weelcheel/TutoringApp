package com.csce.tutorapp;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TestConversationActivity extends AppCompatActivity {

    /* current conversation */
    private Conversation currentConversation;

    private ListView convoList;
    private Button sendBtn;
    private EditText sendMessageTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_conversation);

        convoList = (ListView) findViewById(R.id.convoList);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        sendMessageTxt = (EditText) findViewById(R.id.sendMessage);

        currentConversation = new Conversation();

        final ArrayAdapter<ConversationMessage> adapter = new ArrayAdapter<>(TestConversationActivity.this, android.R.layout.simple_list_item_1, currentConversation.getMessages());

        final DatabaseReference userProfileDb = FirebaseDatabase.getInstance().getReference("conversations").child("testconvo");
        userProfileDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Conversation newConvo = dataSnapshot.getValue(Conversation.class);

                if (newConvo != null)
                {
                    currentConversation = newConvo;
                    adapter.clear();
                    adapter.addAll(currentConversation.getMessages());
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
                FirebaseDatabase.getInstance().getReference().child("conversations").child("testconvo").setValue(currentConversation);
            }
        });

        convoList.setAdapter(adapter);
    }

    void addTextView(String msg){
        TextView newMsg = new TextView(this);
        newMsg.setText(msg);
        newMsg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

    }
}
