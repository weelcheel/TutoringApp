package com.csce.tutorapp;

import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;


/**
 * Created by tylerroper on 10/26/16.
 */

public class HomeScreenActivity extends AppCompatActivity {

    private Button findTutorBtn, profileBtn, messagesBtn, logoutBtn;
    private ImageView profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //load the view for the page
        setContentView(R.layout.home_screen_activity);
        findTutorBtn = (Button) findViewById(R.id.find_tutor_button);
        profileBtn = (Button) findViewById(R.id.profile_button);
        messagesBtn = (Button) findViewById(R.id.messages_button);
        logoutBtn = (Button) findViewById(R.id.logout_button);
        profilePicture = (ImageView) findViewById(R.id.profile_picture);

        createButtonListeners();
    }

    private void createButtonListeners ()
    {
        findTutorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: Switch to find tutor activity
                Intent i = new Intent(getApplicationContext(), FindTutorActivity.class);
                startActivity(i);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: Switch to profile activity
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(i);
            }
        });
        
        messagesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: Switch to messages
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: logout
            }
        });
    }
}
