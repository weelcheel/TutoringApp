package com.csce.tutorapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * Created by tylerroper on 12/2/16.
 */

public class EditProfileActivity extends AppCompatActivity{

    private Button saveButton, cancelButton;
    private EditText bioText;
    private CheckBox mathBox, scienceBox, historyBox, englishBox;
    private TextView subject, about;
    private ArrayList<String> subjectArray = new ArrayList<String>();
    private User signedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load the view for the page
        setContentView(R.layout.activity_edit_profile);
        subject = (TextView) findViewById(R.id.subjects_text);
        about = (TextView) findViewById(R.id.bio_text);
        saveButton = (Button) findViewById(R.id.save_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        bioText = (EditText) findViewById(R.id.bio_textEdit);
        mathBox = (CheckBox) findViewById(R.id.math_checkbox);
        scienceBox = (CheckBox) findViewById(R.id.science_checkbox);
        historyBox = (CheckBox) findViewById(R.id.history_checkbox);
        englishBox = (CheckBox) findViewById(R.id.english_checkbox);

        final DatabaseReference userProfileDb = FirebaseDatabase.getInstance().getReference("users").child(FirebaseUtility.getCurrentFirebaseUser().getUid());
        userProfileDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                signedInUser = dataSnapshot.getValue(User.class);
                for(int i = 0; i < signedInUser.getStudentSubjects().size(); i++) {
                    if(signedInUser.getStudentSubjects().get(i).equals("math"))
                        mathBox.setChecked(true);
                    else if(signedInUser.getStudentSubjects().get(i).equals("science"))
                        scienceBox.setChecked(true);
                    else if(signedInUser.getStudentSubjects().get(i).equals("history"))
                        historyBox.setChecked(true);
                    else if(signedInUser.getStudentSubjects().get(i).equals("english"))
                        englishBox.setChecked(true);
                }
                bioText.setText(signedInUser.getAbout());
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
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){

                //TODO: Switch to profile activity after saving data to firebase
                if(mathBox.isChecked())
                    subjectArray.add("math");
                if(scienceBox.isChecked())
                    subjectArray.add("science");
                if(historyBox.isChecked())
                    subjectArray.add("history");
                if(englishBox.isChecked())
                    subjectArray.add("english");


                signedInUser.updateSubjects(subjectArray);
                signedInUser.updateAbout(bioText.getText().toString());
                FirebaseUtility.updateUser(signedInUser);

                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: switch to profile screen
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });
    }
}
