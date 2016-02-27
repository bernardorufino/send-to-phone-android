package com.brufino.sendtophone.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;

public class GcmListenerServiceImpl extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle payload) {
        String title = payload.getString("title");
        String data = payload.getString("data");
        String typeString = payload.getString("type");
        if (typeString == null || title == null || data == null) {
            /* TODO: More details */
            Log.e("S2P", "Malformed message received");
        }
        SentItem.Type type = SentItem.Type.valueOf(typeString);
        String actionType = payload.getString("action_type", "notification");

        SentItem sentItem = new SentItem(title, data, type);

        SentItemsManager manager = SentItemsManager.getInstance();
        manager.load(getApplicationContext());
        manager.insert(sentItem);
        manager.save(getApplicationContext());

        switch (actionType) {
            case "notification":
                createNotification(sentItem);
                break;
            case "open":
                createAppNotification(sentItem);
                open(sentItem);
                break;
        }
    }


    private void open(SentItem sentItem) {
        if (sentItem.getType() != SentItem.Type.URL) {
            /* TODO */
            return;
        }
        Uri url = Uri.parse(sentItem.getData());
        Intent intent = new Intent(Intent.ACTION_VIEW, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void createAppNotification(SentItem sentItem) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_cloud_download_black_24dp)
                .setContentTitle(sentItem.getTitle())
                .setContentText(sentItem.getData())
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }

    private void createNotification(SentItem sentItem) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_cloud_download_black_24dp)
                .setContentTitle(sentItem.getTitle())
                .setContentText(sentItem.getData())
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        if (sentItem.getType() == SentItem.Type.URL) {
            Uri url = Uri.parse(sentItem.getData());
            Intent intent = new Intent(Intent.ACTION_VIEW, url);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}
