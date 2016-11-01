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

public class ViewTutorActivity extends AppCompatActivity {

    private TextView userNameTxt, subjectsTxt, aboutTxt;
    private RatingBar studentRating, tutorRating;
    private Button returnBtn, contactBtn, homeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //load the view for the page
        setContentView(R.layout.view_tutor_activity);
        homeBtn = (Button) findViewById(R.id.home_button);
        returnBtn = (Button) findViewById(R.id.return_button);
        contactBtn = (Button) findViewById(R.id.contact_button);
        userNameTxt = (TextView) findViewById(R.id.name_text);
        subjectsTxt = (TextView) findViewById(R.id.subjects_text);
        aboutTxt = (TextView) findViewById(R.id.about_text);
        studentRating = (RatingBar) findViewById(R.id.student_rating_bar);
        tutorRating = (RatingBar) findViewById(R.id.tutor_rating_bar);


    }
}
