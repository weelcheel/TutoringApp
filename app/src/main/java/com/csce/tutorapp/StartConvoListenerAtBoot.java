package com.csce.tutorapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by willh on 12/7/2016.
 */

public class StartConvoListenerAtBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (FirebaseUtility.getCurrentFirebaseUser() != null)
            context.startService(new Intent(ConversationBroadcastReceiver.class.getName()));
    }
}
