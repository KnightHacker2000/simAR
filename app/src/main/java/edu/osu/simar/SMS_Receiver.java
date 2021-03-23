package edu.osu.simar;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class SMS_Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == MainActivity.ACTION_SMS_STATUS) {
            if (getResultCode() == Activity.RESULT_OK) {
                Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "Error sending message", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
