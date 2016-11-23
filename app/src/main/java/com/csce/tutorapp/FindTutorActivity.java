package com.csce.tutorapp;
// Author: Joel Ashman
// Date Created: 10/22/2016
// Last Update: 10/23/2016
// Description: Gets information from the user to do a search of users that meet the criteria.

import android.content.Intent;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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

import java.util.ArrayList;
import java.util.List;

public class FindTutorActivity extends AppCompatActivity {
    MultiAutoCompleteTextView mactvInstitutions;
    private static final String TAG = "AnonymousAuth";

    private DatabaseReference refDatabase;
    private DatabaseReference refInstitution;

    private EditText searchSubject;
    private CheckBox restrictInstitution;
    private Button cancelBtn, searchBtn;


    // Create adapters for the autocomplete text fields.
    ArrayAdapter<String> adapterInstitution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore last saved instance.
        super.onCreate(savedInstanceState);

        // Load the resources for the layout.
        setContentView(R.layout.activity_find_tutor);

        refDatabase = FirebaseDatabase.getInstance().getReference("https://tutoring-app-e2bdd/");
        refInstitution = refDatabase.child("institution");

        adapterInstitution = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
    }

    /**
     * Created by tylerroper on 10/30/16.
     */
    private void createButtonListeners() {
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Switch to home activity
                Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(i);
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Switch to search results activity
                Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
                startActivity(i);
            }
        });
    }

    // tylerroper

    @Override
    public void onStart() {
        super.onStart();

        // Listens for user to attempt to put information in the institution field.
        // Grab data from the database to make suggestions as the user types their institution.
        ValueEventListener mactvInstitutionsFieldListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot institutions : dataSnapshot.getChildren()) {
                    String institution = institutions.getValue(String.class);
                    adapterInstitution.add(institution);
                    Log.d(TAG, "institution:" + institution);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        refInstitution.addValueEventListener(mactvInstitutionsFieldListener);
        refDatabase.child("institution").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot institutions : dataSnapshot.getChildren()) {
                    String institution = institutions.getValue(String.class);
                    adapterInstitution.add(institution);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // Assign the institutions field, finding it by field.
        mactvInstitutions = (MultiAutoCompleteTextView) findViewById(R.id.mactv_institutions);
        // Set the adapter for the institutions field.
        mactvInstitutions.setAdapter(adapterInstitution);

    }
}


