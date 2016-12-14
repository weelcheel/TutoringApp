package com.csce.tutorapp;
// Author: Joel Ashman
// Date Created: 10/22/2016
// Last Update: 10/23/2016
// Description: Gets information from the user to do a search of users that meet the criteria.

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.appevents.internal.Constants;
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


import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by tylerroper on 10/30/16.
 */

public class FindTutorActivity extends AppCompatActivity {
    MultiAutoCompleteTextView mactvSubject;
    private static final String TAG = "AnonymousAuth";

    private DatabaseReference refDatabase;
    private CheckBox checkboxMale, checkboxFemale;
    private Button btnCancel, btnSearch, btnSun, btnMon, btnTue, btnWed, btnThu, btnFri, btnSat;
    private static boolean[] boolDaysOfWeek;
    private static boolean [] boolTutorGender;

    // Create adapters for the autocomplete text fields.
    ArrayAdapter<String> adapterSubject;

    private GeoFire geoFire;
    private ArrayList<String> foundTutorKeys;

    //Colors
    int colorBtnAccent;
    int colorBtnNorm;

    public static int LOCATION_PERMISSION_GRANTED = 710;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore last saved instance.
        super.onCreate(savedInstanceState);

        // Load the resources for the layout.
        setContentView(R.layout.find_tutor_activity);

        // initialize references
        refDatabase = FirebaseDatabase.getInstance().getReference();

        // initialize arrays
        boolDaysOfWeek = new boolean[7];
        boolTutorGender = new boolean[2];

        // initialize checkboxes, and ensure related bool array reflects values
        checkboxMale = (CheckBox) findViewById(R.id.checkbox_find_tutor_male);
        checkboxMale.setChecked(true);
        boolTutorGender[0] = !checkboxMale.isChecked();
        checkboxFemale = (CheckBox) findViewById(R.id.checkbox_find_tutor_female);
        checkboxFemale.setChecked(true);
        boolTutorGender[1] = !checkboxFemale.isChecked();


        // initialize buttons
        btnSun = (Button) findViewById(R.id.schedule_dayOfWeek_Sunday_button);
        btnMon = (Button) findViewById(R.id.schedule_dayOfWeek_Monday_button);
        btnTue = (Button) findViewById(R.id.schedule_dayOfWeek_Tuesday_button);
        btnWed = (Button) findViewById(R.id.schedule_dayOfWeek_Wednesday_button);
        btnThu = (Button) findViewById(R.id.schedule_dayOfWeek_Thursday_button);
        btnFri = (Button) findViewById(R.id.schedule_dayOfWeek_Friday_button);
        btnSat = (Button) findViewById(R.id.schedule_dayOfWeek_Saturday_button);
        btnSearch = (Button) findViewById(R.id.button_search_tutor);
        btnCancel = (Button) findViewById(R.id.button_cancel_search);

        // initialize text views
        mactvSubject = (MultiAutoCompleteTextView) findViewById(R.id.mactv_subject);

        //initialize geofire
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("activetutors");
        geoFire = new GeoFire(ref);

        //Colors
        colorBtnAccent = Color.parseColor("#ff4081");
        colorBtnNorm = Color.parseColor("#d6d7d7");

        foundTutorKeys = new ArrayList<>();

        createButtonListeners();
        getSubjectList();
    }

    private void createButtonListeners() {

        btnSun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boolDaysOfWeek[0]){
                    btnSun.setBackgroundColor(colorBtnAccent);
                    boolDaysOfWeek[0] = true;
                }
                else {
                    btnSun.setBackgroundColor(colorBtnNorm);
                    boolDaysOfWeek[0] = false;
                }

            }
        });

        btnMon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boolDaysOfWeek[1]){
                    btnMon.setBackgroundColor(colorBtnAccent);
                    boolDaysOfWeek[1] = true;
                }
                else {
                    btnMon.setBackgroundColor(colorBtnNorm);
                    boolDaysOfWeek[1] = false;
                }

            }
        });

        btnTue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boolDaysOfWeek[2]){
                    btnTue.setBackgroundColor(colorBtnAccent);
                    boolDaysOfWeek[2] = true;
                }
                else {
                    btnTue.setBackgroundColor(colorBtnNorm);
                    boolDaysOfWeek[2] = false;
                }

            }
        });

        btnWed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boolDaysOfWeek[3]){
                    btnWed.setBackgroundColor(colorBtnAccent);
                    boolDaysOfWeek[3] = true;
                }
                else {
                    btnWed.setBackgroundColor(colorBtnNorm);
                    boolDaysOfWeek[3] = false;
                }

            }
        });

        btnThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boolDaysOfWeek[4]){
                    btnThu.setBackgroundColor(colorBtnAccent);
                    boolDaysOfWeek[4] = true;
                }
                else {
                    btnThu.setBackgroundColor(colorBtnNorm);
                    boolDaysOfWeek[4] = false;
                }

            }
        });

        btnFri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boolDaysOfWeek[5]){
                    btnFri.setBackgroundColor(colorBtnAccent);
                    boolDaysOfWeek[5] = true;
                }
                else {
                    btnFri.setBackgroundColor(colorBtnNorm);
                    boolDaysOfWeek[5] = false;
                }

            }
        });

        btnSat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!boolDaysOfWeek[6]){
                    btnSat.setBackgroundColor(colorBtnAccent);
                    boolDaysOfWeek[6] = true;
                }
                else {
                    btnSat.setBackgroundColor(colorBtnNorm);
                    boolDaysOfWeek[6] = false;
                }

            }
        });

        checkboxMale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (checkboxMale.isChecked()) {
                    checkboxMale.setChecked(true);
                    boolTutorGender[0] = true;
                }
                else {
                    checkboxMale.setChecked(false);
                    boolTutorGender[0] = false;
                }
            }
        });

        checkboxFemale.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (checkboxFemale.isChecked()) {
                    checkboxFemale.setChecked(true);
                    boolTutorGender[1] = true;
                }
                else {
                    checkboxFemale.setChecked(false);
                    boolTutorGender[1] = false;
                }
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v){
                int permissionCheck = ContextCompat.checkSelfPermission(FindTutorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(FindTutorActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_GRANTED);
                    return;
                }

                ExecuteTutorSearch();
                //TODO: Switch to search results activity
                //Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
                //startActivity(i);
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getSubjectList() {
        final ArrayList<String> subjectList = new ArrayList<String>();
        refDatabase.child("subject").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot institutions : dataSnapshot.getChildren()) {
                    subjectList.add(institutions.getValue().toString());
                }
                adapterSubject = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_list_item_1, subjectList);
                mactvSubject.setAdapter(adapterSubject);
                mactvSubject.setThreshold(1);
                //mactvSubject.showDropDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    private void ExecuteTutorSearch(){
        int permissionCheck = ContextCompat.checkSelfPermission(FindTutorActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(FindTutorActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_GRANTED);
            return;
        }


        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final LocationListener locListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    final GeoQuery query = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(), location.getLongitude()), 50);
                    final LocationListener t = this;
                    query.addGeoQueryEventListener(new GeoQueryEventListener() {
                        @Override
                        public void onKeyEntered(String key, GeoLocation location) {
                            foundTutorKeys.add(key);
                        }

                        @Override
                        public void onKeyExited(String key) {

                        }

                        @Override
                        public void onKeyMoved(String key, GeoLocation location) {

                        }

                        @Override
                        public void onGeoQueryReady() {
                            if (foundTutorKeys.size() > 0){
                                Toast.makeText(FindTutorActivity.this, "Search completed.", Toast.LENGTH_LONG).show();
                                locationManager.removeUpdates(t);

                                Intent searchResults = new Intent(FindTutorActivity.this, SearchResultsActivity.class);
                                searchResults.putExtra("foundkeys", foundTutorKeys);
                                searchResults.putExtra("gendervalues", boolTutorGender);
                                searchResults.putExtra("daysOfTheWeek", boolDaysOfWeek);
                                startActivity(searchResults);
                            }
                        }

                        @Override
                        public void onGeoQueryError(DatabaseError error) {

                        }
                    });
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };

            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 5000, 10, locListener);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        if (requestCode == LOCATION_PERMISSION_GRANTED)
            ExecuteTutorSearch();
    }
}


