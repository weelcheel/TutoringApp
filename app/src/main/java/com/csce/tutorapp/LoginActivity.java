package com.csce.tutorapp;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;

import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener
{

    /* grab the screen components and put them into variables */
    private EditText inputEmail, inputPassword;
    private Button signInBtn, signUpBtn;

    /* api buttons */
    private SignInButton googleSignUpBtn;
    private LoginButton fbSignUpBtn;

    private ProgressBar progressBar;

    /* google API client for google signin */
    private GoogleApiClient googleApiClient;

    /* signin code for Google API */
    private static final int GOOGLE_SIGN_IN = 9001;

    /* facebook API for facebook signin */
    private CallbackManager fbCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialize facebook API
        FacebookSdk.sdkInitialize(getApplicationContext());

        //just try to load the home page if there is already a user logged in
        if (FirebaseUtility.getCurrentFirebaseUser() != null)
        {
            handleSuccessfulAuth();
            return;
        }

        //load the view for the page
        setContentView(R.layout.activity_main);

        //grab the visual components from the layout
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        signInBtn = (Button) findViewById(R.id.sign_in_button);
        signUpBtn = (Button) findViewById(R.id.sign_up_button);
        googleSignUpBtn = (SignInButton) findViewById(R.id.google_signin);
        fbSignUpBtn = (LoginButton) findViewById(R.id.fb_signin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //initialize google sign-in request data for registering with google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.default_web_client_id))
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //initialize facebook sign-in button
        fbCallbackManager = CallbackManager.Factory.create();
        fbSignUpBtn.setReadPermissions("email", "public_profile");

        //just so onCreate doesn't become a massive function
        createButtonListeners();
    }

    private void printKeyHash() {
        // Add code to print out the key hash

        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }

    /* this should only be called from onCreate after the component variables have been set */
    private void createButtonListeners()
    {
        //setup on click listeners for when the user clicks on the buttons on the screen

        //they already have an account and need to sign in
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent createAccountIntent = new Intent(LoginActivity.this, AccountCreationActivity.class);
                startActivity(createAccountIntent);
            }
        });

        //user is creating an account with an email and password
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                //@TODO: implement this for actual app usage. This is to test whether or not authentication through Firebase works.

                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                //validate input email and password
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(getApplicationContext(), "Email or password field cannot be blank!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6)
                {
                    Toast.makeText(getApplicationContext(), "Password must be a minimum of six (6) characters.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //every input is valid, so try to create the user in Firebase
                progressBar.setVisibility(View.VISIBLE);

                handleEmailPassword(email, password);
            }
        });

        //create an account with facebook credentials, so handle facebook callback events
        fbSignUpBtn.registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                //handle a successful Facebook login
                handleFacebookSigninResult(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Hi?", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //create an account with google credentials
        googleSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent googleSigninIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(googleSigninIntent, GOOGLE_SIGN_IN);
            }
        });
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(getApplicationContext(), "Cannot connect to Google. Reason: " + connectionResult, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        //let Facebook handle any results
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);

        //Google signin result captured, so handle it
        if (requestCode == GOOGLE_SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSigninResult(result);
        }
    }

    /* handles an email and password registration */
    private void handleEmailPassword(String email, String password){
        //send the data to Firebase and then handle the result
        Task<AuthResult> accountCreationResult = FirebaseUtility.getAuthenticator().signInWithEmailAndPassword(email, password);
        accountCreationResult.addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Toast.makeText(LoginActivity.this, task.isSuccessful() ? "Successfully created new account." : "Account creation unsuccessful", Toast.LENGTH_SHORT).show();

                if (task.isSuccessful())
                {
                    handleSuccessfulAuth();
                }
            }
        });
    }

    /* handles a Google sign-in result */
    private void handleGoogleSigninResult(GoogleSignInResult result){
        if (result.isSuccess())
        {
            GoogleSignInAccount account = result.getSignInAccount();

            //try to sign in to Firebase with the Google account and handle the task's events
            FirebaseUtility.authWithGoogle(account)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //if the sign-in was successful
                        Toast.makeText(LoginActivity.this, task.isSuccessful() ? "Successfully created new account." : "Account creation unsuccessful", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful())
                        {
                            handleSuccessfulAuth();
                        }
                    }
                });
        }
    }

    /* handle Facebook signin result */
    private void handleFacebookSigninResult(AccessToken fbToken){
        //take a token and use it for Firebase authentication
        FirebaseUtility.authWithFacebook(fbToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if the sign-in was successful
                        Toast.makeText(LoginActivity.this, task.isSuccessful() ? "Successfully created new account." : "Account creation unsuccessful", Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful())
                        {
                            handleSuccessfulAuth();
                        }
                    }
                });
    }

    /* handle a successful Firebase authentication */
    private void handleSuccessfulAuth(){
        //check to see if we should login or go to profile create screen
        final DatabaseReference userProfileDb = FirebaseDatabase.getInstance().getReference("users").child(FirebaseUtility.getCurrentFirebaseUser().getUid());
        userProfileDb.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                User value = dataSnapshot.getValue(User.class);
                if (value != null)
                {
                    Log.d("profile", "Value is: " + value.getEmail());
                    if (value.getIsProfileCreated())
                    {
                        //switch to homescreen
                        Intent homescreenIntent = new Intent(LoginActivity.this, HomeActivity.class);
                        homescreenIntent.putExtra(FirebaseUtility.INTENT_USER_PATH, value);
                        startActivity(homescreenIntent);
                        finish();
                    }
                    else
                    {
                        Intent userprofileIntent = new Intent(LoginActivity.this, UserProfileActivity.class);
                        userprofileIntent.putExtra(FirebaseUtility.INTENT_USER_PATH, value);
                        startActivity(userprofileIntent);
                        finish();
                    }
                }
                else
                {
                    User newUser = new User(FirebaseUtility.getCurrentFirebaseUser().getUid());
                    //HashMap<String, User> userMap = new HashMap<>();
                    //userMap.put(newUser.getID(), newUser);
                    FirebaseUtility.updateUser(newUser);

                    Intent userprofileIntent = new Intent(LoginActivity.this, UserProfileActivity.class);
                    userprofileIntent.putExtra(FirebaseUtility.INTENT_USER_PATH, newUser);
                    startActivity(userprofileIntent);
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("profile", "Failed to read value.", error.toException());
            }
        });
    }
}
