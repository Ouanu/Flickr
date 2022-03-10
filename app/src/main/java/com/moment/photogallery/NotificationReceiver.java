package com.moment.photogallery;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.i(TAG, "receiver broadcast:" + intent.getAction());
        Log.i(TAG, "receiver result:" + getResultCode());

        int requestCode = intent.getIntExtra(PollWorker.Companion.REQUEST_CODE, 0);
        Notification notification = intent.getParcelableExtra(PollWorker.Companion.NOTIFICATION);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(requestCode, notification);

    }


}
