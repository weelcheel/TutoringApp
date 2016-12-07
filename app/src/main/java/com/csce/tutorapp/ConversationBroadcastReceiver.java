package com.csce.tutorapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by willh on 12/7/2016.
 */

public class ConversationBroadcastReceiver extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public void onCreate(){
        super.onCreate();

        final ChildEventListener convoListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ConversationMessage message = dataSnapshot.getValue(ConversationMessage.class);
                String m = message != null ? message.getMessage() : "AY";
                Log.v("messages added", m);
                postNotification(m);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                ConversationMessage message = dataSnapshot.getValue(ConversationMessage.class);
                String m = message != null ? message.getMessage() : "AYY";
                Log.v("messages changed", m);
                postNotification(m);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        final DatabaseReference userdb = FirebaseDatabase.getInstance().getReference("users").child(FirebaseUtility.getCurrentFirebaseUser().getUid());
        final DatabaseReference convodb = FirebaseDatabase.getInstance().getReference("conversations");
        userdb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User thisUser = dataSnapshot.getValue(User.class);
                if (thisUser != null){
                    for (String cid : thisUser.getConversationIDs()){
                        convodb.child(cid).child("messages").limitToLast(1).addChildEventListener(convoListener);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void postNotification(String notifString){
        NotificationManager mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = R.mipmap.ic_launcher;
        Context context = getApplicationContext();
        CharSequence contentTitle = "Background" + Math.random();
        Intent notificationIntent = new Intent(context, HomeActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(icon)
                .setContentTitle(contentTitle)
                .setContentText(notifString)
                .setContentIntent(contentIntent);

        mNotificationManager.notify(1, mBuilder.build());
    }
}
