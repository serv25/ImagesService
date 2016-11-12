package com.example.serega.imagesservice;


import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class DownloadService extends IntentService {

    private static final String FOLDER_TO_SAVE = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
    private NotificationManager notificationManager;

    public DownloadService() {
        super("DownloadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        synchronized (this) {
            try {
                wait(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            URL url = new URL(intent.getStringExtra(MainActivity.URL));
            String imageName = intent.getStringExtra(MainActivity.IMAGE_NAME);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            saveBitmap(bitmap, imageName);

            Intent intentBroadcast = new Intent(MainActivity.BROADCAST_ACTION);
            intentBroadcast.putExtra(MainActivity.BITMAP_URL, FOLDER_TO_SAVE + File.separator + imageName);
            intentBroadcast.putExtra(MainActivity.IMAGE_NAME, imageName);
            sendBroadcast(intentBroadcast);
            sendNotification(imageName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendNotification(String imageName) {

        Context context = getApplicationContext();

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Resources res = getApplicationContext().getResources();
        Notification.Builder builder = new Notification.Builder(getApplicationContext());

        builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_launcher))
                .setTicker(imageName + " is downloaded!")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentTitle("Images Service")
                .setContentText(imageName + " is downloaded!");
        Notification notification = builder.build();

        notificationManager.notify(1, notification);
    }

    private void saveBitmap(Bitmap bitmap, String imageName) {

        try {
            File file = new File(FOLDER_TO_SAVE, imageName);
            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
