package com.skyrealm.jamcloud;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

/**
 * Created by Brockyy on 12/16/2015.
 */

public class GCMListener extends GcmListenerService {


    public void onMessageReceived(String from, Bundle data) {
        String event = data.getString("event");

        if (from.startsWith("/topics/")) {
            // message received from some topic.
        } else {
            // normal downstream message.
            Log.d("Tagged", "Tagged");
            if(event.equals("RESUME"))
            {
                Intent in= new Intent();
                in.setAction("PLAY");
                sendBroadcast(in);
            } else if(event.equals("PAUSE"))
            {
                Intent in= new Intent();
                in.setAction("PAUSE");
                sendBroadcast(in);
            }
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        Log.d("Tagged", "Tagged");
        // [END_EXCLUDE]
    }
    // [END receive_message]

}
