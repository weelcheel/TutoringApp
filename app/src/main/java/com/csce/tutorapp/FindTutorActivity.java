package com.csce.tutorapp;

import android.content.SearchRecentSuggestionsProvider;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
/**
 * Created by tylerroper on 10/30/16.
 */

public class FindTutorActivity extends AppCompatActivity {

    private EditText searchSubject;
    private CheckBox restrictInstitution;
    private Button cancelBtn, searchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load the view for the page
        setContentView(R.layout.find_tutor_activity);
        cancelBtn = (Button) findViewById(R.id.cancel_search_button);
        searchBtn = (Button) findViewById(R.id.search_tutor_button);
        searchSubject = (EditText) findViewById(R.id.subject_text_box);
        restrictInstitution = (CheckBox) findViewById(R.id.restrict_institution_box);

        createButtonListeners();
    }

    private void createButtonListeners ()
    {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: Switch to home activity
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //TODO: Switch to search results activity
                Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
                startActivity(i);
            }
        });
    }
}
