package com.csce.tutorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        FirebaseDatabase.getInstance().getReference(".info/connected").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //check for permissions

                //now see which activity to launch next
                if (FirebaseUtility.getCurrentFirebaseUser() != null)
                {
                    //already logged in, so just head to the home screen
                    Intent homescreenIntent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(homescreenIntent);
                    finish();
                }
                else
                {
                    //need to log in, go to the login screen
                    Intent loginscreenIntent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(loginscreenIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SplashActivity.this, "Check your data connection!", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

}
