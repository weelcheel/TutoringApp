package com.csce.tutorapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class UserProfileActivity extends AppCompatActivity {

    /* create account button */
    private Button createButton;
    private EditText fnText, lnText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        createButton = (Button) findViewById(R.id.create_button);
        fnText = (EditText) findViewById(R.id.firstName);
        lnText = (EditText) findViewById(R.id.lastName);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User currentUser = getIntent().getParcelableExtra(FirebaseUtility.INTENT_USER_PATH);
                if (currentUser != null){
                    currentUser.updateProfile(fnText.getText().toString(), lnText.getText().toString(), "test");
                    FirebaseUtility.updateUser(currentUser);

                    Intent homeIntent = new Intent(UserProfileActivity.this, HomeActivity.class);
                    homeIntent.putExtra(FirebaseUtility.INTENT_USER_PATH, currentUser);
                    startActivity(homeIntent);
                    finish();
                }
            }
        });
    }

}
