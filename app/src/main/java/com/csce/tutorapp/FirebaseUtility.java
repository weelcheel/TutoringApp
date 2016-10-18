package com.csce.tutorapp;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by willh on 10/17/2016.
 *
 * This class is a helper class with only static functions to communicate with Google Firebase.
 *
 */

public class FirebaseUtility {
    
    /* gets the current Firebase Authenticator */
    public static FirebaseAuth getAuthenticator(){
        return FirebaseAuth.getInstance();
    }

    /* gets the current signed-in user (null if none) from the authenticator and returns it */
    public static FirebaseUser getCurrentFirebaseUser(){
        return (getAuthenticator() != null) ? getAuthenticator().getCurrentUser() : null;
    }

    /* takes a GoogleSignInAccount and gets Firebase credentials from it and returns the authentication task */
    public static Task<AuthResult> authWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        return authTaskFromCredentials(credential);
    }

    /* takes a Facebook access token and returns an authentication task from Firebase */
    public static Task<AuthResult> authWithFacebook(AccessToken fbToken){
        AuthCredential credential = FacebookAuthProvider.getCredential(fbToken.getToken());

        return authTaskFromCredentials(credential);
    }

    /* gets a Firebase authentication task from Firebase credentials */
    private static Task<AuthResult> authTaskFromCredentials(AuthCredential credential){
        //request task from authenticator if it exists
        Task<AuthResult> resultTask = null;
        if (getAuthenticator() != null)
        {
            resultTask = getAuthenticator().signInWithCredential(credential);
        }

        return resultTask;
    }
}
