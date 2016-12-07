package com.csce.tutorapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * Created by tylerroper on 10/29/16.
 */

public class ProfileActivity extends AppCompatActivity{

    private TextView userNameTxt, subjectsTxt, aboutTxt;
    private RatingBar studentRating, tutorRating;
    private Button editBtn, homeBtn;
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
    }

}
