package com.csce.tutorapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Created by tylerroper on 10/29/16.
 */

public class ProfileActivity extends AppCompatActivity{

    private TextView userNameTxt, subjectsTxt, aboutTxt;
    private RatingBar studentRating, tutorRating;
    private Button editBtn, homeBtn, msgBtn;
    private User signedInUser = new User(FirebaseUtility.getCurrentFirebaseUser().getUid());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load the view for the page
        setContentView(R.layout.profile_activity);
        homeBtn = (Button) findViewById(R.id.return_home_button);
        editBtn = (Button) findViewById(R.id.edit_profile_button);
        userNameTxt = (TextView) findViewById(R.id.name_text);
        subjectsTxt = (TextView) findViewById(R.id.subjects_text);
        aboutTxt = (TextView) findViewById(R.id.about_text);
        studentRating = (RatingBar) findViewById(R.id.student_rating_bar);
        tutorRating = (RatingBar) findViewById(R.id.tutor_rating_bar);
        msgBtn = (Button) findViewById(R.id.send_msg_btn);

        String inUser = getIntent().getStringExtra("userid");

        final DatabaseReference userProfileDb = FirebaseDatabase.getInstance().getReference("users").child(inUser != null ? inUser : FirebaseUtility.getCurrentFirebaseUser().getUid());
        userProfileDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                signedInUser = dataSnapshot.getValue(User.class);
                if (signedInUser != null) {
                    userNameTxt.setText(signedInUser.getFirstName() + " " + signedInUser.getLastName());

                    for(int i = 0; i < signedInUser.getStudentSubjects().size(); i++) {
                        subjectsTxt.setText(subjectsTxt.getText() + signedInUser.getStudentSubjects().get(i) + " \n");
                    }

                    aboutTxt.setText(signedInUser.getAbout());

                    String t = FirebaseUtility.getCurrentFirebaseUser().getUid();
                    if (signedInUser.getID().equals(t))
                        msgBtn.setEnabled(false);
                    if (!signedInUser.getID().equals(t))
                        editBtn.setEnabled(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("profile", "Failed to read value.", error.toException());
            }
        });

        createButtonListeners();
    }

    private void createButtonListeners ()
    {
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: Switch to home activity
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: switch to edit screen
                Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(i);
            }
        });

        msgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Enter New Conversation Details");

                final EditText nameinput = new EditText(ProfileActivity.this);
                nameinput.setInputType(InputType.TYPE_CLASS_TEXT);
                nameinput.setHint("Conversation Name");

                builder.setView(nameinput);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //create the conversation
                        try {
                            final String convoID = getSHAHash(signedInUser.getEmail()+FirebaseUtility.getCurrentFirebaseUser().getEmail()+System.currentTimeMillis());
                            final DatabaseReference userdb = FirebaseDatabase.getInstance().getReference("users").child(FirebaseUtility.getCurrentFirebaseUser().getUid());

                            userdb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    User thisUser = dataSnapshot.getValue(User.class);
                                    if (thisUser == null)
                                        return;

                                    //create the conversation
                                    Conversation newConversation = new Conversation(signedInUser.getID(), nameinput.getText().toString(), convoID, thisUser.getID());
                                    FirebaseDatabase.getInstance().getReference("conversations").child(convoID).setValue(newConversation);

                                    //now add this id to both users
                                    //this user
                                    thisUser.getConversationIDs().add(convoID);
                                    FirebaseUtility.updateUser(thisUser);

                                    //remote user
                                    signedInUser.getConversationIDs().add(convoID);
                                    //manually update this user since we won't have permission to update the entire user object
                                    FirebaseDatabase.getInstance().getReference("users").child(signedInUser.getID()).child("conversationIDs").setValue(signedInUser.getConversationIDs());

                                    Intent convoIntent = new Intent(ProfileActivity.this, ConversationActivity.class);
                                    convoIntent.putExtra("convoID", convoID);
                                    startActivity(convoIntent);

                                    return;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        catch (NoSuchAlgorithmException e){
                            e.printStackTrace();
                        }
                        catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }
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
