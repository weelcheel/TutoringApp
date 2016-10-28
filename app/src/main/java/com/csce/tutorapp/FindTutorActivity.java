package com.csce.tutorapp;

// Author: Joel Ashman
// Date Created: 10/22/2016
// Last Update: 10/23/2016
// Description: Gets information from the user to do a search of users that meet the criteria.

import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.MultiAutoCompleteTextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.api.model.StringList;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FindTutorActivity extends AppCompatActivity {
    MultiAutoCompleteTextView mactvInstitutions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore last saved instance.
        super.onCreate(savedInstanceState);

        // Load the resources for the layout.
        setContentView(R.layout.activity_find_tutor);

        // Get an instance of the database. Create reference to the database.
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference refDatabase = database.getReference("https://tutoring-app-e2bdd.firebaseio.com/");

        // Create adapters for the autocomplete text fields.
        final ArrayAdapter<String> adapterInstitution = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        // Listens for user to attempt to put information in the institution field.
        // Grab data from the database to make suggestions as the user types their institution.
        refDatabase.child("institution").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot institutions: dataSnapshot.getChildren()){
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