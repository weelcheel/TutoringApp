package com.csce.tutorapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    /* signout button component */
    private Button signoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //assign button
        signoutButton = (Button) findViewById(R.id.signoutBtn);

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
    }

}
