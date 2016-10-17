package com.csce.tutorapp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

}
