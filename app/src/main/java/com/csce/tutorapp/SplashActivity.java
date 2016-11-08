package com.csce.tutorapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        //check for permissions

        //now see which activity to launch next
        if (FirebaseUtility.getCurrentFirebaseUser() != null)
        {
            //already logged in, so just head to the home screen
            Intent homescreenIntent = new Intent(this, HomeActivity.class);
            startActivity(homescreenIntent);
            finish();
        }
        else
        {
            //need to log in, go to the login screen
            Intent loginscreenIntent = new Intent(this, LoginActivity.class);
            startActivity(loginscreenIntent);
            finish();
        }
    }

}
