package com.csce.tutorapp;


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

    private EditText searchSubject;
    private CheckBox restrictInstitution;
    private Button cancelBtn, searchBtn;
    private GeoFire geoFire;
    private ArrayList<String> foundTutorKeys;

    public static int LOCATION_PERMISSION_GRANTED = 710;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //load the view for the page
        setContentView(R.layout.find_tutor_activity);
        cancelBtn = (Button) findViewById(R.id.cancel_search_button);
        searchBtn = (Button) findViewById(R.id.search_tutor_button);
        searchSubject = (EditText) findViewById(R.id.subject_text_box);
        restrictInstitution = (CheckBox) findViewById(R.id.restrict_institution_box);

        //initialize geofire
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("activetutors");
        geoFire = new GeoFire(ref);

        foundTutorKeys = new ArrayList<>();

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
