package com.csce.tutorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class ConversationListActivity extends AppCompatActivity {

    /* list view for the list of conversations */
    private ListView convoList;

    /* new convo button */
    private FloatingActionButton newConvoBtn;

    /* array list of conversations populated by Firebase */
    private ArrayList<Conversation> conversations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);

        convoList = (ListView) findViewById(R.id.convoList);
        newConvoBtn = (FloatingActionButton) findViewById(R.id.fab);
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

        //new conversation button clicked
        newConvoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConversationListActivity.this);
                builder.setTitle("Enter New Conversation Details");

                LinearLayout inputs = new LinearLayout(ConversationListActivity.this);
                inputs.setOrientation(LinearLayout.VERTICAL);

                final EditText idinput = new EditText(ConversationListActivity.this);
                idinput.setInputType(InputType.TYPE_CLASS_TEXT);
                idinput.setHint("Other User Email");

                final EditText nameinput = new EditText(ConversationListActivity.this);
                nameinput.setInputType(InputType.TYPE_CLASS_TEXT);
                nameinput.setHint("Conversation Name");

                inputs.addView(idinput);
                inputs.addView(nameinput);

                builder.setView(inputs);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final DatabaseReference userdb = FirebaseDatabase.getInstance().getReference("users");

                        userdb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot child : dataSnapshot.getChildren()){
                                    String desiredEmail = idinput.getText().toString();
                                    String thisEmail = (String) child.child("email").getValue();
                                    if (TextUtils.equals(desiredEmail, thisEmail)){
                                        //found the other user, so add both these users to the conversation and then start that activity
                                        try {
                                            //first generate a conversation id
                                            String convoID = getSHAHash(desiredEmail+thisEmail+System.currentTimeMillis());

                                            User remoteUser = child.getValue(User.class);

                                            //create the conversation
                                            Conversation newConversation = new Conversation(remoteUser.getID(), nameinput.getText().toString(), convoID, FirebaseUtility.getCurrentFirebaseUser().getUid());
                                            FirebaseDatabase.getInstance().getReference("conversations").child(convoID).setValue(newConversation);

                                            //now add this id to both users
                                            //this user
                                            User thisUser = dataSnapshot.child(FirebaseUtility.getCurrentFirebaseUser().getUid()).getValue(User.class);
                                            thisUser.getConversationIDs().add(convoID);
                                            FirebaseUtility.updateUser(thisUser);

                                            //remote user
                                            remoteUser.getConversationIDs().add(convoID);
                                            //manually update this user since we won't have permission to update the entire user object
                                            FirebaseDatabase.getInstance().getReference("users").child(remoteUser.getID()).child("conversationIDs").setValue(remoteUser.getConversationIDs());

                                            return;
                                        }
                                        catch (NoSuchAlgorithmException e){
                                            e.printStackTrace();
                                        }
                                        catch (UnsupportedEncodingException e){
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                Toast.makeText(ConversationListActivity.this, "Email doesn't exist!", Toast.LENGTH_LONG).show();
                                return;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                builder.show();
            }
        });
    }

    private static String getSHAHash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(input.getBytes("UTF-8"));

        return new BigInteger(1, crypt.digest()).toString(16);
    }
}
