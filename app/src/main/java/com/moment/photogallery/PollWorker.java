package com.moment.photogallery;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.moment.photogallery.PollWorker.Companion.ACTION_SHOW_NOTIFICATION;
import static com.moment.photogallery.PollWorker.Companion.NOTIFICATION;
import static com.moment.photogallery.PollWorker.Companion.PERM_PRIVATE;
import static com.moment.photogallery.PollWorker.Companion.REQUEST_CODE;

public class PollWorker extends Worker {
    private static final String NOTIFICATION_CHANNEL_ID = "flickr_poll";
    private static final String TAG = "PollWorker";
    private Context context;
    private WorkerParameters workerParams;


    public PollWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.workerParams = workerParams;
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
//        Log.i(TAG, "Work request triggered");
        String query = new QueryPreferences().getStoredQuery(context);
        String lastResultId = new QueryPreferences().getLastResultId(context);
        List<GalleryItem> items;
        if (query.isEmpty()) {
            try {
                items = new FlickrFetchr().fetchPhotosRequest().execute().body().getPhotos().getGalleryItems();
            } catch (IOException e) {
                e.printStackTrace();
                items = Collections.emptyList();
            }
        } else {
            try {
                items = new FlickrFetchr().searchPhotosRequest(query).execute().body().getPhotos().getGalleryItems();
            } catch (IOException e) {
                e.printStackTrace();
                items = Collections.emptyList();
            }
        }

        if (items.isEmpty()) {
            return Result.success();
        }

        String resultId = items.get(0).getId();
        if (resultId == lastResultId) {
            Log.i(TAG, "Got an old result:" + resultId);
        } else {
            Log.i(TAG, "Got a new result:" + resultId);
            QueryPreferences.getINSTANCE().setLastResultId(context, resultId);
            Intent intent = PhotoGalleryActivity.Companion.newIntent(context);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Resources resources = context.getResources();
            Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                    .setTicker(resources.getString(R.string.new_pictures_title))
                    .setSmallIcon(android.R.drawable.ic_menu_report_image)
                    .setContentTitle(resources.getString(R.string.new_pictures_title))
                    .setContentText(resources.getString(R.string.new_pictures_text))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

//            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
//            notificationManagerCompat.notify(0, notification);
//
//            context.sendBroadcast(new Intent(ACTION_SHOW_NOTIFICATION), PERM_PRIVATE);
            showBackgroundNotification(0, notification);
        }

        return Result.success();
    }

    private void showBackgroundNotification(int requestCode, Notification notification) {
        Intent intent = new Intent(ACTION_SHOW_NOTIFICATION);
        intent.putExtra(REQUEST_CODE, requestCode);
        intent.putExtra(NOTIFICATION, notification);

        context.sendOrderedBroadcast(intent, PERM_PRIVATE);
    }

    public static class Companion {
        public static final String ACTION_SHOW_NOTIFICATION = "com.moment.photogallery.SHOW_NOTIFICATION";
        public static final String PERM_PRIVATE = "com.moment.photogallery.PRIVATE";
        public static final String REQUEST_CODE = "REQUEST_CODE";
        public static final String NOTIFICATION = "NOTIFICATION";
    }




}
