package com.csce.tutorapp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by willh on 10/17/2016.
 */

public class FirebaseMessageService extends FirebaseMessagingService {

    /* handle what to do when the app's service receives a message from the Firebase server */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage){
        Log.d("firebaseNotification", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("firebaseNotification", "Message data payload: " + remoteMessage.getData());
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("firebaseNotification", "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

}
