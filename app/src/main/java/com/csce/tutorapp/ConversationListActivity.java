package com.csce.tutorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ConversationListActivity extends AppCompatActivity {

    /* list view for the list of conversations */
    private ListView convoList;

    /* array list of conversations populated by Firebase */
    private ArrayList<Conversation> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        convoList = (ListView) findViewById(R.id.convoList);
        conversations = new ArrayList<>();

        final ArrayAdapter<Conversation> adapter = new ArrayAdapter<>(ConversationListActivity.this, android.R.layout.simple_list_item_1, conversations);
        convoList.setAdapter(adapter);

        //get all conversations that this user is a part of
        final DatabaseReference userProfileDb = FirebaseDatabase.getInstance().getReference("users").child(FirebaseUtility.getCurrentFirebaseUser().getUid());
        final DatabaseReference conversationDb = FirebaseDatabase.getInstance().getReference("conversations");
        userProfileDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User currentUser = dataSnapshot.getValue(User.class); //get the user info from Firebase
                if (currentUser != null){
                    conversationDb.addValueEventListener(new ValueEventListener() { //get conversations node from Firebase
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            conversations.clear();

                            for (DataSnapshot child : dataSnapshot.getChildren()){
                                for (int i = 0; i < currentUser.getConversationIDs().size(); i++){
                                    if (TextUtils.equals(child.getKey(), currentUser.getConversationIDs().get(i))){
                                        conversations.add(child.getValue(Conversation.class));
                                    }
                                }
                            }

                            adapter.notifyDataSetInvalidated();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //set what happens when a list view item is clicked
        convoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < conversations.size() && conversations.get(position) != null){
                    Intent convoIntent = new Intent(ConversationListActivity.this, ConversationActivity.class);
                    convoIntent.putExtra("convoID", conversations.get(position).getConvoID());
                    startActivity(convoIntent);
                }
            }
        });
    }
}
