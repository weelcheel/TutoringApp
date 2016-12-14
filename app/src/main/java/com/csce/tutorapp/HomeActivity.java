package com.csce.tutorapp;

import android.*;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    public static int LOCATION_PERMISSION_GRANTED = 710;

    /* signout button component */
    private Button signoutButton, conversationsBtn,
            findTutorBtn, profileBtn, activeTutorBtn;

    /* profile name */
    private TextView profileName;

    /* user profile that is currently signed in */
    private User signedInUser;

    private GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_activity);

        Intent serIntent = new Intent(this, ConversationBroadcastReceiver.class);
        startService(serIntent);

        //assign button
        signoutButton = (Button) findViewById(R.id.logout_button);
        profileName = (TextView) findViewById(R.id.profileName);
        conversationsBtn = (Button) findViewById(R.id.convo_button);
        findTutorBtn = (Button) findViewById(R.id.find_tutor_button);
        profileBtn = (Button) findViewById(R.id.profile_button);
        activeTutorBtn = (Button) findViewById(R.id.active_tutor_button);

        //initialize geofire
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("activetutors");
        geoFire = new GeoFire(ref);

        //hide the profile name until set
        profileName.setVisibility(View.INVISIBLE);

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

        //go to test conversation
        conversationsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent convo = new Intent(HomeActivity.this, ConversationListActivity.class);
                startActivity(convo);
            }
        });

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

        activeTutorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activeTutorBtn.setEnabled(false);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("activetutors");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(signedInUser.getID())){
                            geoFire.removeLocation(signedInUser.getID());
                            Toast.makeText(HomeActivity.this, "You are no longer an active tutor.", Toast.LENGTH_LONG).show();
                            activeTutorBtn.setEnabled(true);
                        }
                        else{
                            int permissionCheck = ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
                            if (permissionCheck != PackageManager.PERMISSION_GRANTED)
                            {
                                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_GRANTED);
                                return;
                            }

                            MakeActiveTutor();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        activeTutorBtn.setEnabled(true);
                    }
                });
            }
        });


        final DatabaseReference userProfileDb = FirebaseDatabase.getInstance().getReference("users").child(FirebaseUtility.getCurrentFirebaseUser().getUid());

        userProfileDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                signedInUser = dataSnapshot.getValue(User.class);
                if (signedInUser != null) {
                    Log.d("profile", "Value is: " + signedInUser.getEmail());
                    if (signedInUser.getIsProfileCreated()) {
                        //populate home screen here
                        profileName.setText(getString(R.string.home_welcome) + signedInUser.getFirstName() + " " + signedInUser.getLastName());
                        profileName.setVisibility(View.VISIBLE);
                    } else {
                        Intent userprofileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                        userprofileIntent.putExtra(FirebaseUtility.INTENT_USER_PATH, signedInUser);
                        startActivity(userprofileIntent);
                        finish();
                    }
                } else {
                    User newUser = new User(FirebaseUtility.getCurrentFirebaseUser().getUid());
                    //HashMap<String, User> userMap = new HashMap<>();
                    //userMap.put(newUser.getID(), newUser);
                    FirebaseUtility.updateUser(newUser);

                    Intent userprofileIntent = new Intent(HomeActivity.this, UserProfileActivity.class);
                    userprofileIntent.putExtra(FirebaseUtility.INTENT_USER_PATH, newUser);
                    startActivity(userprofileIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("profile", "Failed to read value.", error.toException());
                profileName.setText("ERROR! :(");
            }
        });
    }

    private void MakeActiveTutor(){
        int permissionCheck = ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_GRANTED);
            return;
        }

        final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            final LocationListener locListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    geoFire.setLocation(signedInUser.getID(), new GeoLocation(location.getLatitude(), location.getLongitude()));

                    Toast.makeText(HomeActivity.this, "You are now an active tutor.", Toast.LENGTH_LONG).show();
                    activeTutorBtn.setEnabled(true);
                    locationManager.removeUpdates(this);
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
            MakeActiveTutor();
    }
}
