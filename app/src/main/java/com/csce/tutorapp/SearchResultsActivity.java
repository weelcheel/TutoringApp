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
import android.widget.ListView;
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
 * Created by tylerroper on 10/29/16.
 */

public class SearchResultsActivity extends AppCompatActivity{

    private Button homeBtn, newSearchBtn;
    private ListView tutorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load the view for the page
        setContentView(R.layout.search_results_activity);
        homeBtn = (Button) findViewById(R.id.home_button);
        newSearchBtn = (Button) findViewById(R.id.new_search_button);
        tutorList = (ListView) findViewById(R.id.tutor_list);

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

        newSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: Switch to find tutor activity
                Intent i = new Intent(getApplicationContext(), FindTutorActivity.class);
                startActivity(i);
            }
        });

        //TODO: Add a modify search button, and associated functionality
    }
}
