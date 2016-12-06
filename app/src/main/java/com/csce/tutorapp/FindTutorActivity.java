package com.csce.tutorapp;
// Author: Joel Ashman
// Date Created: 10/22/2016
// Last Update: 10/23/2016
// Description: Gets information from the user to do a search of users that meet the criteria.

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FindTutorActivity extends AppCompatActivity {
    AutoCompleteTextView actvSubject;
    MultiAutoCompleteTextView mactvSubject;
    private static final String TAG = "AnonymousAuth";

    private DatabaseReference refDatabase;
    private DatabaseReference refInstitution;
    private DatabaseReference refSubject;

    private EditText searchSubject;
    private CheckBox restrictInstitution;
    private Button btnCancel, btnSearch;
    private LinearLayout selectSchedule;
    private TextView tvStartTime, tvEndTime;

    // Create adapters for the autocomplete text fields.
    ArrayAdapter<String> adapterSubject;

    BroadcastReceiver brFindTutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Restore last saved instance.
        super.onCreate(savedInstanceState);

        // Load the resources for the layout.
        setContentView(R.layout.find_tutor_activity);
        // initialize references
        refDatabase = FirebaseDatabase.getInstance().getReference();
        refInstitution = refDatabase.child("institution");
        refSubject = refDatabase.child("subject");
        // initialize linear layouts
        selectSchedule = (LinearLayout) findViewById(R.id.linearlayout_schedule);
        // initialize buttons
        btnSearch = (Button) findViewById(R.id.button_search_tutor);
        btnCancel = (Button) findViewById(R.id.button_cancel_search);
        // initialize text views
        mactvSubject = (MultiAutoCompleteTextView) findViewById(R.id.mactv_subject);
        tvStartTime = (TextView) findViewById(R.id.textview_start_time);
        tvEndTime = (TextView) findViewById(R.id.textview_end_time);

        createButtonListeners();
        getSubjectList();

        brFindTutor = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("finish_find_tutor")) {
                    finish();
                }
            }
        };
        registerReceiver(brFindTutor, new IntentFilter("finish_activity"));
    }

    private void createButtonListeners() {
        selectSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ScheduleSelectionActivity.class);
                startActivity(i);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Switch to search results activity
                Intent i = new Intent(getApplicationContext(), SearchResultsActivity.class);
                startActivity(i);
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
        refDatabase.child("institution").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot institutions : dataSnapshot.getChildren()) {
                    subjectList.add(institutions.getValue().toString());
                }
                adapterSubject = new ArrayAdapter<String>(FindTutorActivity.this, android.R.layout.simple_list_item_1, subjectList);
                mactvSubject.setAdapter(adapterSubject);
                mactvSubject.setThreshold(1);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set the times to the current time and the current time +1 hour
        //String test = ;
        tvStartTime.setText(DateFormat.getTimeInstance().format(DateFormat.SHORT));
        tvEndTime.setText(DateFormat.getTimeInstance().format(DateFormat.SHORT));

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            tvStartTime.setText(getIntent().getExtras().getString("Start Time"));
            tvEndTime.setText(getIntent().getExtras().getString("End Time"));
        }
        catch (Exception e) {
            tvStartTime.setText(DateFormat.getTimeInstance().format(DateFormat.SHORT).toString());
            tvEndTime.setText(DateFormat.getTimeInstance().format(DateFormat.SHORT).toString());
        }
    }

    public void onRestart() {
        super.onRestart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(brFindTutor);

    }
}


