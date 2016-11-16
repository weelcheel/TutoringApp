package com.csce.tutorapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    /* signout button component */
    private Button signoutButton, testConvoBtn;

    /* profile name */
    private TextView profileName;

    /* user profile that is currently signed in */
    private User signedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //assign button
        signoutButton = (Button) findViewById(R.id.signoutBtn);
        profileName = (TextView) findViewById(R.id.profileName);
        testConvoBtn = (Button) findViewById(R.id.testConvo);

        //set button text based on sign in status
        if (FirebaseUtility.getCurrentFirebaseUser() == null)
            signoutButton.setText(getResources().getString(R.string.action_sign_in_short));

        //event listener for button
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                signoutButton.setText(getResources().getString(R.string.action_sign_in_short));
                Toast.makeText(HomeActivity.this, "Successfully signed out.", Toast.LENGTH_LONG).show();
                Intent loginScreenIntent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(loginScreenIntent);
                finish();
            }
        });

        //go to test conversation
        testConvoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent convo = new Intent(HomeActivity.this, ConversationListActivity.class);
                startActivity(convo);
            }
        });

        final DatabaseReference userProfileDb = FirebaseDatabase.getInstance().getReference("users").child(FirebaseUtility.getCurrentFirebaseUser().getUid());
        userProfileDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                signedInUser = dataSnapshot.getValue(User.class);
                if (signedInUser != null) {
                    Log.d("profile", "Value is: " + signedInUser.getEmail());
                    if (signedInUser.getIsProfileCreated()) {
                        //populate home screen here
                        profileName.setText(signedInUser.getFirstName() + " " + signedInUser.getLastName());
                    } else {
                        Intent userprofileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                        userprofileIntent.putExtra(FirebaseUtility.INTENT_USER_PATH, signedInUser);
                        startActivity(userprofileIntent);
                        finish();
                    }
                } else {
                    User newUser = new User(FirebaseUtility.getCurrentFirebaseUser().getUid());
                    //HashMap<String, User> userMap = new HashMap<>();
                    //userMap.put(newUser.getID(), newUser);
                    FirebaseUtility.updateUser(newUser);

                    Intent userprofileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                    userprofileIntent.putExtra(FirebaseUtility.INTENT_USER_PATH, newUser);
                    startActivity(userprofileIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("profile", "Failed to read value.", error.toException());
                profileName.setText("ERROR! :(");
            }
        });
    }
}
